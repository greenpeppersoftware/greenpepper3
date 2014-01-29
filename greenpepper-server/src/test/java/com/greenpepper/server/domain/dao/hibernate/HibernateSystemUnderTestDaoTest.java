package com.greenpepper.server.domain.dao.hibernate;

import java.util.List;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.domain.ClasspathSet;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.SystemUnderTest;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;

public class HibernateSystemUnderTestDaoTest  extends AbstractDBUnitHibernateMemoryTest
{
    private static final String DATAS = "/dbunit/datas/HibernateSystemUnderTestDaoTest.xml";
    private SystemUnderTestDao systemUnderTestDao;
    private ClasspathSet sutPaths;
	private ClasspathSet fixturePaths;
	private ClasspathSet runnerClassPaths;
	private ClasspathSet updatedClassPaths;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        insertIntoDatabase(DATAS);
        systemUnderTestDao = new HibernateSystemUnderTestDao(this);
        sutPaths = new ClasspathSet();
        sutPaths.add("SUT-PATH-1");
        sutPaths.add("SUT-PATH-2");
        fixturePaths = new ClasspathSet();
        fixturePaths.add("FIXTURE-PATH-1");
        fixturePaths.add("FIXTURE-PATH-2");
        runnerClassPaths = new ClasspathSet();
        runnerClassPaths.add("RUNNER-PATH-1");
        runnerClassPaths.add("RUNNER-PATH-2");
        updatedClassPaths = new ClasspathSet();
        updatedClassPaths.add("RUNNER-PATH-UPDATED");
        updatedClassPaths.add("RUNNER-PATH-UPDATED");
    }
    
    protected void tearDown() throws Exception
    {
        if(session != null)
        {
            closeSession();
        }
        super.tearDown();
    }
    
    public void testWeCanRetrieveAllAvailableEnvironmentTypes()
    {
        assertEquals(2, systemUnderTestDao.getAllEnvironmentTypes().size());
    }

    public void testAnEnvironmentTypeCanBeSelectedbyName() throws GreenPepperServerException
    {
        assertNull(systemUnderTestDao.getEnvironmentTypeByName("ENVTYPE-NOT-FOUND"));
        assertNotNull(systemUnderTestDao.getEnvironmentTypeByName(".NET"));
    }

    public void testARunnerCanBeSelectedbyName() throws GreenPepperServerException
    {
        assertNull(systemUnderTestDao.getRunnerByName("RUNNER-NOT-FOUND"));
        assertNotNull(systemUnderTestDao.getRunnerByName("RUNNER-1"));
    }
    
    public void testWeCanRetrieveAllAvailableRunners()
    {        
        assertEquals(4, systemUnderTestDao.getAllRunners().size());
    }
    
    public void testWeCanUpdateARunner() throws GreenPepperServerException
    {
        Runner runner = Runner.newInstance("RUNNER-TO-UPDATE");
        runner.setCmdLineTemplate("CMD_UPDATED");
        runner.setMainClass("CLASS_UPDATED");
        runner.setServerName("SNAME_UPDATED");
        runner.setServerPort("SPORTUP");
        runner.setClasspaths(updatedClassPaths);
        runner.setEnvironmentType(EnvironmentType.newInstance("JAVA"));

        session.getTransaction().begin();
        systemUnderTestDao.update("RUNNER-TO-UPDATE", runner);
        session.getTransaction().commit();

        Runner runnerDB = getById(Runner.class, -1l);
        assertEquals("CMD_UPDATED", runnerDB.getCmdLineTemplate());
        assertEquals("CLASS_UPDATED", runnerDB.getMainClass());
        assertEquals("SNAME_UPDATED", runnerDB.getServerName());
        assertEquals("SPORTUP", runnerDB.getServerPort());
        assertTrue(updatedClassPaths.containsAll(runnerDB.getClasspaths()));
        assertTrue(runnerDB.getClasspaths().containsAll(updatedClassPaths));
    }
    
    public void testWeCantUpdateANotFoundRunner()
    {
        try
        {
            session.getTransaction().begin();
            systemUnderTestDao.update("RUNNER-NOT-FOUND", Runner.newInstance("TO-SOME-RUNNER"));
            session.getTransaction().commit();
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, e.getId());
        }
    }
    
    public void testWeCantUpdateARunnerToAnAlreadyUsedName()
    {
        try
        {
            session.getTransaction().begin();
            systemUnderTestDao.update("RUNNER-TO-UPDATE", Runner.newInstance("RUNNER-1"));
            session.getTransaction().commit();
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.RUNNER_ALREADY_EXISTS, e.getId());
        }
    }
    
    public void testWeCantUpdateARunnerWithANoneExistingEnvironmentType()
    {
        try
        {
            Runner runner = Runner.newInstance("RUNNER-TO-UPDATE");
            runner.setEnvironmentType(EnvironmentType.newInstance("ENVTYPE-NOT-FOUND"));

            session.getTransaction().begin();
            systemUnderTestDao.update("RUNNER-TO-UPDATE", runner);
            session.getTransaction().commit();
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.ENVTYPE_NOT_FOUND, e.getId());
        }
    }
    
    public void testARunnerCanBeCreated() throws GreenPepperServerException
    {
        Runner runner = Runner.newInstance("RUNNER_CREATED");
        runner.setCmdLineTemplate("CMD_TEMPLATE");
        runner.setMainClass("MAINCLASS");
        runner.setServerName("SERVERNAME");
        runner.setServerPort("8080");
        runner.setClasspaths(runnerClassPaths);
        runner.setEnvironmentType(EnvironmentType.newInstance("JAVA"));

        session.getTransaction().begin();
        systemUnderTestDao.create(runner);        
        session.getTransaction().commit();  
        
        assertNotNull(getById(Runner.class, runner.getId()));
        assertEquals("RUNNER_CREATED", runner.getName());
        assertEquals("CMD_TEMPLATE", runner.getCmdLineTemplate());
        assertEquals("MAINCLASS", runner.getMainClass());
        assertEquals("SERVERNAME", runner.getServerName());
        assertEquals("8080", runner.getServerPort());
        assertTrue(runnerClassPaths.containsAll(runner.getClasspaths()));
        assertTrue(runner.getClasspaths().containsAll(runnerClassPaths));
    }
    
    public void testARunnerCantBeCreatedIfAnotherRunnerExistsUnderTheSameName()
    {
        try
        {
            session.getTransaction().begin();
            Runner runner = Runner.newInstance("RUNNER_CREATED");
            runner.setEnvironmentType(EnvironmentType.newInstance("ENVTYPE-NOT-FOUND"));
            systemUnderTestDao.create(runner);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.ENVTYPE_NOT_FOUND, e.getId());
        }
    }
    
    public void testARunnerCantBeCreatedIfEnvironmentTypeDoesntExists()
    {
        try
        {
            session.getTransaction().begin();
            Runner runner = Runner.newInstance("RUNNER-1");
            systemUnderTestDao.create(runner);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.RUNNER_ALREADY_EXISTS, e.getId());
        }
    }
    
    public void testWeCanRemoveARunner() throws GreenPepperServerException
    {
        session.getTransaction().begin();
        systemUnderTestDao.removeRunner("RUNNER-TO-REMOVE");
        session.getTransaction().commit();     
        
        assertNull(getById(Runner.class, -2l));
    }
    
    public void testWeCantRemoveANoneExistingRunner()
    {
        try 
        {
            session.getTransaction().begin();
			systemUnderTestDao.removeRunner("RUNNER-NOT-FOUND");
	        session.getTransaction().commit();     
	        fail();
		} 
        catch (GreenPepperServerException e) 
        {
            assertEquals(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, e.getId());
		}
    }
    
    public void testWeCantRemoveARunnerIfASutIsReferecingIt()
    {
        try 
        {
            session.getTransaction().begin();
			systemUnderTestDao.removeRunner("RUNNER-1");
	        session.getTransaction().commit();  
	        fail();
		} 
        catch (GreenPepperServerException e) 
        {
            assertEquals(GreenPepperServerErrorKey.RUNNER_SUT_ASSOCIATED, e.getId());
		}
    }

    public void testASutCanBeSelectedbyName() throws GreenPepperServerException
    {
        assertNull(systemUnderTestDao.getByName("PROJECT-NOT-FOUND", "SUT-1"));
        assertNull(systemUnderTestDao.getByName("PROJECT-1", "SUT-NOT-FOUND"));
        assertNotNull(systemUnderTestDao.getByName("PROJECT-1", "SUT-1"));
    }
    
    public void testASutCanBeCreated() throws GreenPepperServerException
    {
        
        Project project = Project.newInstance("PROJECT-1");
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT-CREATED");
        sut.setProject(project);
        sut.setFixtureClasspaths(fixturePaths);
        sut.setFixtureFactory(null);
        sut.setFixtureFactoryArgs(null);
        sut.setIsDefault(true);
		sut.setProjectDependencyDescriptor(null);
		sut.setRunner(Runner.newInstance("RUNNER-1"));
        
        session.getTransaction().begin();
        systemUnderTestDao.create(sut);        
        session.getTransaction().commit();        
        
        assertNotNull(getById(SystemUnderTest.class, sut.getId()));
    }

    public void testAnErrorOccuresWhenCreatingASutWithANoneExistingRunner()
    {
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT-CREATED");
        sut.setRunner(Runner.newInstance("RUNNER-NOT-FOUND"));
        sut.setProject(Project.newInstance("PROJECT-1"));
        
        try
        {
            session.getTransaction().begin();
            systemUnderTestDao.create(sut);
            session.getTransaction().commit();
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, e.getId());
        }
    }

    public void testAnErrorOccuresWhenCreatingASutWithANoneExistingProject()
    {
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT-CREATED");
        sut.setRunner(Runner.newInstance("RUNNER-1"));
        sut.setProject(Project.newInstance("PROJECT-NOT-FOUND"));
        
        try
        {
            session.getTransaction().begin();
            systemUnderTestDao.create(sut);
            session.getTransaction().commit();
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, e.getId());
        }
    }
    
    public void testTheUnicityOfTheProjectNameAndSutNameOnCreation()
    {
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT-1");
        sut.setProject(Project.newInstance("PROJECT-1"));
        
        try        
        {
            session.getTransaction().begin();
            systemUnderTestDao.create(sut);
            session.getTransaction().commit();
            fail();
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    public void testWeCanUpdateASut() throws GreenPepperServerException
    {        
        SystemUnderTest newSut = SystemUnderTest.newInstance("SUT-UPDATED");
        newSut.setProject(Project.newInstance("PROJECT-1"));
        newSut.setRunner(Runner.newInstance("RUNNER-2"));
		newSut.setProjectDependencyDescriptor("PROJECT-DEPENDENCY-DESCRIPTOR-11");

		session.getTransaction().begin();
		systemUnderTestDao.update("SUT-TO-UPDATE", newSut);
		session.getTransaction().commit();
		
		SystemUnderTest loadedSut = getById(SystemUnderTest.class, -1l);
		assertEquals("PROJECT-1", loadedSut.getProject().getName());
		assertEquals("RUNNER-2", loadedSut.getRunner().getName());
		assertEquals("SUT-UPDATED", loadedSut.getName());
		assertEquals("PROJECT-DEPENDENCY-DESCRIPTOR-11", loadedSut.getProjectDependencyDescriptor());
	}
    
    public void testWaCantUpdateASutWithToAnExisitingSutNameForTheSameProject()
    {
        SystemUnderTest newSut = SystemUnderTest.newInstance("SUT-1");
        newSut.setProject(Project.newInstance("PROJECT-1"));
        newSut.setRunner(Runner.newInstance("RUNNER-2"));
        

		try {
			session.getTransaction().begin();
			systemUnderTestDao.update("SUT-TO-UPDATE", newSut);
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
            assertEquals(GreenPepperServerErrorKey.SUT_ALREADY_EXISTS, e.getId());
		}
    }
    
    public void testWaCantUpdateASutWithANoneExistingRunner()
    {
        SystemUnderTest newSut = SystemUnderTest.newInstance("SUT-UPDATED");
        newSut.setProject(Project.newInstance("PROJECT-1"));
        newSut.setRunner(Runner.newInstance("RUNNER-NOT-FOUND"));
        
		try 
		{
			session.getTransaction().begin();
			systemUnderTestDao.update("SUT-TO-UPDATE", newSut);
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
            assertEquals(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, e.getId());
		}
    }
    
    public void testWeCanRemoveASut() throws GreenPepperServerException
    {
		session.getTransaction().begin();
		systemUnderTestDao.remove("PROJECT-1", "SUT-TO-REMOVE");
		session.getTransaction().commit();
		
		assertNull(getById(SystemUnderTest.class, -2l));
    }
    
    public void testWeCantRemoveANoneExistingSut()
    {
		try 
		{
			session.getTransaction().begin();
			systemUnderTestDao.remove("PROJECT-1", "SUT-NOT-FOUND");
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
            assertEquals(GreenPepperServerErrorKey.SUT_NOT_FOUND, e.getId());
		}
    }
    
    public void testWeCantRemoveASutThatIsReferenced()
    {
		try 
		{
			session.getTransaction().begin();
			systemUnderTestDao.remove("PROJECT-1", "SUT-1");
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
            assertEquals(GreenPepperServerErrorKey.SUT_REFERENCE_ASSOCIATED, e.getId());
		}
    }
    
    public void testWeCantRemoveASutThatIsAssociatedWithSpecifications()
    {
		try 
		{
			session.getTransaction().begin();
			systemUnderTestDao.remove("PROJECT-1", "SUT-2");
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
            assertEquals(GreenPepperServerErrorKey.SUT_SPECIFICATION_ASSOCIATED, e.getId());
		}
    }
    
    public void testWeCantRemoveASutThatIsAssociatedWithExecutions()
    {
		try 
		{
			session.getTransaction().begin();
			systemUnderTestDao.remove("PROJECT-1", "SUT-3");
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
            assertEquals(GreenPepperServerErrorKey.SUT_EXECUTION_ASSOCIATED, e.getId());
		}
    }
    
    public void testWeCanSelectANewSystemUnderTestAsDefault() throws GreenPepperServerException
    {
        SystemUnderTest newDefaultSut = SystemUnderTest.newInstance("SUT-TO-BE-DEFAULT");
        newDefaultSut.setProject(Project.newInstance("PROJECT-1"));

        session.getTransaction().begin();
        systemUnderTestDao.setAsDefault(newDefaultSut);
        session.getTransaction().commit();
        
        List<SystemUnderTest> suts = systemUnderTestDao.getAllForProject("PROJECT-1");
        for(SystemUnderTest sut : suts)
        {
            if(sut.getName().equals(newDefaultSut.getName()))
                assertTrue(sut.isDefault());
            else
                assertFalse(sut.isDefault());
        }
    }
    
    public void testWeCantSelectANewSystemUnderTestAsDefaultIfProjectIsNotFoound()
    {
        SystemUnderTest newDefaultSut = SystemUnderTest.newInstance("SUT-TO-BE-DEFAULT");
        newDefaultSut.setProject(Project.newInstance("PROJECT-NOT-FOUND"));

        try 
        {
			session.getTransaction().begin();
			systemUnderTestDao.setAsDefault(newDefaultSut);
			session.getTransaction().commit();
		} 
        catch (GreenPepperServerException e) 
        {
            assertEquals(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, e.getId());
		}
    }
    
    public void testWeCanRetrieveAllSutsForAGivenProject()
    {        
        assertEquals(6, systemUnderTestDao.getAllForProject("PROJECT-1").size());
    }
    
    public void testWeCanRetrieveAllSutsForAGivenRunner()
    {        
        assertEquals(6, systemUnderTestDao.getAllForRunner("RUNNER-1").size());
    }
    
    public void testWeCanRetriveAllReferences()
    {        
        Project project = Project.newInstance("PROJECT-1");
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT-1");
        sut.setProject(project);
        
        assertEquals(1, systemUnderTestDao.getAllReferences(sut).size());
    }
    
    public void testWeCanRetriveAllSpecificationsForAGivenSut()
    {        
        Project project = Project.newInstance("PROJECT-1");
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT-1");
        sut.setProject(project);
        
        assertEquals(2, systemUnderTestDao.getAllSpecifications(sut).size());
    }
}

