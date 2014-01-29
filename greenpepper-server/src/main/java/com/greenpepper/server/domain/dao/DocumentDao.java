package com.greenpepper.server.domain.dao;

import java.util.List;

import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.Requirement;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;

public interface DocumentDao
{
    /**
     * Retrieves the Requirement for the specified repository UID.
     * If none found an GreenPepperServerException is thrown.
     * </p>
     * @param repositoryUid
     * @param requirementName
     * @return the Requirement for the specified repository UID.
     */
    public Requirement getRequirementByName(String repositoryUid, String requirementName);
    
    /**
     * Saves the Requirement for the specified repository UID.
     * </p>
     * @param repositoryUid
     * @param requirementName
     * @return the new Requirement
     * @throws GreenPepperServerException
     */
    public Requirement createRequirement(String repositoryUid, String requirementName) throws GreenPepperServerException;

    /**
     * Retrieves the Requirement for the specified repository UID. 
     * If none found then a new is saved and returned.
     * </p>
     * @param repositoryUid
     * @param requirementName
     * @return the retrieved/created Requirement
     * @throws GreenPepperServerException
     */
    public Requirement getOrCreateRequirement(String repositoryUid, String requirementName) throws GreenPepperServerException;

    /**
     * Removes the Requirement.
     * </p>
     * @param requirement
     * @throws GreenPepperServerException
     */
    public void removeRequirement(Requirement requirement) throws GreenPepperServerException;
    
    /**
     * Retrieves the Specification for the specified repository UID.
     * </p>
     * @param repositoryUid
     * @param specificationName
     * @return the Specification for the specified repository UID.
     */
    public Specification getSpecificationByName(String repositoryUid, String specificationName);

	/**
	 * Retrieves the Specification for the specified id.
	 * </p>
	 * @param id Specification id to retrieve
	 * @return the Specification for the given id
	 */
	public Specification getSpecificationById(Long id);
    
    /**
     * Saves the Specification for the specified repository UID.
     * </p>
     * @param systemUnderTestName
     * @param repositoryUid
     * @param specificationName
     * @return the new Specification
     * @throws GreenPepperServerException
     */
    public Specification createSpecification(String systemUnderTestName, String repositoryUid, String specificationName) throws GreenPepperServerException;

    /**
     * Retrieves the Specification for the specified repository UID.
     * If none found then a new is saved and returned.
     * </p>
     * @param systemUnderTestName
     * @param repositoryUid
     * @param specificationName
     * @return the retrieved/created Specification
     * @throws GreenPepperServerException
     */
    public Specification getOrCreateSpecification(String systemUnderTestName, String repositoryUid, String specificationName) throws GreenPepperServerException;

    /**
     * UPdates the Specification.
     * </p>
     * @param newSpecification
     * @param Specification
     * @throws GreenPepperServerException 
     */
    public void updateSpecification(Specification oldSpecification, Specification newSpecification) throws GreenPepperServerException;

    /**
     * Removes the Specification.
     * </p>
     * @param Specification
     * @throws GreenPepperServerException 
     */
    public void removeSpecification(Specification specification) throws GreenPepperServerException;

    /**
     * Retrieves the Reference from dataBase.
     * </p>
     * @param Reference
     * @return the Reference from dataBase.
     */
    public Reference get(Reference reference);

    /**
     * Retrieves the list of References linked to the Specification
     * </p>
     * @param Specification
     * @return the list of References linked to the Specification
     */
    public List<Reference> getAllReferences(Specification specification);

    /**
     * Retrieves the list of References linked to the Requirement
     * </p>
     * @param Requirement
     * @return the list of References linked to the Requirement
     */
    public List<Reference> getAllReferences(Requirement requirement);

    /**
     * Adds the SystemUnderTest to the SystemUnderTest list of the Specification
     * </p>
     * @param systemUnderTest
     * @param specification
     * @throws GreenPepperServerException
     */
    public void addSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification) throws GreenPepperServerException;
    
    /**
     * Removes the SystemUnderTest to the SystemUnderTest list of the Specification
     * </p>
     * @param systemUnderTest
     * @param specification
     * @throws GreenPepperServerException
     */
    public void removeSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification) throws GreenPepperServerException;
    
    /**
     * Creates the Reference.
     * The Project, the repositories and the System under test have to exist
     * else an exception will be thrown.
     * </p>
     * @param Reference
     * @return the new Created Reference
     * @throws GreenPepperServerException
     */
    public Reference createReference(Reference reference) throws GreenPepperServerException;
    
    /**
     * Deletes the Reference
     * </p>
     * @param Reference
     * @throws GreenPepperServerException
     */
    public void removeReference(Reference reference) throws GreenPepperServerException;
    
    /**
     * Updates the old Reference with the new one.
     * Basically removes the old one and creates a new one.
     * </p>
     * @param oldReference
     * @param newReference
     * @return the updated Reference
     * @throws GreenPepperServerException
     */
    public Reference updateReference(Reference oldReference, Reference newReference) throws GreenPepperServerException;

	/**
	 * Creates the Execution.
	 *
	 * @param execution
	 * @return the new created Execution
	 * @throws GreenPepperServerException
	 */
	public Execution createExecution(Execution execution) throws GreenPepperServerException;
	
    /**
     * Run the Specification on the SystemUnderTest.
     * </p>
     * @param systemUnderTest
     * @param specification
     * @param implemeted
     * @param locale
     * @return the execution of the Specification on the SystemUnderTest.
     * @throws GreenPepperServerException
     */
    public Execution runSpecification(SystemUnderTest systemUnderTest, Specification specification, boolean implemeted, String locale) throws GreenPepperServerException;
    
    /**
     * Run the Specification of the reference.
     * </p>
     * @param reference
     * @param locale
     * @return the executed Reference
     * @throws GreenPepperServerException
     */
    public Reference runReference(Reference reference, String locale) throws GreenPepperServerException;

    /**
     * Retrieves all Specifications for a given SystemUnderTest and Repository
     * <p>
     * @param sut
     * @param repository
     * @return all Specifications for a given SystemUnderTest and Repository
     */
    public List<Specification> getSpecifications(SystemUnderTest sut, Repository repository);

	/**
	 * Retrieve specification Executions for a given Specification where the specification has been
	 * executed before the given start date.
	 *
	 * @param specification
	 * @param sut
	 * @param maxResults
	 * @return Specification executions containing at most the max-result items
	 */
	public List<Execution> getSpecificationExecutions(Specification specification, SystemUnderTest sut, int maxResults);

	/**
	 * Retrieve an Execution for the given id.
	 *
	 * @param id
	 * @return execution for the given id
	 * @throws GreenPepperServerException
	 */
	public Execution getSpecificationExecution(Long id);
}
