package org.apache.openjpa.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertNotNull;
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
                Assert.assertTrue(proxyManager.isUnproxyable(this.obj.getClass())); //isUnproxyble fallisce nei casi in cui passiamo un int
            }
        }else if(this.excpectedProxyClass.toString().compareTo("notNull") == 0){
            Assert.assertNotNull(customProxy);

            Assert.assertFalse(proxyManager.isUnproxyable(this.obj.getClass()));
        }else{
            Assert.assertFalse(proxyManager.isUnproxyable(this.obj.getClass())); //isUnproxyble fallisce nei casi in cui passiamo un int

            Assert.assertEquals(0,
                    customProxy.getClass().getSimpleName().compareTo(this.excpectedProxyClass.toString()));
        }



        //Now check if copy works
        if(this.excpectedProxyClass != null && this.obj != null){
            Object copyProxy = proxyManager.copyCustom(customProxy);

            Assert.assertEquals(0,
                    this.obj.getClass().getSimpleName().compareTo(copyProxy.getClass().getSimpleName()));
        }
    }
}

