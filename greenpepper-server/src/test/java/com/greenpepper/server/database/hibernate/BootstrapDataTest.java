package com.greenpepper.server.database.hibernate;

import java.net.URL;

import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.configuration.ServerConfiguration;
import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;

public class BootstrapDataTest extends AbstractDBUnitHibernateMemoryTest
{
    private URL configURL = BootstrapDataTest.class.getResource("configuration-test.xml");
    private BootstrapData boot;
    private SystemInfoDao systemDao;
    
    protected void setUp() throws Exception
    {
        super.setUp();

        ServerConfiguration config = ServerConfiguration.load(configURL);
        config.getProperties().setProperty("baseUrl", "no directories");
        
        boot = new BootstrapData(this, config.getProperties());
        systemDao = new HibernateSystemInfoDao(this);
    }
    
    public void tearDown()
    {
    }

    public void testWhileUpgradingIfAnErrorOccuresTheBootstrapProcessWillAbortAndProvoqueARollBack() throws Exception
    {
    	try 
    	{
			setupSystemInfo("VERSION.THAT.WILL.CAUSE.A.FAILURE");
			boot.execute();
			fail();
		} 
    	catch (GreenPepperServerException e)
    	{
			assertEquals("Boostrap Failure", e.getMessage());
		}

        assertNull(systemDao.getSystemInfo());
    }
    
    public void testWhileRegistratingTheRunnersIfAnErrorOccuresTheBootstrapProcessWillContinue() throws Exception
    {
    	boot.execute();
    	assertNotNull(systemDao.getSystemInfo());
    }
    
	private void setupSystemInfo(String gpVersion)
	{
		SystemInfo sysForServerOfCurrentVersion = new SystemInfo();
		sysForServerOfCurrentVersion.setVersion(0);
		sysForServerOfCurrentVersion.setGpVersion(gpVersion);
		systemDao.store(sysForServerOfCurrentVersion);
	}
}
