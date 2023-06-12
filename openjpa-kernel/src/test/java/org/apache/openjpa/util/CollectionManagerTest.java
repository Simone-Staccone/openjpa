package org.apache.openjpa.util;

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
    private boolean isComparatorError = false;


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
        this.elementType = collectionProxyPartition.elementType;
        this.comparator = collectionProxyPartition.comparator;
        this.autoOff = collectionProxyPartition.autoOff;


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
        collectionProxyParameters.add(new CollectionPartition(Collection.class, String.class, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 12;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(Collection.class, String.class, null,false , true));



        collectionProxyParameters.add(new CollectionPartition(Collection.class, null, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int difference = 0;
                if(!o1.equals(o2)){
                    difference = o1.toString().length() - o2.toString().length();
                }
                return difference;
            }
        },false , false));
        collectionProxyParameters.add(new CollectionPartition(Collection.class, null, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 12;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(Collection.class, null, null,false , true));


        collectionProxyParameters.add(new CollectionPartition(String.class, String.class, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int difference = 0;
                if(!o1.equals(o2)){
                    difference = o1.toString().length() - o2.toString().length();
                }
                return difference;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(String.class, String.class, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 12;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(String.class, String.class, null,false , true));


        collectionProxyParameters.add(new CollectionPartition(String.class, null, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int difference = 0;
                if(!o1.equals(o2)){
                    difference = o1.toString().length() - o2.toString().length();
                }
                return difference;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(String.class, null, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 12;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(String.class, null, null,false , true));



        collectionProxyParameters.add(new CollectionPartition(null, String.class, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int difference = 0;
                if(!o1.equals(o2)){
                    difference = o1.toString().length() - o2.toString().length();
                }
                return difference;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(null, String.class, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 12;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(null, String.class, null,false , true));


        collectionProxyParameters.add(new CollectionPartition(null, null, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int difference = 0;
                if(!o1.equals(o2)){
                    difference = o1.toString().length() - o2.toString().length();
                }
                return difference;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(null, null, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 12;
            }
        },false , true));
        collectionProxyParameters.add(new CollectionPartition(null, null, null,false , true));

        return collectionProxyParameters;
    }



    @Test
    public void addProxyCalendarMethodTest() {
        try {
            ProxyManagerImpl newProxyManager = (ProxyManagerImpl) proxyManager;
            Collection proxyCollection1 = newProxyManager.copyCollection((Collection) proxyManager.newCollectionProxy(this.collection, this.elementType,this.comparator, this.autoOff));

            newProxyManager.setAssertAllowedType(true);

            ProxyCollection proxyCollection =
                    (ProxyCollection) newProxyManager.newCollectionProxy(this.collection, this.elementType,this.comparator, this.autoOff);


            if(collection == null){
                Assert.assertNull(proxyCollection);
            }

            if(this.elementType != null){
                proxyCollection.add("test");
                proxyCollection.add("another test");
                Assert.assertThrows(UserException.class, () -> proxyCollection.add(1));
                //Check on copy collection to trigger addProxyCollection()


                proxyCollection1.add("test");
                proxyCollection1.add("another test");

                Assert.assertFalse(proxyCollection.isEmpty());

                if(proxyCollection != null){
                    Assert.assertTrue(proxyCollection.getElementType().equals(proxyCollection1.iterator().next().getClass()));
                }



                if (proxyCollection.stream().max(this.comparator).toString().compareTo("Optional[another test]") != 0) {
                    this.isComparatorError = true;
                    throw new WrongComparatorException(); //Wrong comparator, so exception
                }
                Assert.assertEquals(String.class, proxyCollection.getElementType());

                Assert.assertEquals(2, proxyCollection.size());



            }else{
                //Check if null element type permits multy element collections
                proxyCollection.add(1);
                proxyCollection.add("another test");

                //Check on copy collection to trigger addProxyCollection()


                proxyCollection1.add("test");
                proxyCollection1.add("another test");

                Assert.assertFalse(proxyCollection.isEmpty());


                if (proxyCollection.stream().max(this.comparator).toString().compareTo("Optional[another test]") != 0) {
                    this.isComparatorError = true;
                    throw new WrongComparatorException(); //Wrong comparator, so exception
                }

                Assert.assertEquals(2, proxyCollection.size());

            }

            Assert.assertFalse(expectedException);
        }catch (Exception e){
            if(this.isComparatorError){
                Assert.assertEquals(WrongComparatorException.class,e.getClass());
            }

            if(this.collection == null){
                Collection proxyCollection1 = proxyManager.copyCollection(null); //Check copy with null

                Assert.assertNull(proxyCollection1);

                proxyCollection1 = proxyManager.copyCollection(new ArrayList());
                proxyCollection1.add("test");

                Assert.assertTrue(proxyCollection1.remove("test"));


                //Added after Jacoco report
                try {
                    proxyCollection1 = (Collection) proxyManager.newCollectionProxy(ModifierSample.class, null, null, true);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
            Assert.assertTrue(expectedException);
        }

    }


    class ModifierSample extends ArrayList{
        private String Field;

        public  String getField()
        {
            return "javaTpoint";

        }

        public void setField(String Field) {
            this.Field = Field;
        }
    }


}
