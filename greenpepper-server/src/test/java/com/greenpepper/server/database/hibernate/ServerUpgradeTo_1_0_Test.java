package com.greenpepper.server.database.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;

import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.database.hibernate.upgrades.UpgradeOf_1_0;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.dao.RepositoryDao;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateRepositoryDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;

public class ServerUpgradeTo_1_0_Test  extends AbstractDBUnitHibernateMemoryTest
{
    private static final String DATAS = "/dbunit/datas/InitializedDataBase-1.0.xml";
    private SystemInfoDao systemDao;
    private RepositoryDao repoDao;
    
    public void setUp() throws Exception
    {
        super.setUp();
        insertIntoDatabase(DATAS);
        new InitialDatas(this).insert();
        
        systemDao = new HibernateSystemInfoDao(this);
        repoDao = new HibernateRepositoryDao(this);
        systemDao.getSystemInfo().setGpVersion("1.0");
    }

    public void testTheRepositoryTypeOfJiraIsUpgraded() throws Exception
    {
        upgradeInTransaction();

        List<RepositoryType> types = repoDao.getAllTypes();
        for(RepositoryType type : types)
        {
        	if(type.getName().equals("JIRA"))
        	{
        		assertEquals("%s/browse/%s", type.getDocumentUrlFormat());
        		assertNull(type.getTestUrlFormat());
        	}
        }
    }

    public void testAllJiraRepositoryMustBeUpgraded() throws Exception
    {
        upgradeInTransaction();

        List<Repository> repos = repoDao.getAll();
        for(Repository repo : repos)
        {
        	if(repo.getType().getName().equals("JIRA"))
        	{
        		assertEquals("http://domain:port/context", repo.getBaseRepositoryUrl());
        		assertEquals("http://domain:port/context/rpc/xmlrpc?handler=handler", repo.getBaseTestUrl());
        		assertEquals("http://domain:port/context", repo.getBaseUrl());
        	}
        }
    }

    public void testTheRepositoryTypeOfConfluenceIsUpgraded() throws Exception
    {
        upgradeInTransaction();

        List<RepositoryType> types = repoDao.getAllTypes();
        for(RepositoryType type : types)
        {
        	if(type.getName().equals("CONFLUENCE"))
        	{
        		assertEquals("%s/%s", type.getDocumentUrlFormat());
        		assertNull(type.getTestUrlFormat());
        	}
        }
    }

    public void testAllConfluenceRepositoryMustBeUpgraded() throws Exception
    {
    	int confCounter = 1;
        upgradeInTransaction();

        List<Repository> repos = repoDao.getAll();
        for(int i = 1; i < repos.size(); i++)
        {
        	Repository repo = repos.get(i);
        	if(repo.getType().getName().equals("CONFLUENCE"))
        	{
        		assertEquals("http://domain:port/context/display/SPACE KEY"+confCounter, repo.getBaseRepositoryUrl());
        		assertEquals("http://domain:port/context/rpc/xmlrpc?handler=handler#SPACE KEY"+confCounter, repo.getBaseTestUrl());
        		assertEquals("http://domain:port/context", repo.getBaseUrl());
        		confCounter++;
        	}
        }
    }
    
    public void testAllExecutionNowHaveTheIgnoredFieldSettedTo0() throws Exception
    {
        upgradeInTransaction();
        final Criteria criteria = getSession().createCriteria(Execution.class);  
    	@SuppressWarnings("unchecked")      
        List<Execution> executions = criteria.list();
        for(Execution exe : executions)
        {
        	assertEquals(0, exe.getIgnored());
        }
    }
    
	private void upgradeInTransaction() throws Exception {
		beginTransaction();
		new ServerUpgrader(this).upgradeTo(new UpgradeOf_1_0().upgradedTo());
		commitTransaction();
	}
}
