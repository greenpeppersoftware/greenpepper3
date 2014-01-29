package com.greenpepper.server.domain.dao.hibernate;

import java.util.List;

import org.hibernate.Transaction;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.dao.ProjectDao;

public class HibernateProjectDaoTest extends AbstractDBUnitHibernateMemoryTest
{
    private static final String DATAS = "/dbunit/datas/HibernateProjectDaoTest.xml";
    private ProjectDao projectDao;

    protected void setUp() throws Exception
    {
        super.setUp();
        insertIntoDatabase(DATAS);        
        projectDao = new HibernateProjectDao(this);
	}
    
    protected void tearDown() throws Exception
    {
        deleteFromDatabase(DATAS);
        super.tearDown();
    }

    public void testThatAProjectCanBeSelectedbyName() throws GreenPepperServerException
    {
        Project projectByName = projectDao.getByName("PROJECT-1");
        assertNotNull(projectByName);
    }

    public void testThatSelectByNameThrowsAnExceptionIfTheProjectIsNotPresisted()
    {
        assertNull(projectDao.getByName("PROJECT-NOT-FOUND"));
    }
    
    public void testThatAProjectCanBeCreated()
			throws GreenPepperServerException {
        session.getTransaction().begin();
        Project projectCreated = projectDao.create("PROJECT-CREATED");
        session.getTransaction().commit();
        
        assertNotNull(getById(Project.class, projectCreated.getId()));
        assertEquals("PROJECT-CREATED", projectCreated.getName());
    }
    
    public void testThatTheUnicityOfTheProjectNameOnCreation()
    {
        try
        {
            projectDao.create("PROJECT-1");
            fail();
        }
		catch (GreenPepperServerException ex) {
			assertEquals(GreenPepperServerErrorKey.PROJECT_ALREADY_EXISTS, ex.getId());
		}
	}
    
    public void testThatTheRepositoryAreWellAssociated() throws GreenPepperServerException
    {
        Project project = projectDao.getByName("PROJECT-1");
        assertEquals(1, project.getRepositories().size());
    }
    
    public void testThatWeCanRetrieveAllProjects()
    {
        List<Project> projects = projectDao.getAll();
        assertEquals(6, projects.size());
    }

	public void testThatWeCanRemoveAProjectThatIsNotAssociatedToEntities() throws GreenPepperServerException
	{
		int numberOfProjects = projectDao.getAll().size();
		Transaction transaction = session.beginTransaction();
		projectDao.remove("PROJECT-TO-REMOVE-NOASSOCIATIONS");
		transaction.commit();
		assertEquals(numberOfProjects - 1, projectDao.getAll().size());
	}

	public void testThatWeCannotRemoveAProjectAssociatedToARepository() throws GreenPepperServerException
	{
		Project projectToRemove = projectDao.getByName("PROJECT-TO-REMOVE-5");
		assertEquals(1, projectToRemove.getRepositories().size());

		try
		{
			projectDao.remove(projectToRemove.getName());
			fail();
		}
		catch (GreenPepperServerException ex)
		{
			assertEquals(GreenPepperServerErrorKey.PROJECT_REPOSITORY_ASSOCIATED, ex.getId());
		}
	}

	public void testThatWeCannotRemoveAProjectAssociatedToSUTS() throws GreenPepperServerException
	{
		Project projectToRemove = projectDao.getByName("PROJECT-TO-REMOVE-6");
		assertEquals(1, projectToRemove.getSystemUnderTests().size());

		try
		{
			projectDao.remove(projectToRemove.getName());
			fail();
		}
		catch (GreenPepperServerException ex)
		{
			assertEquals(GreenPepperServerErrorKey.PROJECT_SUTS_ASSOCIATED, ex.getId());
		}
	}

	public void testWeCantUpdateANotFoundProject()
	{
		try
		{
			session.getTransaction().begin();
			projectDao.update("PROJECT-NOT-FOUND", Project.newInstance("TO-SOME-PROJECT"));
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
			assertEquals(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, e.getId());
		}
	}

	public void testWeCantUpdateAProjectToAnAlreadyUsedName()
	{
		try
		{
			session.getTransaction().begin();
			projectDao.update("PROJECT-1", Project.newInstance("PROJECT-2"));
			session.getTransaction().commit();
			fail();
		}
		catch (GreenPepperServerException e)
		{
			assertEquals(GreenPepperServerErrorKey.PROJECT_ALREADY_EXISTS, e.getId());
		}
	}

	public void testWeCanUpdateAProject()
			throws GreenPepperServerException
	{

		session.getTransaction().begin();
		Project projectUpdated = projectDao.update("PROJECT-1", Project.newInstance("PROJECT-1-UPDATED"));
		session.getTransaction().commit();

		assertNotNull(getById(Project.class, projectUpdated.getId()));
		assertEquals("PROJECT-1-UPDATED", projectUpdated.getName());
	}
}