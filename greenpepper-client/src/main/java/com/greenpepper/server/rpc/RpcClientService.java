package com.greenpepper.server.rpc;

import java.util.Set;

import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.ServerPropertiesManager;
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
import com.greenpepper.server.license.LicenseBean;

/**
 * The GreenPepper Client interface.
 * All available methods are documented here.
 * <p/>
 * Copyright (c) 2006 Pyxis technologies inc. All Rights Reserved.
 * @author JCHUET
 */
public interface RpcClientService
{
    /**
     * Retrieves the GreenpPepper Server license.
     * <p/>
     * @param identifier
     * @return the GreenpPepper Server license.
     * @throws GreenPepperServerException
     */
    public LicenseBean license(String identifier) throws GreenPepperServerException;

    /**
     * Uploads the new GreenpPepper Server license.
     * <p/>
     * @param newLicence
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void uploadLicense(String newLicence, String identifier) throws GreenPepperServerException;

    /**
     * Tests the connection at the url and handler.
     * </p>
     * @param url
     * @param handler
     * @return true if server successfully pinged.
     * @throws GreenPepperServerException
     */
    public boolean testConnection(String url, String handler) throws GreenPepperServerException;

    /**
     * Pings the server.
     * </p>
     * @param repository
     * @param identifier
     * @return true if server successfully pinged.
     * @throws GreenPepperServerException
     */
    public boolean ping(Repository repository, String identifier) throws GreenPepperServerException;
    
    /**
     * Retrieves the EnvironmentTypes available.
     * <p/>
     * @param identifier
     * @return all the EnvironmentTypes available.
     * @throws GreenPepperServerException
     */
    public Set<EnvironmentType> getAllEnvironmentTypes(String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the Runner for a given name.
     * <p/>
     * @param name
     * @param identifier
     * @return the Runner for a given name.
     * @throws GreenPepperServerException
     */
    public Runner getRunner(String name, String identifier) throws GreenPepperServerException;
    
    /**
     * Retrieves the Runners available.
     * <p/>
     * @param identifier
     * @return the all Runners available.
     * @throws GreenPepperServerException
     */
    public Set<Runner> getAllRunners(String identifier) throws GreenPepperServerException;
    
    /**
     * Creates a new Runner
     * <p/>
     * @param runner
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void createRunner(Runner runner, String identifier) throws GreenPepperServerException;
    
    /**
     * Updates the Runner
     * <p/>
     * @param oldRunnerName
     * @param runner
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void updateRunner(String oldRunnerName, Runner runner, String identifier) throws GreenPepperServerException;
    
    /**
     * Removes the Runner of the given name
     * <p/>
     * @param name
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void removeRunner(String name, String identifier) throws GreenPepperServerException;
    
    /**
     * Checks if registered and Retrieves the Repository.
     * <p/>
     * @param repository
     * @param identifier
     * @return the registered Repository.
     * @throws GreenPepperServerException
     */
    public Repository getRegisteredRepository(Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Registers the repository in GreenPepper-server.
     * If project not found it will be created.
     * @param repository
     * @param identifier
     * @return the registered repository.
     * @throws GreenPepperServerException
     */
    public Repository registerRepository(Repository repository, String identifier) throws GreenPepperServerException;


    /**
     * Updates the Repository Registration.
     * If project not found it will be created.
     * <p/>
     * @param repository
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void updateRepositoryRegistration(Repository repository, String identifier) throws GreenPepperServerException;
    
    /**
     * Removes the Repository if this one doesnt hold any specifications.
     * <p/>
     * @param repositoryUid
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void removeRepository(String repositoryUid, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the complete project list.
     * <p/>
     * @param identifier
     * @return the complete project list.
     * @throws GreenPepperServerException
     */
    public Set<Project> getAllProjects(String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the Specification repository list for the project associated with
     * the specified repository or an error id in a Hastable if an error occured.
     * <p/>
     * @param repository
     * @param identifier
     * @return the Specification repository list for the project associated with
     * the specified repository or an error id in a Hastable if an error occured.
     * @throws GreenPepperServerException
     */
    public Set<Repository> getSpecificationRepositoriesOfAssociatedProject(Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the Specification repository list for the project associated with
     * the specified system under test or an error id in a Hastable if an error occured.
     * <p/>
     * @param systemUnderTest
     * @param identifier
     * @return the Specification repository list for the project associated with
     * the specified systemUnderTest or an error id in a Hastable if an error occured.
     * @throws GreenPepperServerException
     */
    public Set<Repository> getSpecificationRepositoriesOfAssociatedProject(SystemUnderTest systemUnderTest, String identifier) throws GreenPepperServerException;
    
    /**
     * Retrieves all the Specification repositorys list by project
     * or an error id in a Hastable if an error occured.
     * <p/>
     * @param identifier
     * @return the Specification repository list for the project
     * or an error id in a Hastable if an error occured.
     * @throws GreenPepperServerException
     */
    public Set<Repository> getAllSpecificationRepositories(String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the Repository list for the project associated with
     * the specified system under test or an error id in a Hastable if an error occured.
     * <p/>
     * @param systemUnderTest
     * @param identifier
     * @return the repository list for the project associated with
     * the specified systemUnderTest or an error id in a Hastable if an error occured.
     * @throws GreenPepperServerException
     */
    public Set<Repository> getAllRepositoriesForSystemUnderTest(SystemUnderTest systemUnderTest, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the Requirement repository list for the project associated with
     * the specified repository or an error id in a Hastable if an error occured.
     * <p/>
     * @param repository
     * @param identifier
     * @return the Requirement repository list for the project associated with
     * the specified repository or an error id in a Hastable if an error occured.
     * @throws GreenPepperServerException
     */
    public Set<Repository> getRequirementRepositoriesOfAssociatedProject(Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the SystemUnderTest list for the project associated with
     * the specified repository or an error id in a Hastable if an error occured.
     * <p/>
     * @param repository
     * @param identifier
     * @return the SystemUnderTest list for for the project associated with
     * the specified repository or an error id in a Hastable if an error occured.
     * @throws GreenPepperServerException
     */
    public Set<SystemUnderTest> getSystemUnderTestsOfAssociatedProject(Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the SystemUnderTest list for the project associated.
     * <p/>
     * @param projectName
     * @param identifier
     * @return the SystemUnderTest list for for the project.
     * @throws GreenPepperServerException
     */
    public Set<SystemUnderTest> getSystemUnderTestsOfProject(String projectName, String identifier) throws GreenPepperServerException;

    /**
     * Adds the SystemUnderTest to the SystemUnderTest list of the Specification.
     * <p/>
     * @param systemUnderTest
     * @param specification
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void addSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification, String identifier) throws GreenPepperServerException;

    /**
     * Removes the SystemUnderTest to the SystemUnderTest list of the Specification.
     * <p/>
     * @param systemUnderTest
     * @param specification
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void removeSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification, String identifier) throws GreenPepperServerException;

    /**
     * Checks if the Specification is in atleast one Reference.
     * <p/>
     * @param Specification
     * @return true if the specification is in atleast one Reference.
     * @throws GreenPepperServerException
     */
    public boolean hasReferences(Specification specification, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the References list of the specified Specification
     * <p/>
     * @param specification
     * @param identifier
     * @return the References list of the specified Specification
     * @throws GreenPepperServerException
     */
    public Set<Reference> getReferences(Specification specification, String identifier) throws GreenPepperServerException;

    /**
     * Checks if the Requirement is in atleast one Reference.
     * <p/>
     * @param requirement
     * @return true if the Requirement is in atleast one Reference.
     * @throws GreenPepperServerException
     */
    public boolean hasReferences(Requirement requirement, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the References list of the specified requirement
     * <p/>
     * @param requirementParams
     * @param identifier
     * @return the References list of the specified requirement
     * @throws GreenPepperServerException
     */
    public Set<Reference> getReferences(Requirement requirement, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the Reference.
     * </p>
     * @param Reference
     * @param identifier
     * @return the Reference.
     * @throws GreenPepperServerException
     */
    public Reference getReference(Reference reference, String identifier) throws GreenPepperServerException;

    /**
     * Creates a new SystemUnderTest.
     * </p>
     * @param systemUnderTest
     * @param repository
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void createSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the SystemUnderTest.
     * </p>
     * @param systemUnderTest
     * @param repository
     * @param identifier
     * @return SystemUnderTest
     * @throws GreenPepperServerException
     */
    public SystemUnderTest getSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Updates the SystemUnderTest.
     * </p>
     * @param oldsystemUnderTestName
     * @param newsystemUnderTest
     * @param repository
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void updateSystemUnderTest(String oldsyStemUnderTestName, SystemUnderTest newSystemUnderTest, Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Removes the SystemUnderTest.
     * </p>
     * @param systemUnderTest
     * @param repository
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void removeSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Sets the systemUnderTest as the project default SystemUnderTest
     * <p/>
     * @param systemUnderTest
     * @param repository
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void setSystemUnderTestAsDefault(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException;

    /**
     * Removes the Requirement
     * <p/>
     * @param requirement
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void removeRequirement(Requirement requirement, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the specification
     * <p/>
     * @param specification
     * @param identifier
     * @return the specification
     * @throws GreenPepperServerException
     */
    public Specification getSpecification(Specification specification, String identifier) throws GreenPepperServerException;

    /**
     * Creates the Specification
     * <p/>
     * @param specification
     * @param identifier
     * @return the new Specification
     * @throws GreenPepperServerException
     */
    public Specification createSpecification(Specification specification, String identifier) throws GreenPepperServerException;

    /**
     * Updates the Specification
     * <p/>
     * @param oldSpecification
     * @param newSpecification
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void updateSpecification(Specification oldSpecification, Specification newSpecification, String identifier) throws GreenPepperServerException;

    /**
     * Removes the Specification
     * <p/>
     * @param Specification
     * @param identifier
     * @throws GreenPepperServerException
     */
    public void removeSpecification(Specification specification, String identifier) throws GreenPepperServerException;

    /**
     * Creates a Reference
     * <p/>
     * @param Reference
     * @throws GreenPepperServerException
     */
    public void createReference(Reference reference, String identifier) throws GreenPepperServerException;

    /**
     * Update the Reference.
     * The Old one will be deleted based on the oldReferenceParams and a new One
     * will be created based on the newReferenceParams.
     * <p/>
     * @param oldReference
     * @param newReference
     * @param identifier
     * @return the updated Reference.
     * @throws GreenPepperServerException
     */
    public Reference updateReference(Reference oldReference, Reference newReference, String identifier) throws GreenPepperServerException;

    /**
     * Deletes the specified Reference.
     * <p/>
     * @param Reference
     * @throws GreenPepperServerException
     */
    public void removeReference(Reference reference, String identifier) throws GreenPepperServerException;

    /**
     * Executes the Specification over the selected SystemUnderTest.
     * <p/>
     * @param systemUnderTest
     * @param specification
     * @param implementedVersion
     * @param locale
     * @param identifier
     * @return the Execution of the Specification over the selected SystemUnderTest.
     * @throws GreenPepperServerException
     */
    public Execution runSpecification(SystemUnderTest systemUnderTest, Specification specification, boolean implementedVersion, String locale, String identifier) throws GreenPepperServerException;

    /**
     * Executes the Reference.
     * <p/>
     * @param Reference
     * @param locale
     * @param identifier
     * @return the Reference with its last execution.
     * @throws GreenPepperServerException
     */
    public Reference runReference(Reference reference, String locale, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the list of specification
     * <p/>
     * @param repository
     * @param systemUnderTest
     * @param identifier
     * @return
     */
    public DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest, String identifier) throws GreenPepperServerException;

    /**
     * Retrieves the requirement summary.
     * <p/>
     * @param requirement
     * @param identifier
     * @return the requirement summary.
     */
    public RequirementSummary getSummary(Requirement requirement, String identifier) throws GreenPepperServerException;
    
    /**
     * Retrieves the server properties manager.
     * <p/>
     * @return the server properties manager.
     */
    public ServerPropertiesManager getServerPropertiesManager();
}
