package org.apache.openjpa.util;

import org.apache.openjpa.lib.util.collections.ComparatorChain;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(value = Parameterized.class)
public class CollectionManagerTest {
    private ProxyManager proxyManager;
    private Class collection;
    private Comparator comparator;
    private Class elementType;
    private boolean autoOff;
    private boolean expectedException;



    @Before
    public void setUp() {
        proxyManager = new ProxyManagerImpl();
    }

    @After
    public void tearDown() throws Exception {
        proxyManager = null;
    }

    public CollectionManagerTest(CollectionPartition collectionProxyPartition){
        this.collection = collectionProxyPartition.collection;



        this.expectedException = collectionProxyPartition.expectedException;
    }


    private static final class CollectionPartition {
        private final Class collection;
        private final Comparator comparator;
        private final Class elementType;
        private final boolean autoOff;
        private final boolean expectedException;

        private CollectionPartition(Class collection, Class elementType, Comparator comparator,boolean autoOff,boolean expectedException){
            this.collection = collection;
            this.elementType = elementType;
            this.autoOff = autoOff;
            this.comparator = comparator;
            this.expectedException = expectedException;
        }
    }


    @Parameterized.Parameters
    public static List<CollectionPartition> getObjectType(){
        List<CollectionPartition> collectionProxyParameters = new ArrayList<>();


        collectionProxyParameters.add(new CollectionPartition(Collection.class, String.class, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int difference = 0;
                if(!o1.equals(o2)){
                    difference = o1.toString().length() - o2.toString().length();
                }
                return difference;
            }
        },true , false));

        return collectionProxyParameters;
    }



    @Test
    public void addProxyCalendarMethodTest() {
        try {
            ProxyCollection proxyCollection = (ProxyCollection) proxyManager.newCollectionProxy(this.collection, String.class,this.comparator, true);



            //Check on copy calendar to trigger addProxyCalendarMethods
            Collection proxyCollection1 = proxyManager.copyCollection((Collection) proxyCollection);




            if(collection == null){
                Assert.assertNull(proxyCollection);
            }
            if(proxyCollection != null){
                /*TODO*/

            }

            Assert.assertFalse(expectedException);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(expectedException);
        }

    }
}
