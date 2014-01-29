package com.greenpepper.server.database.hibernate;

import org.hibernate.Transaction;

import com.greenpepper.repository.FileSystemRepository;
import com.greenpepper.runner.repository.AtlassianRepository;
import com.greenpepper.server.GreenPepperServer;
import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.database.hibernate.upgrades.ServerVersionUpgrader;
import com.greenpepper.server.database.hibernate.upgrades.UpgradeOf_1_1;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.dao.RepositoryDao;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateRepositoryDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemUnderTestDao;

public class ServerUpgradeTo_1_1_Test  extends AbstractDBUnitHibernateMemoryTest
{
    private static final String DATAS = "/dbunit/datas/InitializedDataBase-1.1.xml";
    private SystemInfoDao systemDao;
    private RepositoryDao repoDao;
    private SystemUnderTestDao sutDao;
    private EnvironmentType JAVA = EnvironmentType.newInstance("JAVA");
    private EnvironmentType NET = EnvironmentType.newInstance(".NET");
    
    public void setUp() throws Exception
    {
        super.setUp();
        insertIntoDatabase(DATAS);
        new InitialDatas(this).insert();
        
        systemDao = new HibernateSystemInfoDao(this);
        repoDao = new HibernateRepositoryDao(this);
		sutDao = new HibernateSystemUnderTestDao(this);
        systemDao.getSystemInfo().setGpVersion("1.1");
    }
    
    public void testTheJavaEnvironmentIsInjectedIntoTheExistingRunners() throws Exception
    {
    	upgradeInTransaction(new UpgradeOf_1_1());
    	for(Runner runner : sutDao.getAllRunners())
    		assertEquals(EnvironmentType.newInstance("JAVA"), runner.getEnvironmentType());
    }

    public void testTheJiraRepositoryTypeNowHaveARepositoryClassPerEnvironmentType() throws Exception
    {
    	upgradeInTransaction(new UpgradeOf_1_1());
    	RepositoryType jira = repoDao.getTypeByName("JIRA");
    	assertEquals(AtlassianRepository.class.getName(), jira.getRepositoryTypeClass(JAVA));
    	assertEquals("GreenPepper.Repositories.AtlassianRepository", jira.getRepositoryTypeClass(NET));
    }
    
    public void testTheConfluenceRepositoryTypeNowHaveARepositoryClassPerEnvironmentType() throws Exception
    {
    	upgradeInTransaction(new UpgradeOf_1_1());
    	RepositoryType jira = repoDao.getTypeByName("CONFLUENCE");
    	assertEquals(AtlassianRepository.class.getName(), jira.getRepositoryTypeClass(JAVA));
    	assertEquals("GreenPepper.Repositories.AtlassianRepository", jira.getRepositoryTypeClass(NET));
    }
    
    public void testTheFileRepositoryTypeNowHaveARepositoryClassPerEnvironmentType() throws Exception
    {
    	upgradeInTransaction(new UpgradeOf_1_1());
    	RepositoryType jira = repoDao.getTypeByName("FILE");
    	assertEquals(FileSystemRepository.class.getName(), jira.getRepositoryTypeClass(JAVA));
    	assertEquals("GreenPepper.Repositories.FileSystemRepository", jira.getRepositoryTypeClass(NET));
    }
    
    public void testAllUIDAreNowWithADashAndNotASlash() throws Exception
    {
    	upgradeInTransaction(new UpgradeOf_1_1());
        for(Repository repo : repoDao.getAll())
        {
        	assertTrue(repo.getUid().indexOf("-") >= 0);
        }
    }
    
	private void upgradeInTransaction(ServerVersionUpgrader upgrader) throws Exception {
		beginTransaction();
        new ServerUpgrader(this).upgradeTo(upgrader.upgradedTo());
        commitTransaction();
	}

}
