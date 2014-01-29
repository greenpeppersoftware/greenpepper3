package com.greenpepper.server.database.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greenpepper.repository.FileSystemRepository;
import com.greenpepper.runner.repository.AtlassianRepository;
import com.greenpepper.runner.repository.XWikiRepository;
import com.greenpepper.server.GreenPepperServer;
import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.dao.RepositoryDao;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateRepositoryDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemUnderTestDao;

public class InitialDatasTest extends AbstractDBUnitHibernateMemoryTest 
{
    private SystemInfoDao systDao;
    private SystemUnderTestDao sutDao;
    private RepositoryDao repoDao;
    
    protected void setUp() throws Exception
    {
        super.setUp();

        systDao = new HibernateSystemInfoDao(this);
        sutDao = new HibernateSystemUnderTestDao(this);
        repoDao = new HibernateRepositoryDao(this);
    }
    
    public void testTheSystemInfoIsInitializedIfNotEntry() throws Exception
    {
        new InitialDatas(this).insert();
        SystemInfo systemInfo = systDao.getSystemInfo();
        assertEquals("Invalid", systemInfo.getLicense());
        assertEquals(GreenPepperServer.VERSION, systemInfo.getGpVersion());
    }
    
    public void testTheSystemInfoInsertIsNotTriggeredIfWeAlreadyHaveSomeEntriesButTheVersionIsSetToDefault() throws Exception
    {
    	insertIntoDatabase("/dbunit/datas/InitializedDataBase-latest.xml");
        new InitialDatas(this).insert();
        
        SystemInfo systemInfo = systDao.getSystemInfo();
        assertEquals("LICENSED", systemInfo.getLicense());
        assertEquals(InitialDatas.DEFAULT_VERSION, systemInfo.getGpVersion());
    }
    
    public void testTheEnvironmentsAreProperlyInserted() throws Exception
    {
        new InitialDatas(this).insert();
        
        List<EnvironmentType> envs =  sutDao.getAllEnvironmentTypes();
        assertTrue(envs.contains(EnvironmentType.newInstance("JAVA")));
        assertTrue(envs.contains(EnvironmentType.newInstance(".NET")));
    }
    
    public void testAllSupportedRepositoryTypesAreRegisteredAtInitialization() throws Exception
    {
        new InitialDatas(this).insert();
        Map<String, String> atlassRepoClasses = new HashMap<String, String>();
        atlassRepoClasses.put("JAVA", AtlassianRepository.class.getName());
        atlassRepoClasses.put(".NET", "GreenPepper.Repositories.AtlassianRepository");
        Map<String, String> fileRepoClasses = new HashMap<String, String>();
        fileRepoClasses.put("JAVA", FileSystemRepository.class.getName());
        fileRepoClasses.put(".NET", "GreenPepper.Repositories.FileSystemRepository");
		Map<String, String> xWikiRepoClasses = new HashMap<String, String>();
		xWikiRepoClasses .put("JAVA", XWikiRepository.class.getName());
		xWikiRepoClasses .put(".NET", "GreenPepper.Repositories.XWikiRepository");

        assertTypeRegistered("FILE", fileRepoClasses, "%s%s", "%s%s");
        assertTypeRegistered("CONFLUENCE", atlassRepoClasses, "%s/%s", null);
        assertTypeRegistered("JIRA", atlassRepoClasses, "%s/browse/%s", null);
		assertTypeRegistered("XWIKI", xWikiRepoClasses, "%s/%s", null);
    }
    
    public void testAllSupportedRepositoryTypesInsertIsNotTriggeredIfWeAlreadyHaveSomeEntries() throws Exception
    {
        new InitialDatas(this).insert();

        List<RepositoryType> types = repoDao.getAllTypes();
        for(RepositoryType type : types)
    	{
            assertEquals(new Integer(0), type.getVersion());
    	}
    }
    
    private void assertTypeRegistered(String typeName, Map<String, String> repoTypeClasses, String docFormat, String testFormat)
    {
        RepositoryType type = repoDao.getTypeByName(typeName);
        
        assertNotNull(type);
    	for(String envType : repoTypeClasses.keySet())
    		assertEquals(repoTypeClasses.get(envType), type.getRepositoryTypeClass(EnvironmentType.newInstance(envType)));
    	
        assertEquals(docFormat, type.getDocumentUrlFormat());
        assertEquals(testFormat, type.getTestUrlFormat());
    }
}
