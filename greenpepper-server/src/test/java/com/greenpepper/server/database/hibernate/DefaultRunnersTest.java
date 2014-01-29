package com.greenpepper.server.database.hibernate;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;

import com.greenpepper.runner.Main;
import com.greenpepper.server.GreenPepperServer;
import com.greenpepper.server.configuration.ServerConfiguration;
import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemUnderTestDao;
import com.greenpepper.util.URIUtil;

public class DefaultRunnersTest extends AbstractDBUnitHibernateMemoryTest
{
    private static final String DATAS = "/dbunit/datas/InitializedDataBase-latest.xml";
    private URL configURL = DefaultRunnersTest.class.getResource("configuration-test.xml");
	private Properties properties;

	private String basePath;
	private SystemUnderTestDao sutDao;
	private File runnerDir;

    protected void setUp() throws Exception
    {
        super.setUp();

		basePath = URIUtil.decoded(new File(configURL.getFile()).getParent());
        runnerDir = new File(basePath, "WEB-INF/lib");
        ServerConfiguration config = ServerConfiguration.load(configURL);
        config.getProperties().setProperty("baseUrl", basePath);
		properties = config.getProperties();

		sutDao = new HibernateSystemUnderTestDao(this);
    }
    
    public void testTheJavaCurrentVersionRunnerIsProperlyRegisteredAndAllLibsAreCopiedIntoTheRunnersDirectory() throws Exception
    {  
        List<String> expectedCp = new ArrayList<String>();
        expectedCp.add(URIUtil.decoded(new File(runnerDir, "greenpepper-confluence-plugin-dummy-complete.jar").getAbsolutePath()).toUpperCase());
        
        new InitialDatas(this).insert();
        new DefaultRunners(this, properties).insert();
        
        Runner runner = sutDao.getRunnerByName("GPCore JAVA v. dummy");
        
        assertNotNull(runner);
        assertEquals(Main.class.getName(), runner.getMainClass());
        assertEquals("java -mx252m -cp ${classpaths} ${mainClass} ${inputPath} ${outputPath} -l ${locale} -r ${repository} -f ${fixtureFactory} --xml", runner.getCmdLineTemplate());
        assertNull(runner.getServerName());
        assertNull(runner.getServerPort());
        assertEquals("JAVA", runner.getEnvironmentType().getName());

        List<String> runnerClasspaths = toUpperCaseList(runner.getClasspaths());
		assertTrue(runnerClasspaths.containsAll(expectedCp));
        assertTrue("expected:" + expectedCp.toString() + "but got :" + runnerClasspaths.toString(), expectedCp.containsAll(runnerClasspaths));
        assertTrue(expectedCp.containsAll(asPathList((runnerDir.listFiles()))));
    }
    
    public void testTheJavaCurrentVersionRunnerInsertIsNotTriggeredIfWeAlreadyHaveARunnerWithSameVersion() throws Exception
    {  
        insertIntoDatabase(DATAS);
        new DefaultRunners(this, properties).insert(); 
        
        Runner runner = sutDao.getRunnerByName("GPCore JAVA v. dummy");
        
        assertNotNull(runner);
        assertEquals("MAIN", runner.getMainClass());
        assertEquals("JAVA", runner.getEnvironmentType().getName());
        assertEquals("1", runner.getServerName());
        assertEquals("1", runner.getServerPort());
    }

	public void testTheJavaCurrentVersionRunnerIsProperlyRegisteredUsingTheHomeDirectory() throws Exception
	{
		File homeDir = new File(basePath, "home");
		runnerDir = new File(homeDir, "java/runner");
		properties.put("greenpepper.home", homeDir.getAbsolutePath());

		List<String> expectedCp = new ArrayList<String>();
		expectedCp.add(URIUtil.decoded(new File(runnerDir, "greenpepper-core-dummy.jar").getAbsolutePath()).toUpperCase());
		expectedCp.add(URIUtil.decoded(new File(runnerDir, "greenpepper-extensions-java-dummy.jar").getAbsolutePath()).toUpperCase());
		expectedCp.add(URIUtil.decoded(new File(runnerDir, "xmlrpc-2.0.1.jar").getAbsolutePath()).toUpperCase());
		expectedCp.add(URIUtil.decoded(new File(runnerDir, "commons-codec-1.3.jar").getAbsolutePath()).toUpperCase());

		new InitialDatas(this).insert();
		new DefaultRunners(this, properties).insert();

		Runner runner = sutDao.getRunnerByName("GPCore JAVA v. dummy");

		assertNotNull(runner);
		assertEquals(Main.class.getName(), runner.getMainClass());
		assertEquals("java -mx252m -cp ${classpaths} ${mainClass} ${inputPath} ${outputPath} -l ${locale} -r ${repository} -f ${fixtureFactory} --xml", runner.getCmdLineTemplate());
		assertNull(runner.getServerName());
		assertNull(runner.getServerPort());
		assertEquals("JAVA", runner.getEnvironmentType().getName());

		 List<String> runnerClasspaths = toUpperCaseList(runner.getClasspaths());
		assertTrue(runnerClasspaths.containsAll(expectedCp));
		assertTrue(expectedCp.containsAll(runnerClasspaths));
		assertTrue(expectedCp.containsAll(asPathList((runnerDir.listFiles()))));
	}

	public void testTheDotNetCurrentVersionRunnerIsProperlyRegisteredUsingTheHomeDirectory() throws Exception
	{
		File homeDir = new File(basePath, "home");
		runnerDir = new File(homeDir, "dotnet/runner");
		properties.put("greenpepper.home", homeDir.getAbsolutePath());

		List<String> expectedCp = new ArrayList<String>();
		expectedCp.add(URIUtil.decoded(new File(runnerDir, "GreenPepper.Extensions.dll").getAbsolutePath()).toUpperCase());
		expectedCp.add(URIUtil.decoded(new File(runnerDir, "GreenPepper.Core.dll").getAbsolutePath()).toUpperCase());
		expectedCp.add(URIUtil.decoded(new File(runnerDir, "CookComputing.XmlRpc.dll").getAbsolutePath()).toUpperCase());

		new InitialDatas(this).insert();
		new DefaultRunners(this, properties).insert();

		Runner runner = sutDao.getRunnerByName(String.format("GPCore .NET v. %s", GreenPepperServer.VERSION));

		assertNotNull(runner);
		assertNull(runner.getMainClass());
		assertEquals(String.format("%s/GreenPepper.exe ${inputPath} ${outputPath} -a ${classpaths} " +
								   "-r ${repository} -f ${fixtureFactory} --xml",
								   runnerDir.getAbsolutePath().replaceAll("\\\\", "/")).toUpperCase(),
					 runner.getCmdLineTemplate().toUpperCase());
		assertNull(runner.getServerName());
		assertNull(runner.getServerPort());
		assertEquals(".NET", runner.getEnvironmentType().getName());

		List<String> runnerClasspaths = toUpperCaseList(runner.getClasspaths());
		assertTrue(runnerClasspaths.containsAll(expectedCp));
		assertTrue(expectedCp.containsAll(runnerClasspaths));
		assertTrue(expectedCp.containsAll(asPathList((runnerDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return !pathname.getName().endsWith("GreenPepper.exe");
			}
		})))));
	}

    private List<String> toUpperCaseList(Collection<String> classpaths) {
        List<String> paths = new ArrayList<String>();
        for(String path :classpaths)
        {
            paths.add(path.toUpperCase());
        }
        
        return paths;
	}

	private List<String> asPathList(File[] files)
    {
        List<String> paths = new ArrayList<String>();
        for(File file :files)
        {
            paths.add(URIUtil.decoded(file.getAbsolutePath()).toUpperCase());
        }
        
        return paths;
    }
}
