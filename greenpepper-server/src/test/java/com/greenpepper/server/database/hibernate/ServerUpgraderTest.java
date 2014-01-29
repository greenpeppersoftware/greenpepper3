package com.greenpepper.server.database.hibernate;

import org.hibernate.Transaction;

import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;

public class ServerUpgraderTest extends AbstractDBUnitHibernateMemoryTest
{    
    private SystemInfoDao systemDao;
        
    protected void setUp() throws Exception
    {
        super.setUp();
        systemDao = new HibernateSystemInfoDao(this);
    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNoUpgradesShouldBeTriggeredIfTheInstalledVersionMatchesTheCurrentVersion() throws Exception
    {
    	setupSystemInfo("VERSION");
        new ServerUpgrader(this).upgradeTo("VERSION");

        assertEquals(new Integer(0), systemDao.getSystemInfo().getVersion());
        assertEquals("VERSION", systemDao.getSystemInfo().getGpVersion());
    }

    public void testIfNoUpgradesNeededTheInstalledVersionIsAutomaticallyUpdatedToTheCurrentVersion() throws Exception
    {
    	setupSystemInfo("SOME.VERSION");
        new ServerUpgrader(this).upgradeTo("VERSION.WITH.NO.UPGRADES");
        
        assertEquals("VERSION.WITH.NO.UPGRADES", systemDao.getSystemInfo().getGpVersion()); 
    }

    public void testTheUpgradesAreCalledBasedOnTheNewlyUpgradedVersion() throws Exception
    {        
    	setupSystemInfo("VERSION.THAT.NEEDS.UPGRADES");
    	
    	Transaction transaction = session.beginTransaction();
        new ServerUpgrader(this).upgradeTo("VERSION.UPGRADED");
        transaction.commit();
        
	    SystemInfo systemInfo = systemDao.getSystemInfo();
        assertEquals(new Integer(2), systemInfo.getVersion());
        assertEquals("VERSION.UPGRADED", systemInfo.getGpVersion());
        assertEquals("CB", systemInfo.getLicense());
    }

    public void testAnUpgradedThatReturnsNoUpgradedVersionToIsConsideredAsANoUpgradeVersion() throws Exception
    {        
    	setupSystemInfo("VERSION.THAT.DOESNT.RETURN.AN.UPGRADED.TO.VERSION");
        Transaction transaction = session.beginTransaction();
        new ServerUpgrader(this).upgradeTo("VERSION.UPGRADED");
        transaction.commit();

	    SystemInfo systemInfo = systemDao.getSystemInfo();
        assertEquals(new Integer(1), systemInfo.getVersion());
        assertEquals("VERSION.UPGRADED", systemInfo.getGpVersion());
        assertEquals("A", systemInfo.getLicense());
    }
    
	private void setupSystemInfo(String gpVersion)
	{
		SystemInfo sysForServerOfCurrentVersion = new SystemInfo();
//		sysForServerOfCurrentVersion.setId(1L);
		sysForServerOfCurrentVersion.setVersion(0);
		sysForServerOfCurrentVersion.setGpVersion(gpVersion);
		systemDao.store(sysForServerOfCurrentVersion);
	}
}
