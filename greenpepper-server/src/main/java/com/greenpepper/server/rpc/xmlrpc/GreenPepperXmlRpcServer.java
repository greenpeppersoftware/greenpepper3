package com.greenpepper.server.rpc.xmlrpc;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.greenpepper.server.GreenPepperServerErrorKey.ENVTYPES_NOT_FOUND;
import static com.greenpepper.server.GreenPepperServerErrorKey.GENERAL_ERROR;
import static com.greenpepper.server.GreenPepperServerErrorKey.REFERENCE_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REFERENCE_NOT_FOUND;
import static com.greenpepper.server.GreenPepperServerErrorKey.REFERENCE_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REFERENCE_UPDATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_GET_REGISTERED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_REGISTRATION_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_UPDATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REQUIREMENT_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_PROJECTS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_REFERENCE;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_REFERENCES;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_REPOSITORIES;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_REQUIREMENT_REPOS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_SPECIFICATION_REPOS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_SUTS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RUNNERS_NOT_FOUND;
import static com.greenpepper.server.GreenPepperServerErrorKey.RUNNER_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.RUNNER_NOT_FOUND;
import static com.greenpepper.server.GreenPepperServerErrorKey.RUNNER_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.RUNNER_UPDATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.RUN_REFERENCE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATIONS_NOT_FOUND;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATION_ADD_SUT_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATION_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATION_NOT_FOUND;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATION_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATION_REMOVE_SUT_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATION_RUN_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SPECIFICATION_UPDATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUCCESS;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_DELETE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_SET_DEFAULT_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_UPDATE_FAILED;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.GreenPepperServerService;
import com.greenpepper.server.domain.DocumentNode;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.Requirement;
import com.greenpepper.server.domain.RequirementSummary;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;
import com.greenpepper.server.license.LicenseErrorKey;
import com.greenpepper.server.license.Permission;
import com.greenpepper.server.rpc.RpcServerService;
import com.greenpepper.util.StringUtil;

/**
 * The XML-RPC Servlet
 * <p/>
 * Copyright (c) 2006 Pyxis technologies inc. All Rights Reserved.
 *
 * @author jchuet
 */
public class GreenPepperXmlRpcServer implements RpcServerService
{
    private static Logger log = LoggerFactory.getLogger(GreenPepperXmlRpcServer.class);

	private GreenPepperServerService service;

	public GreenPepperXmlRpcServer(GreenPepperServerService service)
    {
		this.service = service;
	}

	public GreenPepperXmlRpcServer()
	{
	}

	public void setService(GreenPepperServerService service)
	{
		this.service = service;
	}

	/**
     * @inheritDoc NO NEEDS TO SECURE THIS
     */
    public String testConnection()
    {
        return SUCCESS;
    }

    /**
     * @inheritDoc NO NEEDS TO SECURE THIS
     */
    public Vector<Object> license()
    {
		try
		{
			return service.license().marshallize();
		}
		catch (GreenPepperServerException e)
		{
			return errorAsVector( e, LicenseErrorKey.LIC_NOT_FOUND);
		}
	}

    /**
     * @inheritDoc NO NEEDS TO SECURE THIS
     */
    public String uploadNewLicense(String license)
    {
        try
        {
            service.uploadNewLicense(license);
			
			return SUCCESS;
        }
        catch (GreenPepperServerException e)
        {
            return errorAsString( e, "" );
        }
        catch (Exception e)
        {
            return errorAsString( e, LicenseErrorKey.FAILED_TO_UPDATE_LIC );
        }
    }

    /**
     * @inheritDoc NO NEEDS TO SECURE THIS
     */
    public String ping(Vector<Object> repositoryParams)
    {
        try
        {
			Repository repository = loadRepository( repositoryParams );

			service.verifyRepositoryPermission(repository, Permission.READ );
			
			return SUCCESS;
        }
        catch (GreenPepperServerException e)
        {
            return errorAsString( e, "" );
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public Vector<Object> getAllEnvironmentTypes()
    {
        try
        {
            List<EnvironmentType> envTypes = service.getAllEnvironmentTypes();

            log.debug("Retrieved All Environment Types number: " + envTypes.size());
            return XmlRpcDataMarshaller.toXmlRpcEnvironmentTypesParameters(envTypes);
        }
        catch (Exception e)
        {
            return errorAsVector(e, ENVTYPES_NOT_FOUND);
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public Vector<Object> getRunner(String name)
    {
        try
        {
            Runner runner = service.getRunner(name);

            log.debug("Retrieved Runner name: " + name);
            return runner.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector(e, RUNNER_NOT_FOUND);
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public Vector<Object> getAllRunners()
    {
        try
        {
            List<Runner> runners = service.getAllRunners();

            log.debug("Retrieved All Runner number: " + runners.size());
            return XmlRpcDataMarshaller.toXmlRpcRunnersParameters(runners);
        }
        catch (Exception e)
        {
            return errorAsVector(e, RUNNERS_NOT_FOUND);
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public String createRunner(Vector<Object> runnerParams)
    {
        try
        {
            Runner runner = XmlRpcDataMarshaller.toRunner(runnerParams);

			service.createRunner(runner);

            log.debug( "Created Runner: " + runner.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
			return errorAsString( e, RUNNER_CREATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public String updateRunner(String oldRunnerName, Vector<Object> runnerParams)
    {
        try
        {
            Runner runner = XmlRpcDataMarshaller.toRunner(runnerParams);

            service.updateRunner(oldRunnerName, runner);

            log.debug( "Updated Runner: " + oldRunnerName );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, RUNNER_UPDATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public String removeRunner(String name)
    {
        try
        {
            service.removeRunner(name);

            log.debug( "Removed Runner: " + name );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, RUNNER_REMOVE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getRegisteredRepository(Vector<Object> repositoryParams)
    {
        try
        {
			Repository repository = loadRepository( repositoryParams );

			repository = service.getRegisteredRepository( repository );

			return repository.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, REPOSITORY_GET_REGISTERED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> registerRepository(Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = XmlRpcDataMarshaller.toRepository( repositoryParams );

			Repository registeredRepository = service.registerRepository(repository);

			log.debug( "Registered Repository: " + registeredRepository.getUid() );
            return registeredRepository.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, REPOSITORY_REGISTRATION_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String updateRepositoryRegistration(Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = XmlRpcDataMarshaller.toRepository( repositoryParams );

			service.updateRepositoryRegistration(repository);
			
			log.debug( "Updated Repository: " + repository.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, REPOSITORY_UPDATE_FAILED );
        }
    }
    
    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public String removeRepository(String repositoryUid)
    {
        try
        {
			service.removeRepository(repositoryUid);

            log.debug( "Removed Repository: " + repositoryUid );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, REPOSITORY_REMOVE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public Vector<Object> getAllProjects()
    {
        try
        {
            List<Project> projects = service.getAllProjects();

            log.debug( "Retrieved All Projects number: " + projects.size() );
            return XmlRpcDataMarshaller.toXmlRpcProjectsParameters( projects );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_PROJECTS );
        }
    }


    /**
     * @inheritDoc
     * NO NEED TO SECURED THIS
     */
    public Vector<Object> getAllSpecificationRepositories()
    {
        try
        {
            Collection<Repository> repositories = service.getAllSpecificationRepositories();

            log.debug( "Retrieved All Specification Repositories number: " + repositories.size() );
            return XmlRpcDataMarshaller.toXmlRpcRepositoriesParameters( repositories );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_SUTS );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getSpecificationRepositoriesOfAssociatedProject(Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            Collection<Repository> repositories = service.getSpecificationRepositoriesOfAssociatedProject(repository.getUid());

            log.debug( "Retrieved Test Repositories Of Associated Project of " + repository.getUid() + " number: " + repositories.size() );
            return XmlRpcDataMarshaller.toXmlRpcRepositoriesParameters( repositories );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_SPECIFICATION_REPOS );
        }
    }
    
    /**
     * @inheritDoc
     * CANT SECURE
     */
    public Vector<Object> getAllRepositoriesForSystemUnderTest(Vector<Object> systemUnderTestParams)
    {
        try
        {
            SystemUnderTest sut = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );
            Collection<Repository> repositories = service.getAllRepositoriesForSystemUnderTest(sut);

            log.debug( "Retrieved All Repositories Of Associated Project of " + sut.getName() + " number: " + repositories.size() );
            return XmlRpcDataMarshaller.toXmlRpcRepositoriesParameters( repositories );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_REPOSITORIES );
        }
    }
    
    /**
     * @inheritDoc
     * CANT SECURE
     */
    public Vector<Object> getSpecificationRepositoriesForSystemUnderTest(Vector<Object> systemUnderTestParams)
    {
        try
        {
            SystemUnderTest sut = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );
            Collection<Repository> repositories = service.getSpecificationRepositoriesForSystemUnderTest(sut);

            log.debug( "Retrieved Test Repositories Of Associated Project of " + sut.getName() + " number: " + repositories.size() );
            return XmlRpcDataMarshaller.toXmlRpcRepositoriesParameters( repositories );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_SPECIFICATION_REPOS );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getRequirementRepositoriesOfAssociatedProject(Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            Collection<Repository> repositories = service.getRequirementRepositoriesOfAssociatedProject(repository.getUid());

            log.debug( "Retrieved Requirement Repositories Of Associated Project of " + repository.getUid() + " number: " + repositories.size() );
            return XmlRpcDataMarshaller.toXmlRpcRepositoriesParameters( repositories );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_REQUIREMENT_REPOS );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getSystemUnderTestsOfAssociatedProject(Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            Collection<SystemUnderTest> suts = service.getSystemUnderTestsOfAssociatedProject(repository.getUid());

            log.debug( "Retrieved SUTs Of Associated Project of " + repository.getUid() + " number: " + suts.size() );
            return XmlRpcDataMarshaller.toXmlRpcSystemUnderTestsParameters( suts );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_SUTS );
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public Vector<Object> getSystemUnderTestsOfProject(String projectName)
    {
        try
        {
			Collection<SystemUnderTest> suts = service.getSystemUnderTestsOfProject(projectName);

            log.debug( "Retrieved SUTs of Project: " + projectName + " number: " + suts.size() );
            return XmlRpcDataMarshaller.toXmlRpcSystemUnderTestsParameters( suts );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_SUTS );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String addSpecificationSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> specificationParams)
    {
        try
        {
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );

            SystemUnderTest sut = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );

			service.addSpecificationSystemUnderTest(sut, specification);
			log.debug( "Added SUT " + sut.getName() + " to SUT list of specification: " + specification.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SPECIFICATION_ADD_SUT_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String removeSpecificationSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> specificationParams)
    {
        try
        {
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );

            SystemUnderTest sut = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );

            service.removeSpecificationSystemUnderTest(sut, specification);

            log.debug( "Removed SUT " + sut.getName() + " to SUT list of specification: " + specification.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SPECIFICATION_REMOVE_SUT_FAILED );
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public String doesSpecificationHasReferences(Vector<Object> specificationParams)
    {
        try
        {
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );
            boolean hasReferences = service.doesSpecificationHasReferences(specification);

            log.debug( "Does Specification " + specification.getName() + "  Has References: " + hasReferences );
            return String.valueOf(hasReferences);
        }
        catch (Exception e)
        {
            return errorAsString( e, String.valueOf( false ) );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getSpecificationReferences(Vector<Object> specificationParams)
    {
        try
        {
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );

            List<Reference> references = service.getSpecificationReferences( specification );

            log.debug( "Retrieved Specification " + specification.getName() + " Test Cases number: " + references.size() );
            return XmlRpcDataMarshaller.toXmlRpcReferencesParameters( references );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_REFERENCES );
        }
    }

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public String doesRequirementHasReferences(Vector<Object> requirementParams)
    {
        try
        {
            Requirement requirement = XmlRpcDataMarshaller.toRequirement( requirementParams );
            boolean hasReferences = service.doesRequirementHasReferences( requirement );

            log.debug( "Does Requirement " + requirement.getName() + " Document Has References: " + hasReferences );
            return String.valueOf(hasReferences);
        }
        catch (Exception e)
        {
            return errorAsString( e, String.valueOf( false ) );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getRequirementReferences(Vector<Object> requirementParams)
    {
        try
        {
            Requirement requirement = XmlRpcDataMarshaller.toRequirement( requirementParams );

            List<Reference> references = service.getRequirementReferences( requirement );

            log.debug( "Retrieved Requirement " + requirement.getName() + " Document References number: " + references.size() );
            return XmlRpcDataMarshaller.toXmlRpcReferencesParameters( references );
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_REFERENCES );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getRequirementSummary(Vector<Object> requirementParams)
    {
        try
        {
            Requirement requirement = XmlRpcDataMarshaller.toRequirement( requirementParams );

            RequirementSummary requirementSummary = service.getRequirementSummary( requirement );

            log.debug( "Retrieved Requirement " + requirement.getName() + " Summary" );
            return requirementSummary.marshallize();
        }
        catch (Exception e)
        {
            return new RequirementSummary().marshallize();
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getReference(Vector<Object> referenceParams)
    {
        try
        {
            Reference reference = XmlRpcDataMarshaller.toReference( referenceParams );

			reference = service.getReference( reference );

			if (reference == null)
            {
                return errorAsVector( null, REFERENCE_NOT_FOUND );
            }

            log.debug( "Retrieved Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName() );
            return reference.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_REFERENCE );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            SystemUnderTest sut = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );

            sut = service.getSystemUnderTest( sut, repository );

            log.debug( "Retrieved SystemUnderTest: " + sut.getName() );
            return sut.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, RETRIEVE_SUTS );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String createSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            SystemUnderTest newSut = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );

			service.createSystemUnderTest(newSut, repository);

            log.debug( "Updated SystemUnderTest: " + newSut.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SUT_CREATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String updateSystemUnderTest(String oldSystemUnderTestName, Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            SystemUnderTest updatedSut = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );

			service.updateSystemUnderTest( oldSystemUnderTestName, updatedSut, repository );

            log.debug( "Updated SystemUnderTest: " + oldSystemUnderTestName );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SUT_UPDATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String removeSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            SystemUnderTest sutToDelete = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );

			service.removeSystemUnderTest(sutToDelete, repository);

            log.debug( "Removed SystemUnderTest: " + sutToDelete.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SUT_DELETE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String setSystemUnderTestAsDefault(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            SystemUnderTest sutToBeDefault = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );

			service.setSystemUnderTestAsDefault( sutToBeDefault, repository );

            log.debug( "Setted as default SystemUnderTest: " + sutToBeDefault.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SUT_SET_DEFAULT_FAILED );
        }
    }

    /**
     * @inheritDoc
     * NOT SECURED FOR SYNCHRONIZATION PURPOSES
     */
    public String removeRequirement(Vector<Object> requirementParams)
    {
        try
        {
            Requirement requirement = XmlRpcDataMarshaller.toRequirement( requirementParams );

			service.removeRequirement(requirement);

            log.debug( "Removed Requirement: " + requirement.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, REQUIREMENT_REMOVE_FAILED );
		}
	}

    /**
     * @inheritDoc
     * NO NEEDS TO SECURE THIS
     */
    public Vector<Object> getSpecification(Vector<Object> specificationParams)
    {
        try
        {
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );

			specification = service.getSpecification(specification);

			if (specification == null)
			{
				return XmlRpcDataMarshaller.errorAsVector(SPECIFICATION_NOT_FOUND);
			}
			else
			{
				log.debug( "Specification found: " + specification.getName() );
				return specification.marshallize();
			}
		}
        catch (Exception e)
        {
            return errorAsVector( e, SPECIFICATION_NOT_FOUND );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getSpecifications(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams)
    {
        try
        {
            Repository repository = loadRepository( repositoryParams );

            SystemUnderTest systemUnderTest = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );
            List<Specification> specifications = service.getSpecifications( systemUnderTest, repository );

            log.debug( "Retrieved specifications for sut: " + systemUnderTest.getName() + " and repoUID:" + repository.getUid() );
            return XmlRpcDataMarshaller.toXmlRpcSpecificationsParameters( specifications );
        }
        catch (Exception e)
        {
            return errorAsVector( e, SPECIFICATIONS_NOT_FOUND );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> getListOfSpecificationLocations(String repositoryUID, String systemUnderTestName)
    {
        try
        {
			Repository repository = service.getRepository( repositoryUID, null );
			
			Vector<Object> locations = service.getListOfSpecificationLocations( repositoryUID, systemUnderTestName );

            log.debug( "Retrieved specification list: " + repository.getName() );
            return locations;
        }
        catch (Exception e)
        {
            return errorAsVector( e, SPECIFICATIONS_NOT_FOUND );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    @SuppressWarnings("unchecked")
    public Vector<Object> getSpecificationHierarchy(Vector<Object> repositoryParams, Vector<Object> sutParams)
    {
        try
        {
			Repository repository = loadRepository( repositoryParams );
			SystemUnderTest systemUnderTest = XmlRpcDataMarshaller.toSystemUnderTest( sutParams );

            DocumentNode hierarchy = service.getSpecificationHierarchy( repository,  systemUnderTest );

            return hierarchy.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, SPECIFICATIONS_NOT_FOUND );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> createSpecification(Vector<Object> specificationParams)
    {
        try
        {
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );

            specification = service.createSpecification( specification );

            log.debug( "Created specification: " + specification.getName() );
            return specification.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, SPECIFICATION_CREATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * NOT SECURED FOR SYNCHRONIZATION PURPOSES
     */
    public String updateSpecification(Vector<Object> oldSpecificationParams, Vector<Object> newSpecificationParams)
    {
        try
        {
            Specification oldSpecification = XmlRpcDataMarshaller.toSpecification( oldSpecificationParams );
            Specification newSpecification = XmlRpcDataMarshaller.toSpecification( newSpecificationParams );

            service.updateSpecification(oldSpecification,  newSpecification);

            log.debug( "Updated Specification: " + oldSpecification.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SPECIFICATION_UPDATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * NOT SECURED FOR SYNCHRONIZATION PURPOSES
     */
    public String removeSpecification(Vector<Object> specificationParams)
    {
        try
        {
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );

			service.removeSpecification( specification );

            log.debug( "Removed specification: " + specification.getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, SPECIFICATION_REMOVE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String createReference(Vector<Object> referenceParams)
    {
        try
        {
            Reference reference = XmlRpcDataMarshaller.toReference( referenceParams );

            service.createReference( reference );

            log.debug( "Created Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName() );
            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, REFERENCE_CREATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> updateReference(Vector<Object> oldReferenceParams, Vector<Object> newReferenceParams)
    {
        try
        {
            Reference oldReference = XmlRpcDataMarshaller.toReference( oldReferenceParams );

            Reference newReference = XmlRpcDataMarshaller.toReference( newReferenceParams );

			newReference = service.updateReference( oldReference,  newReference );

            log.debug( "Updated Reference: " + newReference.getRequirement().getName() + "," + newReference.getSpecification().getName() );

            return newReference.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, REFERENCE_UPDATE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public String removeReference(Vector<Object> referenceParams)
    {
        try
        {
            Reference reference = XmlRpcDataMarshaller.toReference( referenceParams );

            service.removeReference( reference );

            log.debug( "Removed Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName() );

            return SUCCESS;
        }
        catch (Exception e)
        {
            return errorAsString( e, REFERENCE_REMOVE_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> runSpecification(Vector<Object> systemUnderTestParams, Vector<Object> specificationParams, boolean implementedVersion, String locale)
    {
        try
        {
            SystemUnderTest systemUnderTest = XmlRpcDataMarshaller.toSystemUnderTest( systemUnderTestParams );
            Specification specification = XmlRpcDataMarshaller.toSpecification( specificationParams );

			Execution exe = service.runSpecification( systemUnderTest, specification, implementedVersion, locale );
            log.debug( "Runned Specification: " + specification.getName() + " ON System: " + systemUnderTest.getName() );

            return exe.marshallize();
        }
        catch (Exception e)
        {
            return errorAsVector( e, SPECIFICATION_RUN_FAILED );
        }
    }

    /**
     * @inheritDoc
     * SECURED
     */
    public Vector<Object> runReference(Vector<Object> referenceParams, String locale)
    {
        try
        {
            Reference reference = XmlRpcDataMarshaller.toReference( referenceParams );

            reference = service.runReference( reference,  locale );

			log.debug( "Runned Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName() +
					   " ON System: " + reference.getSystemUnderTest().getName());

            return reference.marshallize();
		}
        catch (Exception e)
        {
            return errorAsVector( e, RUN_REFERENCE_FAILED );
        }
    }

    private Repository loadRepository(Vector<Object> repositoryParams) throws GreenPepperServerException
    {
        return loadRepository( XmlRpcDataMarshaller.toRepository( repositoryParams ) );
    }

    private Repository loadRepository(Repository repo) throws GreenPepperServerException
    {
		return service.getRepository( repo.getUid(), repo.getMaxUsers() );
    }
    
    private Vector<Object> errorAsVector(Exception e, String id)
    {
        if (e != null)
        {
            log.info( e.getMessage() );
            log.debug( e.getMessage(), e );
        }
        else log.error( id );

        String returnValue = errorReturnValue( e, id );
        return XmlRpcDataMarshaller.errorAsVector( returnValue );
    }

    private String errorAsString(Exception e, String value)
    {
        if (e != null)
        {
            log.info( e.getMessage() );
            log.debug( e.getMessage(), e );
        }
        else log.error( value );

        String returnValue = errorReturnValue( e, value );
        return XmlRpcDataMarshaller.errorAsString( returnValue );
    }

    private String errorReturnValue(Exception e, String value)
    {
        String returnValue = value;

        if (e != null && e instanceof GreenPepperServerException)
        {
            String id = ((GreenPepperServerException) e).getId();

            if (!StringUtil.isEmpty( id ))
            {
                returnValue = id;
            }
            else
            {
                returnValue = GENERAL_ERROR;
            }
        }

        return returnValue;
    }
}