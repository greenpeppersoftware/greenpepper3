package com.greenpepper.server.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;

import junit.framework.TestCase;

public class ProjectTest extends TestCase
{

    public void testTheBasicBehaviorOfTheEquals()
    {
        assertFalse(Project.newInstance("PROJECT-1").equals(null));
        assertFalse(Project.newInstance("PROJECT-1").equals(new Integer(0)));
        assertFalse(Project.newInstance("PROJECT-1").equals(Project.newInstance("PROJECT-2")));
        assertTrue(Project.newInstance("PROJECT-1").equals(Project.newInstance("PROJECT-1")));
    }

    public void testThatTheProjectIsAlphaComparable()
    {
        assertEquals(0, Project.newInstance("PROJECT-1").compareTo(Project.newInstance("PROJECT-1")));
        assertEquals(-1, Project.newInstance("PROJECT-1").compareTo(Project.newInstance("PROJECT-2")));
        assertEquals(1, Project.newInstance("PROJECT-2").compareTo(Project.newInstance("PROJECT-1")));
    }

    public void testThatTwoProjectWithDifferentNamesAreNotEqual()
    {
        Project project1 = Project.newInstance("NAME");
        Project project2 = Project.newInstance("DIFFERENT NAME");

        assertFalse(project1.equals(project2));
    }

    public void testThatAddingARepositoryItWillBeInTheRepositoriesList() throws GreenPepperServerException
    {
        Project project = Project.newInstance("NAME");
        Repository repository = Repository.newInstance("UID");
        project.addRepository(repository);

        assertTrue(project.getRepositories().contains(repository));
        assertEquals(project, repository.getProject());
    }

    public void testThatAddingARepositoryThatIsAlreadyInTheRepositoriesListWillTriggerAnAxception()
    {
        Repository repository = Repository.newInstance("UID");
        repository.setName("TheRepo");
        Set<Repository> repositories = new HashSet<Repository>();
        repositories.add(repository);

        Project project = Project.newInstance("NAME");
        project.setRepositories(repositories);
        try
        {
            project.addRepository(repository);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.REPOSITORY_ALREADY_EXISTS, e.getId());
        }
    }

    public void testThatRemovingARepositoryItWillBeNoLongerInTheRepositoriesList() throws GreenPepperServerException
    {
        Repository repository = Repository.newInstance("UID");
        Set<Repository> repositories = new HashSet<Repository>();
        repositories.add(repository);

        Project project = Project.newInstance("NAME");
        project.setRepositories(repositories);
        project.removeRepository(repository);

        assertFalse(project.getRepositories().contains(repository));
        assertNull(repository.getProject());
    }

    public void testThatRemovingARepositoryThatIsNotInTheRepositoriesListWillTriggerAnAxception()
    {
        Project project = Project.newInstance("NAME");
        Repository repository = Repository.newInstance("UID");

        try
        {
            project.removeRepository(repository);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.REPOSITORY_NOT_FOUND, e.getId());
        }
    }

    public void testThatWeCanNotAddANewRepositoryWithTheSameNameAsOneAlreadyPresent() throws Exception
    {
        Project project = Project.newInstance("NAME");
        Repository repo1 =  Repository.newInstance("UID1");
        repo1.setName("REPO-1");

        Repository repo2 =  Repository.newInstance("UID2");
        repo2.setName("repo-1");

        project.addRepository(repo1);

        try
        {
            project.addRepository(repo2);
            fail("Should not be able to add a second Repo with same name");
        }
        catch (GreenPepperServerException ex)
        {
            assertEquals(GreenPepperServerErrorKey.REPOSITORY_ALREADY_EXISTS, ex.getId());
        }
    }

    public void testThatAddingASUTItWillBeInTheSUTsList() throws GreenPepperServerException
    {
        Project project = Project.newInstance("NAME");
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT");
        project.addSystemUnderTest(sut);

        assertTrue(project.getSystemUnderTests().contains(sut));
    }

    public void testThatAddingASUTThatIsAlreadyInTheSUTsListWillTriggerAnAxception()
    {
        Project project = Project.newInstance("NAME");
        Set<SystemUnderTest> suts = new HashSet<SystemUnderTest>();

        SystemUnderTest sut = SystemUnderTest.newInstance("SUT");
        sut.setProject(project);
        suts.add(sut);
        project.setSystemUnderTests(suts);

        try
        {
            project.addSystemUnderTest(sut);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.SUT_ALREADY_EXISTS, e.getId());
        }
    }

    public void testThatRemovingASUTItWillBeNoLongerInTheSUTsList() throws GreenPepperServerException
    {
        Project project = Project.newInstance("NAME");
        Set<SystemUnderTest> suts = new HashSet<SystemUnderTest>();

        SystemUnderTest sut = SystemUnderTest.newInstance("SUT");
        sut.setProject(project);
        suts.add(sut);
        project.setSystemUnderTests(suts);

        project.removeSystemUnderTest(sut);
        assertFalse(project.getSystemUnderTests().contains(sut));
        assertNull(sut.getProject());
    }

    public void testThatRemovingASUTThatIsNotInTheSUTsListWillTriggerAnAxception()
    {
        Project project = Project.newInstance("NAME");
        Set<SystemUnderTest> suts = new HashSet<SystemUnderTest>();
        SystemUnderTest sut = SystemUnderTest.newInstance("SUT");
        sut.setProject(project);
        suts.add(sut);

        try
        {
            project.removeSystemUnderTest(sut);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.SUT_NOT_FOUND, e.getId());
        }
    }

    public void testThatTheWeCanRetrieveTheDefaultSUT()
    {
        Project project = Project.newInstance("NAME");
        Set<SystemUnderTest> suts = new HashSet<SystemUnderTest>();
        SystemUnderTest someSut = SystemUnderTest.newInstance("SUT");
        someSut.setProject(project);
        SystemUnderTest defaultSut = SystemUnderTest.newInstance("SUT-DEFAULT");
        defaultSut.setProject(project);
        defaultSut.setIsDefault(true);

        suts.add(someSut);
        suts.add(defaultSut);
        project.setSystemUnderTests(suts);

        assertEquals(defaultSut, project.getDefaultSystemUnderTest());
    }

    public void testThatWeCanNotAddAnewSUTWithTheSameNameAsOneAlreadyPresent() throws Exception
    {
        Project project = Project.newInstance("NAME");
        project.addSystemUnderTest(SystemUnderTest.newInstance("SUT-1"));
        project.addSystemUnderTest(SystemUnderTest.newInstance("SUT-2"));

        try
        {
            project.addSystemUnderTest(SystemUnderTest.newInstance("sut-1"));
            fail("Should not be able to add a second SUT with same name");
        }
        catch (GreenPepperServerException ex)
        {
            assertEquals(GreenPepperServerErrorKey.SUT_ALREADY_EXISTS, ex.getId());
        }
    } 
    
    public void testThatAProjectIsProperlyMarshalled()
    {
        Project project = Project.newInstance("PROJECT-1");
        Vector<Object> params = new Vector<Object>();
        params.add(XmlRpcDataMarshaller.PROJECT_NAME_IDX, "PROJECT-1");
        assertEquals(params, project.marshallize());
    }
}
