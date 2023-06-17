package org.apache.openjpa.kernel;

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.lib.util.Localizer;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.meta.MetaDataDefaults;
import org.apache.openjpa.meta.MetaDataFactory;
import org.apache.openjpa.meta.MetaDataRepository;
import org.apache.openjpa.util.CallbackException;
import org.apache.openjpa.util.ProxyManagerImpl;
import org.apache.openjpa.util.UserException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;


import java.beans.Beans;
import java.util.*;


import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class AttachManagerTest {
    private AttachManager attachManager;
    private AttachManager attachManager2; //Added after ba-dua
    private Object objCollection;
    private boolean expectedException;

    @Before
    public void setUp() throws Exception {
        //Mock plausible configuration for AttachManager
        BrokerImpl broker = Mockito.mock(BrokerImpl.class);
        OpenJPAConfiguration openJPAConfiguration = Mockito.mock(OpenJPAConfiguration.class);
        MetaDataRepository metaDataRepository = Mockito.mock(MetaDataRepository.class);
        MetaDataFactory metaDataFactory =  Mockito.mock(MetaDataFactory.class);
        MetaDataDefaults metaDataDefaults = Mockito.mock(MetaDataDefaults.class);

        ProxyManagerImpl proxyManager = new ProxyManagerImpl();

        when(metaDataRepository.getMetaDataFactory()).thenReturn(metaDataFactory);

        when(metaDataFactory.getDefaults()).thenReturn(metaDataDefaults);

        when(metaDataDefaults.getCallbackMode()).thenReturn(0); //Mock correct behaviour

        when(openJPAConfiguration.getProxyManagerInstance()).thenReturn(proxyManager);
        when(openJPAConfiguration.getMetaDataRepositoryInstance()).thenReturn(metaDataRepository);



        when( broker.getConfiguration() ).thenReturn(openJPAConfiguration);


        attachManager = new AttachManager(broker, true, new OpCallbacks() {
            @Override
            public int processArgument(int op, Object arg, OpenJPAStateManager sm) {
                return 0;
            }
        });



        //added after ba-dua
        BrokerImpl broker2 = Mockito.mock(BrokerImpl.class);
        OpenJPAConfiguration openJPAConfiguration2 = Mockito.mock(OpenJPAConfiguration.class);
        MetaDataRepository metaDataRepository2 = Mockito.mock(MetaDataRepository.class);
        MetaDataFactory metaDataFactory2 =  Mockito.mock(MetaDataFactory.class);
        MetaDataDefaults metaDataDefaults2 = Mockito.mock(MetaDataDefaults.class);

        ProxyManagerImpl proxyManager2 = new ProxyManagerImpl();

        when(metaDataRepository2.getMetaDataFactory()).thenReturn(metaDataFactory2);

        when(metaDataFactory2.getDefaults()).thenReturn(metaDataDefaults2);

        when(metaDataDefaults2.getCallbackMode()).thenReturn(2); //Mock not correct behaviour

        when(openJPAConfiguration2.getProxyManagerInstance()).thenReturn(proxyManager2);
        when(openJPAConfiguration2.getMetaDataRepositoryInstance()).thenReturn(metaDataRepository2);



        when( broker2.getConfiguration() ).thenReturn(openJPAConfiguration2);


        attachManager2 = new AttachManager(broker2, true, new OpCallbacks() {
            @Override
            public int processArgument(int op, Object arg, OpenJPAStateManager sm) {
                return 0;
            }
        });



    }

    @After
    public void tearDown() throws Exception {
        attachManager = null;
    }


    public AttachManagerTest(AttachManagerPartition calendarProxyPartition){
        this.objCollection = calendarProxyPartition.collection;
        this.expectedException = calendarProxyPartition.expectedException;
    }


    private static final class AttachManagerPartition {
        private final Object collection;
        private final boolean expectedException;

        private AttachManagerPartition(Object collection, boolean expectedException){
            this.collection = collection;
            this.expectedException = expectedException;
        }
    }


    @Parameterized.Parameters
    public static List<AttachManagerPartition> getObjectType(){
        List<AttachManagerPartition> attachManagerPartitions = new ArrayList<>();
        Collection collection = new ArrayList();
        collection.add("2");
        collection.add("3");

        attachManagerPartitions.add(new AttachManagerPartition(collection,false));
        attachManagerPartitions.add(new AttachManagerPartition("2",false));
        attachManagerPartitions.add(new AttachManagerPartition("",false));
        attachManagerPartitions.add(new AttachManagerPartition(Collections.emptySet(),false));
        attachManagerPartitions.add(new AttachManagerPartition(null,true));

        return attachManagerPartitions;
    }


    @Test
    public void attachTest() {
        try {
            Assert.assertTrue(attachManager.getCopyNew());

            attachManager.fireBeforeAttach(this.objCollection, Mockito.mock(ClassMetaData.class));


            Assert.assertEquals(this.objCollection,attachManager.attach(this.objCollection));

            if(this.objCollection != null){
                Assert.assertThrows(NullPointerException.class,() -> attachManager.getDetachedObjectId(this.objCollection));
            }else{
                throw new NullPointerException();
            }

            attachManager.setAttachedCopy(this.objCollection, Mockito.mock(PersistenceCapable.class));


            Assert.assertNotNull(attachManager.getAttachedCopy(this.objCollection));

            //No one should have managed object that has just been attached
            Assert.assertThrows(UserException.class, () -> attachManager.assertManaged(this.objCollection));



            Assert.assertFalse(expectedException);
        } catch (NullPointerException | NoSuchElementException e) {
            Assert.assertTrue(expectedException);
        }
    }




    //Added after ba-dua
    @Test
    public void attachBadBehaviourTest() {
        try {
            Assert.assertTrue(attachManager2.getCopyNew());

            attachManager2.fireBeforeAttach(this.attachManager2, Mockito.mock(ClassMetaData.class));

            Assert.assertEquals(this.objCollection,attachManager2   .attach(this.objCollection));

            if(this.objCollection != null){
                Assert.assertThrows(NullPointerException.class,() -> attachManager2.getDetachedObjectId(this.objCollection));
            }else{
                throw new NullPointerException();
            }

            attachManager2.setAttachedCopy(this.objCollection, Mockito.mock(PersistenceCapable.class));


            Assert.assertNotNull(attachManager2.getAttachedCopy(this.objCollection));

            //No one should have managed object that has just been attached
            Assert.assertThrows(UserException.class, () -> attachManager2.assertManaged(this.objCollection));

            Assert.assertFalse(expectedException);
        } catch (NullPointerException | NoSuchElementException e) {
            Assert.assertTrue(expectedException);
        }
    }
}