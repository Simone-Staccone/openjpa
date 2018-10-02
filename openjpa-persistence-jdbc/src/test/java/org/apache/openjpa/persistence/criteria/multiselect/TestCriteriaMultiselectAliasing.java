/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.persistence.criteria.multiselect;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.openjpa.persistence.test.SQLListenerTestCase;

public class TestCriteriaMultiselectAliasing extends SQLListenerTestCase {
    CriteriaQuery<Tuple> critQuery;
    String critQueryString;

    String doCB = System.getProperty("doCB");

    @Override
    public void setUp() throws Exception {
        // Only run on Oracle....the asserts at this time look for SQL specific to the
        // way things are generated for Oracle.
        setSupportedDatabases(
            org.apache.openjpa.jdbc.sql.OracleDictionary.class);
        if (isTestsDisabled()) {
            return;
        }

        super.setUp(DimDay.class, FactWorkAssignment.class
            ,"openjpa.Log","SQL=TRACE,Tests=TRACE", "openjpa.ConnectionFactoryProperties",
            "PrintParameters=true, PrettyPrint=true, PrettyPrintLineLength=72"
            );

        critQuery = createCriteriaBuilder();
        critQueryString = critQuery.toString();
        System.out.println("critQueryString = " + critQueryString);
    }

    public void test (){
        if ("true".equals(doCB)){
            this.ttestCriteriaQuery();
        }
        else{
            this.ttestGeneratedCriteriaQueryString();
        }

    }
    /**
     * This method produce wrong query like (note the extra T_DIM_DAY t3):
     * SELECT t0.empl_cnt FROM
     * T_FACT_WORK_ASGNMT t0, T_DIM_DAY t1, T_DIM_DAY t3
     * WHERE (t0.CLNT_OBJ_ID = ? AND t1.ROLL_13_MNTH_IND = ? AND t0.pers_obj_id IN (
     *     SELECT t2.pers_obj_id FROM T_FACT_WORK_ASGNMT t2 WHERE (t2.CLNT_OBJ_ID = ? AND
     *     t3.MNTH_STRT_DAY_KY >= ?)))
     * [params=(String) dummy1, (int) 1, (String) dummy1, (long) 20150201]
     *
     * The correct query should be:
     * SELECT t0.empl_cnt FROM
     * T_FACT_WORK_ASGNMT t0, T_DIM_DAY t1
     * WHERE (t0.CLNT_OBJ_ID = ? AND t1.ROLL_13_MNTH_IND = ? AND t0.pers_obj_id IN (
     *     SELECT t2.pers_obj_id FROM T_FACT_WORK_ASGNMT t2 WHERE (t2.CLNT_OBJ_ID = ? AND
     *     t3.MNTH_STRT_DAY_KY >= ?)))
     * [params=(String) dummy1, (int) 1, (String) dummy1, (long) 20150201]
     *
     */
    public void ttestCriteriaQuery() {
        EntityManager em = emf.createEntityManager();
        resetSQL();
        em.createQuery(critQuery).getResultList();
        assertNotSQL(".*T_DIM_DAY t3.*");
        assertSQL(".*T_DIM_DAY t1.*");
        em.close();
    }

    /**
     * If we execute just the string generated by Criteria Builder, we
     * do not see an extra alias.  We see:
     * SELECT t0.empl_cnt FROM T_FACT_WORK_ASGNMT t0, T_DIM_DAY t1
     * WHERE (t0.CLNT_OBJ_ID = ? AND t1.ROLL_13_MNTH_IND = ? AND
     *   t0.pers_obj_id IN (SELECT t2.pers_obj_id FROM
     *   T_FACT_WORK_ASGNMT t2 WHERE (t2.CLNT_OBJ_ID = ? AND
     *   t1.MNTH_STRT_DAY_KY = ?)))
     */
    public void ttestGeneratedCriteriaQueryString(){
        if (!"true".equals(doCB)){
        EntityManager em = emf.createEntityManager();
        System.out.println("NOT doing CB");
        resetSQL();
        em.createQuery(critQueryString).getResultList();
        assertNotSQL(".*T_DIM_DAY t3.*");
        assertSQL(".*T_DIM_DAY t1.*");
        em.close();
        }
    }

    public CriteriaQuery<Tuple> createCriteriaBuilder(){
            EntityManager em = emf.createEntityManager();

            List<Predicate> predicates = new ArrayList<Predicate>();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<DimDay> day = cq.from(DimDay.class);

            Root<FactWorkAssignment> wa =  cq.from(FactWorkAssignment.class);

            predicates.add(cb.equal(wa.get(FactWorkAssignment_.orgOID), "dummy1"));
            predicates.add(cb.equal(day.get(DimDay_.roll13MonthInd), 1));

            Subquery<String> subQuery = cq.subquery(String.class);

            Root<FactWorkAssignment> wa1 = subQuery.from(FactWorkAssignment.class);

            subQuery.select(wa1.get(FactWorkAssignment_.personObjId));
            List<Predicate> subQueryPredicates = new ArrayList<Predicate>();
            subQueryPredicates.add(cb.equal(wa1.get(FactWorkAssignment_.orgOID), "dummy1"));

            //Removing this seem to "fix" the issue....I think the fact that we use 'day' from
            //the 'outer' query has an affect....is it OK to use 'day' from the outer query??   I'm
            //assuming so since 'testGeneratedCriteriaQueryString' generates the expected SQL.
//            subQueryPredicates.add(cb.greaterThanOrEqualTo(day.get(DimDay_.monthStrtDate), new Long(20150201L)));
            subQueryPredicates.add(cb.equal(day.get(DimDay_.monthStrtDate), new Long(20150201L)));

            //Doing this places the 'T_DIM_DAY t3' in the 'inner'/sub query.  Is this the proper solution??  Or just a
            //hacky work around?
            //Root<DimDay> day2 =  subQuery.from(DimDay.class);
            //subQueryPredicates.add(cb.greaterThanOrEqualTo(day2.get(DimDay_.monthStrtDate), new Long(20150201L)));

            subQuery.where(subQueryPredicates.toArray(new Predicate[] {}));

            Predicate predicate = wa.get(FactWorkAssignment_.personObjId).in(subQuery);

            predicates.add(predicate);

            List<Selection<?>> selections = new ArrayList<Selection<?>>();

            Expression<Integer> expHC = wa.get(FactWorkAssignment_.employeeCount);
            selections.add(expHC);

            cq.multiselect(selections).where(predicates.toArray(new Predicate[] {}));

            return cq;
        }
}
