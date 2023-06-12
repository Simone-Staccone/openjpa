package org.apache.openjpa;

import org.apache.openjpa.util.ProxyManager;
import org.apache.openjpa.util.ProxyManagerImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ITProxyAttach {
    private ProxyManager proxyManager;

    @Before
    public void setUp(){
        this.proxyManager = new ProxyManagerImpl();
    }
    @After
    public void tearDown(){

    }

    @Test
    public void proxyAttachInteractionTest(){
        Assert.assertNotNull(this.proxyManager);
    }


}
