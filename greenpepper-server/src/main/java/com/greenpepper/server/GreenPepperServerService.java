/*
 * Copyright (c) 2008 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */
package com.greenpepper.server;

import java.util.List;
import java.util.Vector;

import com.greenpepper.report.XmlReport;
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
import com.greenpepper.server.license.GreenPepperLicenceException;
import com.greenpepper.server.license.LicenseBean;
import com.greenpepper.server.license.Permission;

public interface GreenPepperServerService
{

	/**
	 * Retrieves the GreenpPepper Server license.
	 *
	 * @return the GreenpPepper Server license bean.
	 */
	LicenseBean license()
			throws GreenPepperServerException;

	/**
	 * Uploads the new GreenpPepper Server license.
	 *
	 * @param newLicense New license information to upload
	 */
	void uploadNewLicense(String newLicense)
			throws GreenPepperServerException;

	/**
	 * Indicates if the current license type is 'Commercial'.
	 *
	 * @return true if license type is 'Commercial', false otherwise
	 * @throws GreenPepperServerException
	 */
	boolean isCommercialLicense()
			throws GreenPepperServerException;

	/**
	 * Verifies that the license supports the repository has the rgiht permission.
	 *
	 * @param repository The repository to verify
	 * @param permission Permission access to verify on the given repository
	 * @throws GreenPepperLicenceException
	 */
	void verifyRepositoryPermission(Repository repository, Permission permission)
			throws GreenPepperLicenceException;

	/**
	 * Retrieves the EnvironmentTypes available.
	 *
	 * @return all the EnvironmentTypes available.
	 */
	List<EnvironmentType> getAllEnvironmentTypes()
			throws GreenPepperServerException;

	/**
	 * Retrieves the runner for a given the name.
	 *
	 * @param name The name of the runner to retrieve
	 * @return the runner for a given the name.
	 */
	Runner getRunner(String name)
			throws GreenPepperServerException;

	/**
	 * Retrieves all available Runners.
	 *
	 * @return all available Runners
	 */
	List<Runner> getAllRunners()
			throws GreenPepperServerException;

	/**
	 * Creates a new Runner.
	 *
	 * @param runner The runner to create
	 */
	void createRunner(Runner runner)
			throws GreenPepperServerException;

	/**
	 * Updates the Runner.
	 *
	 * @param oldRunnerName The name of the old runner to be updated
	 * @param runner		The runner to update
	 */
	void updateRunner(String oldRunnerName, Runner runner)
			throws GreenPepperServerException;

	/**
	 * Creates a new Runner.
	 *
	 * @param name The name of the runner to remove
	 */
	void removeRunner(String name)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Repository for the uid.
	 *
	 * @param uid The repository identifier
	 * @param maxUsers The maximum user the repository should allow, null for no check
	 * @return the Repository for the uid.
	 */
	Repository getRepository(String uid, Integer maxUsers)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Repository for the uid.
	 *
	 * @param repository The repository
	 * @return the Repository for the uid.
	 */
	Repository getRegisteredRepository(Repository repository)
			throws GreenPepperServerException;

	/**
	 * Registers the repository in GreenPepper-server. If project not found it will be created.
	 *
	 * @param repository The repository to be registered
	 * @return the registered repository.
	 */
	Repository registerRepository(Repository repository)
			throws GreenPepperServerException;

	/**
	 * Updates the Repository Registration. If project not found it will be created.
	 *
	 * @param repository The repository to update
	 */
	void updateRepositoryRegistration(Repository repository)
			throws GreenPepperServerException;

	/**
	 * Removes the Repository if this one doesnt hold any specifications.
	 *
	 * @param repositoryUid The repository identifier to be removed
	 */
	void removeRepository(String repositoryUid)
			throws GreenPepperServerException;

	/**
	 * Gets all repository associated to the given project.
	 *
	 * @param projectName Name of the project
	 * @return list of repository
	 */
	List<Repository> getRepositoriesOfAssociatedProject(String projectName)
			throws GreenPepperServerException;

	/**
	 * Retrieves the project for a given the name.
	 *
	 * @param name The name of the project to retrieve
	 * @return the project for a given the name.
	 */
	Project getProject(String name)
			throws GreenPepperServerException;

	/**
	 * Creates a new Project.
	 *
	 * @param project The project to create
	 * @return the newly created project instance
	 * @throws GreenPepperServerException Exception
	 */
	Project createProject(Project project)
			throws GreenPepperServerException;

	/**
	 * Updates the Project.
	 *
	 * @param oldProjectName The name of the old project to be updated
	 * @param project		The project to update
	 * @return the newly updated project instance
	 * @throws GreenPepperServerException Exception
	 */
	Project updateProject(String oldProjectName, Project project)
			throws GreenPepperServerException;

	/**
	 * Retrieves the complete project list.
	 *
	 * @return the complete project list.
	 */
	List<Project> getAllProjects()
			throws GreenPepperServerException;

	/**
	 * Retrieves all the Specification repository grouped by project or an error id in a Hastable if an error occured.
	 *
	 * @return the Specification repository list grouped by types for the project or an error id in a Hastable if an error
	 *         occured.
	 */
	List<Repository> getAllSpecificationRepositories()
			throws GreenPepperServerException;

	/**
	 * Retrieves the Specification repository list grouped by types for the project associated with the specified
	 * repository or an error id in a Hastable if an error occured.
	 * <p/>
	 *
	 * @param repositoryUid The repository identifier
	 * @return the Specification repository list grouped by types for the project associated with the specified repository
	 *         or an error id in a Hastable if an error occured.
	 */
	List<Repository> getSpecificationRepositoriesOfAssociatedProject(String repositoryUid)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Repository list for the project associated with the specified system under test or an error id in a
	 * Hastable if an error occured.
	 *
	 * @param sut The system under test to retrieve the list of repository
	 * @return the repository list for the project associated with the specified systemUnderTest
	 */
	List<Repository> getAllRepositoriesForSystemUnderTest(SystemUnderTest sut)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Specification repository list grouped by types for the project associated with the specified
	 * SystemUnderTest or an error id in a Hastable if an error occured.
	 *
	 * @param sut The system under test to retrieve the list of repository
	 * @return the Specification repository list grouped by types for the project associated with the specified
	 *         SystemUnderTest or an error id in a Hastable if an error occured.
	 */
	List<Repository> getSpecificationRepositoriesForSystemUnderTest(SystemUnderTest sut)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Requirement repository list for the project associated with the specified repository or an error id in
	 * a Hastable if an error occured.
	 *
	 * @param repositoryUid The repository identifer to retrieve the list of requirement
	 * @return the Requirement repository list for the project associated with the specified repository or an error id in a
	 *         Hastable if an error occured.
	 */
	List<Repository> getRequirementRepositoriesOfAssociatedProject(String repositoryUid)
			throws GreenPepperServerException;

	/**
	 * Retrieves the SystemUnderTest list for the project associated with the specified repository or an error id in a
	 * Hastable if an error occured.
	 *
	 * @param repositoryUid The repository identifier to retrieve the list of sut
	 * @return the SystemUnderTest list for the project associated with the specified repository or an error id in a
	 *         Hastable if an error occured.
	 */
	List<SystemUnderTest> getSystemUnderTestsOfAssociatedProject(String repositoryUid)
			throws GreenPepperServerException;

	/**
	 * Retrieves the SystemUnderTest list for the project associated or an error id in a Hastable if an error occured.
	 *
	 * @param projectName The name of the project to retrieve the list of sut
	 * @return the SystemUnderTest list for the project associated or an error id in a Hastable if an error occured.
	 */
	List<SystemUnderTest> getSystemUnderTestsOfProject(String projectName)
			throws GreenPepperServerException;

	/**
	 * Adds the SystemUnderTest to the SystemUnderTest list of the Specification.
	 *
	 * @param systemUnderTest
	 * @param specification
	 */
	void addSpecificationSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification)
			throws GreenPepperServerException;

	/**
	 * Removes the SystemUnderTest to the SystemUnderTest list of the Specification.
	 *
	 * @param systemUnderTest
	 * @param specification
	 */
	void removeSpecificationSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification)
			throws GreenPepperServerException;

	/**
	 * Checks if the Specification is in atleast one reference.
	 *
	 * @param specification
	 * @return true if the Specification is in atleast one reference.
	 */
	boolean doesSpecificationHasReferences(Specification specification)
			throws GreenPepperServerException;

	/**
	 * Retrieves the references list of the specified Specification
	 *
	 * @param specification
	 * @return the references list of the specified Specification
	 */
	List<Reference> getSpecificationReferences(Specification specification)
			throws GreenPepperServerException;

	/**
	 * Retrieve executions list of the specified Specification
	 *
	 * @param specification
	 * @param sut
	 * @param maxResults
	 * @return the executions list of the specified Specification containing at most max-results items
	 */
	List<Execution> getSpecificationExecutions(Specification specification, SystemUnderTest sut, int maxResults)
			throws GreenPepperServerException;

	/**
	 * Retrieve execution for the given id.
	 *
	 * @param id
	 * @return execution
	 * @throws GreenPepperServerException
	 */
	Execution getSpecificationExecution(Long id)
			throws GreenPepperServerException;

	/**
	 * Checks if the Requirement is in atleast one Reference.
	 *
	 * @param requirement
	 * @return true if the Requirement is in atleast one Reference.
	 */
	boolean doesRequirementHasReferences(Requirement requirement)
			throws GreenPepperServerException;

	/**
	 * Retrieves the References list of the specified requirement
	 *
	 * @param requirement
	 * @return the References list of the specified requirement
	 */
	List<Reference> getRequirementReferences(Requirement requirement)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Requirement summary.
	 *
	 * @param requirement
	 * @return the Requirement summary.
	 */
	RequirementSummary getRequirementSummary(Requirement requirement)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Reference.
	 *
	 * @param reference
	 * @return the Reference.
	 */
	Reference getReference(Reference reference)
			throws GreenPepperServerException;

	/**
	 * Retrieves the systemUnderTest
	 *
	 * @param systemUnderTest
	 * @param repository
	 * @return the System under test
	 */
	SystemUnderTest getSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository)
			throws GreenPepperServerException;

	/**
	 * Creates the systemUnderTest
	 *
	 * @param systemUnderTest
	 * @param repository
	 */
	void createSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository)
			throws GreenPepperServerException;

	/**
	 * Updates the systemUnderTest
	 *
	 * @param oldSystemUnderTestName
	 * @param systemUnderTest
	 * @param repository
	 */
	void updateSystemUnderTest(String oldSystemUnderTestName, SystemUnderTest systemUnderTest,
									  Repository repository)
			throws GreenPepperServerException;

	/**
	 * Removes the systemUnderTest
	 *
	 * @param systemUnderTest
	 * @param repository
	 */
	void removeSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository)
			throws GreenPepperServerException;

	/**
	 * Sets the systemUnderTest as the project default SystemUnderTest
	 *
	 * @param systemUnderTest
	 * @param repository
	 */
	void setSystemUnderTestAsDefault(SystemUnderTest systemUnderTest, Repository repository)
			throws GreenPepperServerException;

	/**
	 * Removes the Requirement.
	 *
	 * @param requirement
	 */
	void removeRequirement(Requirement requirement)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Specification
	 *
	 * @param specification
	 * @return the Specification
	 */
	Specification getSpecification(Specification specification)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Specification using the given id.
	 *
	 * @param id Specification id to retrieve
	 * @return the specification
	 * @throws GreenPepperServerException
	 */
	Specification getSpecificationById(Long id)
			throws GreenPepperServerException;

	/**
	 * Retrieves all Specifications for a given SystemUnderTest and Repository
	 *
	 * @param systemUnderTest
	 * @param repository
	 * @return all Specifications for a given SystemUnderTest and Repository
	 */
	List<Specification> getSpecifications(SystemUnderTest systemUnderTest, Repository repository)
			throws GreenPepperServerException;

	/**
	 * Retrieves the Specification location list for a given SystemUnderTest and Repository
	 *
	 * @param repositoryUID
	 * @param systemUnderTestName
	 * @return the Specification location list for a given SystemUnderTest and Repository
	 */
	Vector<Object> getListOfSpecificationLocations(String repositoryUID, String systemUnderTestName)
			throws GreenPepperServerException;

	/**
	 * Retrieve the spcifications hierarchy for a Repository.
	 *
	 * @param repository
	 * @param systemUnderTest
	 * @return the TestCase executed
	 */
	DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest)
			throws GreenPepperServerException;

	/**
	 * Creates the Specification
	 *
	 * @param specification
	 * @return the new Specification
	 */
	Specification createSpecification(Specification specification)
			throws GreenPepperServerException;

	/**
	 * Updates the Specification.
	 *
	 * @param oldSpecification
	 * @param newSpecification
	 */
	void updateSpecification(Specification oldSpecification, Specification newSpecification)
			throws GreenPepperServerException;

	/**
	 * Removes the Specification.
	 *
	 * @param specification
	 */
	void removeSpecification(Specification specification)
			throws GreenPepperServerException;

	/**
	 * Creates a Reference
	 *
	 * @param reference
	 */
	void createReference(Reference reference)
			throws GreenPepperServerException;

	/**
	 * Update the Reference. The Old one will be deleted based on the oldReferenceParams and a new One will be created
	 * based on the newReferenceParams.
	 *
	 * @param oldReference
	 * @param newReference
	 * @return the updated Reference
	 */
	Reference updateReference(Reference oldReference, Reference newReference)
			throws GreenPepperServerException;

	/**
	 * Deletes the specified Reference.
	 *
	 * @param reference
	 */
	void removeReference(Reference reference)
			throws GreenPepperServerException;

	/**
	 * Creates an Execution.
	 *
	 * @param systemUnderTest
	 * @param specification
	 * @param xmlReport
	 * @return the new created Execution
	 * @throws GreenPepperServerException
	 */
	Execution createExecution(SystemUnderTest systemUnderTest, Specification specification, XmlReport xmlReport)
			throws GreenPepperServerException;

	/**
	 * Executes the Specification over the selected SystemUnderTest.
	 *
	 * @param systemUnderTest
	 * @param specification
	 * @param implementedVersion
	 * @param locale
	 * @return the Execution of the Specification over the selected SystemUnderTest.
	 */
	Execution runSpecification(SystemUnderTest systemUnderTest, Specification specification,
									  boolean implementedVersion, String locale)
			throws GreenPepperServerException;

	/**
	 * Executes the Reference.
	 *
	 * @param reference
	 * @param locale
	 * @return the Reference executed
	 */
	Reference runReference(Reference reference, String locale)
			throws GreenPepperServerException;

	/**
	 * Removes an existing Project.
	 *
	 * @param project
	 * @param cascade Indicates to remove the project in cascading mode (remove any associations)
	 * @throws GreenPepperServerException
	 */
	void removeProject(Project project, boolean cascade)
			throws GreenPepperServerException;
}