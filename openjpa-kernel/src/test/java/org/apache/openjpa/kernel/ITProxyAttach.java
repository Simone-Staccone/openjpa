package org.apache.openjpa.kernel;

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.kernel.AttachManager;
import org.apache.openjpa.kernel.BrokerImpl;
import org.apache.openjpa.kernel.OpCallbacks;
import org.apache.openjpa.kernel.OpenJPAStateManager;
import org.apache.openjpa.meta.MetaDataDefaults;
import org.apache.openjpa.meta.MetaDataFactory;
import org.apache.openjpa.meta.MetaDataRepository;
import org.apache.openjpa.util.ProxyCollection;
import org.apache.openjpa.util.ProxyManager;
import org.apache.openjpa.util.ProxyManagerImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ITProxyAttach {
    private ProxyManager proxyManager;
    private AttachManager attachManager;
    private ProxyManager proxySpy;


    @Before
    public void setUp(){
        this.proxyManager = new ProxyManagerImpl();
        //Mock plausible configuration for AttachManager
        BrokerImpl broker = Mockito.mock(BrokerImpl.class);
        OpenJPAConfiguration openJPAConfiguration = Mockito.mock(OpenJPAConfiguration.class);
        MetaDataRepository metaDataRepository = Mockito.mock(MetaDataRepository.class);
        MetaDataFactory metaDataFactory =  Mockito.mock(MetaDataFactory.class);
        MetaDataDefaults metaDataDefaults = Mockito.mock(MetaDataDefaults.class);


        when(metaDataRepository.getMetaDataFactory()).thenReturn(metaDataFactory);

        when(metaDataFactory.getDefaults()).thenReturn(metaDataDefaults);

        when(metaDataDefaults.getCallbackMode()).thenReturn(0); //Mock correct behaviour

        proxySpy = Mockito.spy(proxyManager);

        when(openJPAConfiguration.getProxyManagerInstance()).thenReturn(proxySpy);
        when(openJPAConfiguration.getMetaDataRepositoryInstance()).thenReturn(metaDataRepository);



        when( broker.getConfiguration() ).thenReturn(openJPAConfiguration);


        attachManager = new AttachManager(broker, true, new OpCallbacks() {
            @Override
            public int processArgument(int op, Object arg, OpenJPAStateManager sm) {
                return 0;
            }
        });
    }
    @After
    public void tearDown(){
        this.proxyManager = null;
    }

    @Test
    public void proxyAttachInteractionTest(){
        Assert.assertNotNull(this.proxyManager);

        attachManager.getProxyManager().newCollectionProxy(List.class,null,null,true);


        verify(proxySpy, times(1)).newCollectionProxy(List.class,null,null,true);

    }


}
