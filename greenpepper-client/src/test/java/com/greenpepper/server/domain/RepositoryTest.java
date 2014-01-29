package com.greenpepper.server.domain;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.domain.component.ContentType;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import com.greenpepper.util.TestCase;


public class RepositoryTest extends TestCase
{
    private Repository repository1, repository2;

    public RepositoryTest()
    {
        repository1 = Repository.newInstance("UID-1");
        repository2 = Repository.newInstance("UID-2");
    }

    public void testBasicEqualsBehavior()
    {
        assertFalse(repository1.equals(null));
        assertFalse(repository1.equals(new Integer(0)));
        assertTrue(repository1.equals(repository1));
        assertFalse(repository1.equals(repository2));
    }

    public void testAddingARequirementItWillBeInTheRequirementsList() throws GreenPepperServerException
    {
        Repository repository = Repository.newInstance("UID");
        Requirement requirement = Requirement.newInstance("REQ");
        repository.addRequirement(requirement);

        assertTrue(repository.getRequirements().contains(requirement));
        assertEquals(repository, requirement.getRepository());
    }

    public void testAddingARequirementIsAlreadyInTheRequirementsListWillTriggerAnAxception()
    {
        Set<Requirement> requirements = new HashSet<Requirement>();
        Repository repository = Repository.newInstance("UID");
        Requirement requirement = Requirement.newInstance("REQ");
        requirements.add(requirement);
        repository.setRequirements(requirements);

        try
        {
            repository.addRequirement(requirement);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.REQUIREMENT_ALREADY_EXISTS, e.getId());
        }
    }

    public void testAddingARequirementWithTheSameNameWillTriggerAnAxception()
    {
        Set<Requirement> requirements = new HashSet<Requirement>();
        Repository repository = Repository.newInstance("UID");

        Requirement requirement = Requirement.newInstance("REQ");
        requirement.setName("NAME");
        requirements.add(requirement);
        repository.setRequirements(requirements);

        requirement = Requirement.newInstance("REQ2");
        requirement.setName("name");

        try
        {
            repository.addRequirement(requirement);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.REQUIREMENT_ALREADY_EXISTS, e.getId());
        }
    }

    public void testRemovingARequirementItWillBeNoLongerInTheRequirementsList() throws GreenPepperServerException
    {
        Set<Requirement> requirements = new HashSet<Requirement>();
        Repository repository = Repository.newInstance("UID");
        Requirement requirement = Requirement.newInstance("REQ");
        requirements.add(requirement);
        repository.setRequirements(requirements);

        repository.removeRequirement(requirement);
        assertFalse(repository.getRequirements().contains(requirement));
        assertNull(requirement.getRepository());
    }

    public void testRemovingARequirementIsNotInTheRequirementsListWillTriggerAnAxception()
    {
        Repository repository = Repository.newInstance("UID");
        Requirement requirement = Requirement.newInstance("REQ");

        try
        {
            repository.removeRequirement(requirement);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.REQUIREMENT_NOT_FOUND, e.getId());
        }
    }

    public void testAddingASpecificationItWillBeInTheSpecificationsList() throws GreenPepperServerException
    {
        Repository repository = Repository.newInstance("UID");
        Specification specification = Specification.newInstance("SPEC");
        repository.addSpecification(specification);

        assertTrue(repository.getSpecifications().contains(specification));
        assertEquals(repository, specification.getRepository());
    }

    public void testAddingASpecificationIsAlreadyInTheSpecificationsListWillTriggerAnAxception()
    {
        Set<Specification> specifications = new HashSet<Specification>();
        Repository repository = Repository.newInstance("UID");
        Specification specification = Specification.newInstance("SPEC");
        specifications.add(specification);
        repository.setSpecifications(specifications);

        try
        {
            repository.addSpecification(specification);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.SPECIFICATION_ALREADY_EXISTS, e.getId());
        }
    }

    public void testAddingASpecificationWithTheSameNameWillTriggerAnAxception()
    {
        Set<Specification> specifications = new HashSet<Specification>();
        Repository repository = Repository.newInstance("UID");
        Specification specification = Specification.newInstance("SPEC");
        specification.setName("NAME");
        specifications.add(specification);
        repository.setSpecifications(specifications);

        specification = Specification.newInstance("SPEC2");
        specification.setName("name");
        try
        {
            repository.addSpecification(specification);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.SPECIFICATION_ALREADY_EXISTS, e.getId());
        }
    }


    public void testRemovingASpecificationItWillBeNoLongerInTheSpecificationsList() throws GreenPepperServerException
    {
        Set<Specification> specifications = new HashSet<Specification>();
        Repository repository = Repository.newInstance("UID");
        Specification specification = Specification.newInstance("SPEC");
        specifications.add(specification);
        repository.setSpecifications(specifications);

        repository.removeSpecification(specification);
        assertFalse(repository.getSpecifications().contains(specification));
        assertNull(specification.getRepository());
    }

    public void testRemovingASpecificationIsNotInTheSpecificationsListWillTriggerAnAxception()
    {
        Repository repository = Repository.newInstance("UID");
        Specification specification = Specification.newInstance("SPEC");

        try
        {
            repository.removeSpecification(specification);
            fail();
        }
        catch (GreenPepperServerException e)
        {
            assertEquals(GreenPepperServerErrorKey.SPECIFICATION_NOT_FOUND, e.getId());
        }
    }

    public void testARepositoryIsProperlyMarshalled()
    {  
    	EnvironmentType JAVA = EnvironmentType.newInstance("JAVA");
        Repository repository = Repository.newInstance("UID");
        repository.setProject(Project.newInstance("PROJECT-1"));
        repository.setName("REPOSITORY");
        repository.setContentType(ContentType.BOTH);
        repository.setBaseUrl("BASE-URL-1");
        repository.setBaseRepositoryUrl("REPO-URL-1");
        repository.setBaseTestUrl("TEST-URI-1");
        repository.setUsername("GreenPepper");
        repository.setPassword("GreenPepper");
        RepositoryType type = RepositoryType.newInstance("FILE");
        type.registerClassForEnvironment("REPO-CLASS",JAVA);
        type.setDocumentUrlFormat("%s%s");
        type.setTestUrlFormat("%s%s");
        repository.setType(type);
        repository.setMaxUsers(50);

        Vector<Object> params = new Vector<Object>();
        params.add(XmlRpcDataMarshaller.REPOSITORY_NAME_IDX, "REPOSITORY");
        params.add(XmlRpcDataMarshaller.REPOSITORY_UID_IDX, "UID");
        Vector<Object> pparams = new Vector<Object>();
        pparams.add(XmlRpcDataMarshaller.PROJECT_NAME_IDX, "PROJECT-1");
        params.add(XmlRpcDataMarshaller.REPOSITORY_PROJECT_IDX, pparams);
        Vector<Object> tparams = new Vector<Object>();
        tparams.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_NAME_IDX, "FILE");
        
        Hashtable<String, String> cparams = new Hashtable<String, String>();
        cparams.put("JAVA", "REPO-CLASS");
        tparams.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_REPOCLASSES_IDX, cparams);
        
        tparams.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_NAME_FORMAT_IDX, "%s%s");
        tparams.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_URI_FORMAT_IDX, "%s%s");
        params.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_IDX, tparams);
        params.add(XmlRpcDataMarshaller.REPOSITORY_CONTENTTYPE_IDX, "BOTH");
        params.add(XmlRpcDataMarshaller.REPOSITORY_BASE_URL_IDX, "BASE-URL-1");
        params.add(XmlRpcDataMarshaller.REPOSITORY_BASEREPO_URL_IDX, "REPO-URL-1");
        params.add(XmlRpcDataMarshaller.REPOSITORY_BASETEST_URL_IDX, "TEST-URI-1");
        params.add(XmlRpcDataMarshaller.REPOSITORY_USERNAME_IDX, "GreenPepper");
        params.add(XmlRpcDataMarshaller.REPOSITORY_PASSWORD_IDX, "GreenPepper");
        params.add(XmlRpcDataMarshaller.REPOSITORY_MAX_USERS_IDX, 50);
        
        assertEquals(params, repository.marshallize());
    }
}
