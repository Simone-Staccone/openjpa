package org.apache.openjpa.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.beans.Beans;
import java.sql.Timestamp;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class ProxyManagerImplTest {
    private ProxyManagerImpl proxyManager;
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
        proxyManager = new ProxyManagerImpl();
        this.collection = Mockito.mock(Collection.class);
        when(this.collection.add(any())).thenReturn(true);
        when(this.collection.addAll(any())).thenReturn(true);

    }

    @After
    public void tearDown() throws Exception {
        proxyManager = null;
    }
    

    @Parameterized.Parameters
    public static List<CustomProxyPartition> getObjectType(){
        List<CustomProxyPartition> customProxyParameters = new ArrayList<>();
        Collection<String> collectionInstance =
                new ArrayList<String>(Arrays.asList("1", "2", "3"));


        customProxyParameters.add(new CustomProxyPartition("java$util$ArrayList$proxy",
                new ArrayList<>(), true));
        customProxyParameters.add(new CustomProxyPartition("java$util$ArrayList$proxy",
                collectionInstance, false));
        customProxyParameters.add(new CustomProxyPartition("java$util$Date$proxy",
                new Date(), false));
        customProxyParameters.add(new CustomProxyPartition("java$util$HashMap$proxy", new HashMap<>(), false));
        customProxyParameters.add(new CustomProxyPartition("java$util$GregorianCalendar$proxy",
                Calendar.getInstance(), false));
        customProxyParameters.add(new CustomProxyPartition("notNull", new HashSet<>(), false));
        customProxyParameters.add(new CustomProxyPartition(null, null, false));
        customProxyParameters.add(new CustomProxyPartition(null, 1, false));

        //Added after jacoco
        customProxyParameters.add(new CustomProxyPartition("java$util$Date$proxy", new ProxyManagerImpl().newDateProxy(Date.class), false));
        customProxyParameters.add(new CustomProxyPartition("java$sql$Timestamp$proxy", new Timestamp(1000), false));
        customProxyParameters.add(new CustomProxyPartition("java$util$TreeMap$proxy", new TreeMap<>(), false));  //Sorted map
        customProxyParameters.add(new CustomProxyPartition("java$util$TreeSet$proxy", new TreeSet<>(), false)); //Sorted set
        customProxyParameters.add(new CustomProxyPartition("java$beans$Beans$10$proxy", new Beans(), false)); //Sorted set


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
    

    @Test
    public void newCustomProxy() {
        Proxy customProxy;

        customProxy = proxyManager.newCustomProxy(this.obj,this.autoOff);

        if(this.excpectedProxyClass == null){
            Assert.assertNull(customProxy);

            //Check if is not proxyble
            if(this.obj != null){
                Assert.assertFalse(proxyManager.isUnproxyable(this.obj.getClass())); //isUnproxyble fallisce nei casi in cui passiamo un int
            }
        }else if(this.excpectedProxyClass.toString().compareTo("notNull") == 0){
            Assert.assertNotNull(customProxy);

            Assert.assertFalse(proxyManager.isUnproxyable(this.obj.getClass()));
        }else{
            Assert.assertFalse(proxyManager.isUnproxyable(this.obj.getClass())); //isUnproxyble fallisce nei casi in cui passiamo un int

            Assert.assertEquals(0,
                    customProxy.getClass().getSimpleName().substring(0,9).compareTo(this.excpectedProxyClass.toString().substring(0,9)));
        }



        //Now check if copy works
        if(this.excpectedProxyClass != null && this.obj != null){
            Object copyProxy = proxyManager.copyCustom(customProxy);

            //Added after PIT
            Assert.assertNotNull(copyProxy);

            Object newCopyProxy = proxyManager.copyCustom(copyProxy);
            if(this.obj.getClass().getSimpleName().compareTo("java$util$Date$proxy") == 0){
                Assert.assertEquals("java$util$Date$proxy",customProxy.getClass().getSimpleName());


                //Added after PIT
                Assert.assertTrue(   ((Date) newCopyProxy).getTime() != 0  );

            }else{
                Assert.assertEquals(0,
                        this.obj.getClass().getSimpleName().compareTo(copyProxy.getClass().getSimpleName()));
            }



            if(this.obj.getClass().getSimpleName().compareTo("Beans") != 0)
                Assert.assertEquals(this.obj,newCopyProxy);
        }




    }
}

