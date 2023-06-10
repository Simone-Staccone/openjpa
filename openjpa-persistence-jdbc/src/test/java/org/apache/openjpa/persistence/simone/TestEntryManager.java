package org.apache.openjpa.persistence.simone;

import org.apache.openjpa.persistence.datacache.common.apps.PObject;
import org.apache.openjpa.persistence.test.SingleEMFTestCase;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

public class TestEntryManager extends SingleEMFTestCase {

    @Before
    public void setUp(){
        super.setUp(PObject.class);
    }

    @Test
    public void testConstructor(){
        Map propMap = new HashMap();
        EntityManager em = emf.createEntityManager(propMap);
    }
}
