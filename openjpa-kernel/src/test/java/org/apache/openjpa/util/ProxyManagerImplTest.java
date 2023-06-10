package org.apache.openjpa.util;

import org.apache.tools.ant.taskdefs.optional.net.SetProxy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class ProxyManagerImplTest {
    private ProxyManagerImpl _mgr;
    private Object obj;
    private Object excpectedProxyClass;
    private boolean autoOff;
    private Collection collection;



    public ProxyManagerImplTest(CustomProxyPartition custom){
        this.excpectedProxyClass = custom.getExpectedProxyClass();
        this.obj = custom.getObj();
        this.autoOff = custom.isAutoOff();


    }

    @Before
    public void setUp() {
        _mgr = new ProxyManagerImpl();
        this.collection = Mockito.mock(Collection.class);
        when(this.collection.add(any())).thenReturn(true);
        when(this.collection.addAll(any())).thenReturn(true);

    }

    @After
    public void tearDown() throws Exception {
    }
    

    @Parameterized.Parameters
    public static List<CustomProxyPartition> getObjectType(){
        List<CustomProxyPartition> customProxyParameters = new ArrayList<>();
        Collection<String> collectionInstance =
                new ArrayList<String>(Arrays.asList("1", "2", "3"));


        customProxyParameters.add(new CustomProxyPartition("java$util$ArrayList$proxy",
                new ArrayList<>(), true));
        customProxyParameters.add(new CustomProxyPartition("java$util$ArrayList$proxy",
                collectionInstance, true));
        customProxyParameters.add(new CustomProxyPartition("java$util$Date$proxy",
                new Date(), true));
        customProxyParameters.add(new CustomProxyPartition("java$util$HashMap$proxy", new HashMap<>(), true));
        customProxyParameters.add(new CustomProxyPartition("java$util$GregorianCalendar$proxy",
                Calendar.getInstance(), true));
        customProxyParameters.add(new CustomProxyPartition(null, null, true));
        customProxyParameters.add(new CustomProxyPartition(null, 1, true));


        customProxyParameters.add(new CustomProxyPartition("java$util$ArrayList$proxy",
                new ArrayList<>(), false));
        customProxyParameters.add(new CustomProxyPartition("java$util$ArrayList$proxy",
                collectionInstance, false));
        customProxyParameters.add(new CustomProxyPartition("java$util$Date$proxy",
                new Date(), false));
        customProxyParameters.add(new CustomProxyPartition("java$util$HashMap$proxy", new HashMap<>(), false));
        customProxyParameters.add(new CustomProxyPartition("java$util$GregorianCalendar$proxy", Calendar.getInstance(), false));
        customProxyParameters.add(new CustomProxyPartition(null, null, false));
        customProxyParameters.add(new CustomProxyPartition(null, 1, false));

        return customProxyParameters;
    }

    private static final class CustomProxyPartition {
        private final Object expectedProxyClass;
        private final Object obj;
        private final boolean autoOff;    

        private CustomProxyPartition(Object expectedProxyClass, Object obj, boolean autoOff){
            this.expectedProxyClass = expectedProxyClass;
            this.obj = obj;
            this.autoOff = autoOff;
        }

        public Object getObj() {
            return obj;
        }

        public boolean isAutoOff() {
            return autoOff;
        }

        public Object getExpectedProxyClass() {
            return expectedProxyClass;
        }
    }
    
    


//    @Test
//    public void getTrackChanges() {
//    }
//
//    @Test
//    public void setTrackChanges() {
//    }
//
//    @Test
//    public void getAssertAllowedType() {
//    }
//
//    @Test
//    public void setAssertAllowedType() {
//    }
//
//    @Test
//    public void getDelayCollectionLoading() {
//    }
//
//    @Test
//    public void setDelayCollectionLoading() {
//    }
//
//    @Test
//    public void getUnproxyable() {
//    }
//
//    @Test
//    public void setUnproxyable() {
//    }
//
//    @Test
//    public void copyArray() {
//    }
//
//    @Test
//    public void copyCollection() {
//    }
//




//    @Test
//    public void newCollectionProxy() {
//    }

//    @Test
//    public void copyMap() {
//    }
//
//    @Test
//    public void newMapProxy() {
//    }
//
//    @Test
//    public void copyDate() {
//    }
//
//    @Test
//    public void newDateProxy() {
//    }
//
//    @Test
//    public void copyCalendar() {
//    }
//
//    @Test
//    public void newCalendarProxy() {
//    }
//
//    @Test
//    public void copyCustom() {
//    }
//
    @Test
    public void newCustomProxy() {
        Proxy res;

        res = _mgr.newCustomProxy(this.obj,this.autoOff);

        if(this.excpectedProxyClass == null){
            Assert.assertNull(res);
        }else{
            Assert.assertEquals(0,
                    res.getClass().getSimpleName().compareTo(this.excpectedProxyClass.toString()));
        }


    }


    @Test
    public void testSetMethodsProxied() throws Exception {
        Class proxy = _mgr.newCollectionProxy(HashSet.class, null, null, true).getClass();
        assertCollectionMethodsProxied(proxy);

//        proxy = _mgr.newCollectionProxy(CustomSet.class, null, null, true).getClass();
//        assertCollectionMethodsProxied(proxy);
//
//        proxy = _mgr.newCollectionProxy(CustomSortedSet.class, null, null, true).getClass();
//        assertCollectionMethodsProxied(proxy);
//
//        proxy = _mgr.newCollectionProxy(CustomComparatorSortedSet.class, null, new CustomComparator(), true).getClass();
//        assertCollectionMethodsProxied(proxy);
    }



    private void assertCollectionMethodsProxied(Class cls) throws Exception {
        assertNotNull(cls.getDeclaredMethod("add", new Class[] { Object.class }));
        assertNotNull(cls.getDeclaredMethod("addAll", new Class[] { Collection.class }));
        assertNotNull(cls.getDeclaredMethod("clear", (Class[]) null));
        assertNotNull(cls.getDeclaredMethod("iterator", (Class[]) null));
        assertNotNull(cls.getDeclaredMethod("remove", new Class[] { Object.class }));
        assertNotNull(cls.getDeclaredMethod("removeAll", new Class[] { Collection.class }));
        assertNotNull(cls.getDeclaredMethod("retainAll", new Class[] { Collection.class }));

        // check a non-mutating method to make sure we're not just proxying
        // everything
        try {
            cls.getDeclaredMethod("contains", new Class[] { Object.class });
            Assert.fail("Proxied non-mutating method.");
        } catch (NoSuchMethodException nsme) {
            // expected
        }
    }





//
//    @Test
//    public void toProxyableCollectionType() {
//    }
//
//    @Test
//    public void toProxyableMapType() {
//    }
//
//    @Test
//    public void isUnproxyable() {
//    }
//
//    @Test
//    public void loadBuildTimeProxy() {
//    }
//
//    @Test
//    public void loadDelayedProxy() {
//    }
//
//    @Test
//    public void generateProxyCollectionBytecode() {
//    }
//
//    @Test
//    public void generateProxyMapBytecode() {
//    }
//
//    @Test
//    public void generateProxyDateBytecode() {
//    }
//
//    @Test
//    public void generateProxyCalendarBytecode() {
//    }
//
//    @Test
//    public void generateProxyBeanBytecode() {
//    }
//
//    @Test
//    public void allowsDuplicates() {
//    }
//
//    @Test
//    public void isOrdered() {
//    }
//
//    @Test
//    public void isSetter() {
//    }
//
//    @Test
//    public void findGetter() {
//    }
//
//    @Test
//    public void findCopyConstructor() {
//    }

}
