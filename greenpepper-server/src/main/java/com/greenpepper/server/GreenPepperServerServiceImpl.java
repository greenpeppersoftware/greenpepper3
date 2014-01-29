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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greenpepper.report.XmlReport;
import com.greenpepper.repository.DocumentRepository;
import static com.greenpepper.server.GreenPepperServerErrorKey.ERROR;
import static com.greenpepper.server.GreenPepperServerErrorKey.EXECUTION_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.PROJECT_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.PROJECT_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.PROJECT_UPDATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REFERENCE_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REFERENCE_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REFERENCE_UPDATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_NOT_FOUND;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_REGISTRATION_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REPOSITORY_UPDATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.REQUIREMENT_REMOVE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_EXECUTIONS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_REFERENCE;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_REFERENCES;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_REQUIREMENT_REPOS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_SPECIFICATION_REPOS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RETRIEVE_SUTS;
import static com.greenpepper.server.GreenPepperServerErrorKey.RUNNER_CREATE_FAILED;
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
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_CREATE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_DELETE_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_SET_DEFAULT_FAILED;
import static com.greenpepper.server.GreenPepperServerErrorKey.SUT_UPDATE_FAILED;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.DocumentNode;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.ReferenceNode;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.Requirement;
import com.greenpepper.server.domain.RequirementSummary;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;
import com.greenpepper.server.domain.component.ContentType;
import com.greenpepper.server.domain.dao.DocumentDao;
import com.greenpepper.server.domain.dao.ProjectDao;
import com.greenpepper.server.domain.dao.RepositoryDao;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;
import com.greenpepper.server.license.Authorizer;
import com.greenpepper.server.license.GreenPepperLicenceException;
import com.greenpepper.server.license.LicenseBean;
import com.greenpepper.server.license.LicenseErrorKey;
import com.greenpepper.server.license.Permission;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import com.greenpepper.util.StringUtil;

public class GreenPepperServerServiceImpl
		implements GreenPepperServerService
{

	private static Logger log = LoggerFactory.getLogger(GreenPepperServerServiceImpl.class);

	private Authorizer authorizer;
	private SessionService sessionService;
	private ProjectDao projectDao;
	private RepositoryDao repositoryDao;
	private SystemUnderTestDao sutDao;
	private DocumentDao documentDao;

	public GreenPepperServerServiceImpl(Authorizer authorizer, SessionService sessionService,
										ProjectDao projectDao, RepositoryDao repositoryDao,
										SystemUnderTestDao sutDao, DocumentDao documentDao)
	{
		this.authorizer = authorizer;
		this.sessionService = sessionService;
		this.projectDao = projectDao;
		this.repositoryDao = repositoryDao;
		this.sutDao = sutDao;
		this.documentDao = documentDao;
	}

	public GreenPepperServerServiceImpl()
	{

	}

	public void setAuthorizer(Authorizer authorizer)
	{
		this.authorizer = authorizer;
	}

	public void setSessionService(SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public void setProjectDao(ProjectDao projectDao)
	{
		this.projectDao = projectDao;
	}

	public void setRepositoryDao(RepositoryDao repositoryDao)
	{
		this.repositoryDao = repositoryDao;
	}

	public void setSutDao(SystemUnderTestDao sutDao)
	{
		this.sutDao = sutDao;
	}

	public void setDocumentDao(DocumentDao documentDao)
	{
		this.documentDao = documentDao;
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public LicenseBean license()
	{
		return authorizer.getLicenseBean();
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public void uploadNewLicense(String newLicense)
			throws GreenPepperServerException
	{
		try
		{
			authorizer.reInitialize(newLicense);
		}
		catch (Exception ex)
		{
			throw handleException(LicenseErrorKey.FAILED_TO_UPDATE_LIC, ex);
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public boolean isCommercialLicense()
			throws GreenPepperServerException
	{
		return authorizer.isCommercialLicense();
	}

	/**
	 * @inheritDoc
	 */
	public void verifyRepositoryPermission(Repository repository, Permission permission)
			throws GreenPepperLicenceException
	{
		authorizer.verify(repository, permission);
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public List<EnvironmentType> getAllEnvironmentTypes()
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			List<EnvironmentType> envTypes = sutDao.getAllEnvironmentTypes();

			log.debug("Retrieved All Environment Types number: " + envTypes.size());

			return envTypes;
		}
		catch (Exception ex)
		{
			throw handleException(ERROR, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public Runner getRunner(String name)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Runner runner = sutDao.getRunnerByName(name);

			log.debug("Retrieved Runner name: " + name);

			return runner;
		}
		catch (Exception ex)
		{
			throw handleException(ERROR, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public List<Runner> getAllRunners()
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			List<Runner> runners = sutDao.getAllRunners();

			log.debug("Retrieved All Runner number: " + runners.size());

			return runners;
		}
		catch (Exception ex)
		{
			throw handleException(ERROR, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public void createRunner(Runner runner)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			sutDao.create(runner);

			sessionService.commitTransaction();
			log.debug("Created Runner: " + runner.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(RUNNER_CREATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public void updateRunner(String oldRunnerName, Runner runner)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			sutDao.update(oldRunnerName, runner);

			sessionService.commitTransaction();
			log.debug("Updated Runner: " + oldRunnerName);
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(RUNNER_UPDATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public void removeRunner(String name)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			sutDao.removeRunner(name);

			sessionService.commitTransaction();
			log.debug("Removed Runner: " + name);
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(RUNNER_REMOVE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Repository getRepository(String uid, Integer maxUsers)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(uid);

			if (maxUsers != null)
			{
				repository.setMaxUsers(maxUsers);
			}

			verifyRepositoryPermission(repository, Permission.READ);

			return repository;
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Repository getRegisteredRepository(Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository registeredRepository = loadRepository(repository.getUid());
			
			registeredRepository.setMaxUsers(repository.getMaxUsers());

			verifyRepositoryPermission(registeredRepository, Permission.READ);

			return registeredRepository;
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Repository registerRepository(Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			verifyRepositoryPermission(repository, Permission.WRITE);

			Project project = projectDao.getByName(repository.getProject().getName());

			if (project == null)
			{
				projectDao.create(repository.getProject().getName());
			}

			repository = repositoryDao.create(repository);

			sessionService.commitTransaction();

			log.debug("Registered Repository: " + repository.getUid());

			return repository;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(REPOSITORY_REGISTRATION_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void updateRepositoryRegistration(Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			verifyRepositoryPermission(repository, Permission.WRITE);

			Project project = projectDao.getByName(repository.getProject().getName());

			if (project == null)
			{
				projectDao.create(repository.getProject().getName());
			}

			repositoryDao.update(repository);

			sessionService.commitTransaction();
			log.debug("Updated Repository: " + repository.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(REPOSITORY_UPDATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public void removeRepository(String repositoryUid)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			repositoryDao.remove(repositoryUid);

			sessionService.commitTransaction();
			log.debug("Removed Repository: " + repositoryUid);
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(REPOSITORY_REMOVE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public List<Repository> getRepositoriesOfAssociatedProject(String projectName)
			throws GreenPepperServerException {

		try
		{
			sessionService.startSession();

			return repositoryDao.getAll(projectName);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public Project getProject(String name)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Project project = projectDao.getByName(name);

			log.debug("Retrieved Project name: " + name);
			return project;
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public List<Project> getAllProjects()
	{
		try
		{
			sessionService.startSession();

			List<Project> projects = projectDao.getAll();

			log.debug("Retrieved All Projects number: " + projects.size());
			return projects;
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public Project createProject(Project project)
			throws GreenPepperServerException {

		Project newProject;

		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			newProject = projectDao.create(project.getName());

			sessionService.commitTransaction();
			log.debug("Created Project: " + project.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(PROJECT_CREATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}

		return newProject;
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public Project updateProject(String oldProjectName, Project project)
			throws GreenPepperServerException {

		Project projectUpdated;
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			projectUpdated = projectDao.update(oldProjectName, project);

			sessionService.commitTransaction();
			log.debug("Updated Project: " + project.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(PROJECT_UPDATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}

		return projectUpdated;
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public List<Repository> getAllSpecificationRepositories()
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			List<Repository> repositories = repositoryDao.getAllRepositories(ContentType.TEST);

			log.debug("Retrieved All Specification Repositories number: " + repositories.size());
			return repositories;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_SUTS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public List<Repository> getSpecificationRepositoriesOfAssociatedProject(String repositoryUid)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(repositoryUid);

			verifyRepositoryPermission(repository, Permission.READ);

			List<Repository> repositories = repositoryDao.getAllTestRepositories(repository.getProject().getName());

			log.debug("Retrieved Test Repositories Of Associated Project of " + repository.getUid() + " number: " +
					  repositories.size());
			return repositories;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_SPECIFICATION_REPOS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc CANT SECURE
	 */
	public List<Repository> getAllRepositoriesForSystemUnderTest(SystemUnderTest sut)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			List<Repository> repositories = repositoryDao.getAllTestRepositories(sut.getProject().getName());

			log.debug("Retrieved Test Repositories Of Associated Project of " + sut.getName() + " number: " +
					  repositories.size());
			return repositories;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_SPECIFICATION_REPOS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc CANT SECURE
	 */
	public List<Repository> getSpecificationRepositoriesForSystemUnderTest(SystemUnderTest sut)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			List<Repository> repositories = repositoryDao.getAllTestRepositories(sut.getProject().getName());

			log.debug("Retrieved Test Repositories Of Associated Project of " + sut.getName() + " number: " +
					  repositories.size());
			return repositories;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_SPECIFICATION_REPOS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public List<Repository> getRequirementRepositoriesOfAssociatedProject(String repositoryUid)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(repositoryUid);

			verifyRepositoryPermission(repository, Permission.READ);

			List<Repository> repositories =
					repositoryDao.getAllRequirementRepositories(repository.getProject().getName());

			log.debug("Retrieved Requirement Repositories Of Associated Project of " + repository.getUid() +
					  " number: " + repositories.size());
			return repositories;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_REQUIREMENT_REPOS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public List<SystemUnderTest> getSystemUnderTestsOfAssociatedProject(String repositoryUid)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(repositoryUid);
			verifyRepositoryPermission(repository, Permission.READ);

			List<SystemUnderTest> suts = sutDao.getAllForProject(repository.getProject().getName());

			log.debug("Retrieved SUTs Of Associated Project of " + repository.getUid() + " number: " + suts.size());
			return suts;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_SUTS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public List<SystemUnderTest> getSystemUnderTestsOfProject(String projectName)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			List<SystemUnderTest> suts = sutDao.getAllForProject(projectName);

			log.debug("Retrieved SUTs of Project: " + projectName + " number: " + suts.size());
			return suts;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_SUTS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void addSpecificationSystemUnderTest(SystemUnderTest sut, Specification specification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(specification.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.WRITE);

			documentDao.addSystemUnderTest(sut, specification);

			sessionService.commitTransaction();
			log.debug("Added SUT " + sut.getName() + " to SUT list of specification: " + specification.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_ADD_SUT_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void removeSpecificationSystemUnderTest(SystemUnderTest sut, Specification specification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(specification.getRepository().getUid());
			authorizer.verify(repository, Permission.WRITE);

			documentDao.removeSystemUnderTest(sut, specification);

			sessionService.commitTransaction();
			log.debug("Removed SUT " + sut.getName() + " to SUT list of specification: " + specification.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_REMOVE_SUT_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public boolean doesSpecificationHasReferences(Specification specification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			boolean hasReferences = !documentDao.getAllReferences(specification).isEmpty();

			log.debug("Does Specification " + specification.getName() + "  Has References: " + hasReferences);
			return hasReferences;
		}
		catch (Exception ex)
		{
			throw handleException(ERROR, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public List<Reference> getSpecificationReferences(Specification specification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(specification.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.READ);

			List<Reference> references = documentDao.getAllReferences(specification);

			log.debug(
					"Retrieved Specification " + specification.getName() + " Test Cases number: " + references.size());
			return references;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_REFERENCES, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public List<Execution> getSpecificationExecutions(Specification specification, SystemUnderTest sut, int maxResults)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			/*
			Repository repository = loadRepository(specification.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.READ);
			*/

			return documentDao.getSpecificationExecutions(specification, sut, maxResults);
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_EXECUTIONS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public Execution getSpecificationExecution(Long id)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			return documentDao.getSpecificationExecution(id);
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_EXECUTIONS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public boolean doesRequirementHasReferences(Requirement requirement)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			boolean hasReferences = !documentDao.getAllReferences(requirement).isEmpty();

			log.debug("Does Requirement " + requirement.getName() + " Document Has References: " + hasReferences);
			return hasReferences;
		}
		catch (Exception ex)
		{
			throw handleException(ERROR, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public List<Reference> getRequirementReferences(Requirement requirement)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(requirement.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.READ);

			List<Reference> references = documentDao.getAllReferences(requirement);

			log.debug("Retrieved Requirement " + requirement.getName() + " Document References number: " +
					  references.size());
			return references;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_REFERENCES, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public RequirementSummary getRequirementSummary(Requirement requirement)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(requirement.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.READ);

			requirement = documentDao.getRequirementByName(repository.getUid(), requirement.getName());

			log.debug("Retrieved Requirement " + requirement.getName() + " Summary");
			return requirement.getSummary();
		}
		catch (Exception ex)
		{
			throw handleException(ERROR, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Reference getReference(Reference reference)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(reference.getSpecification().getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.READ);

			reference = documentDao.get(reference);
			if (reference == null)
			{
				return null;
			}

			log.debug("Retrieved Reference: " + reference.getRequirement().getName() + "," +
					  reference.getSpecification().getName());
			return reference;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_REFERENCE, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public SystemUnderTest getSystemUnderTest(SystemUnderTest sut, Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			verifyRepositoryPermission(repository, Permission.READ);

			SystemUnderTest sutDb = sutDao.getByName(sut.getProject().getName(), sut.getName());

			sessionService.commitTransaction();
			log.debug("Retrieved SystemUnderTest: " + sut.getName());
			return sutDb;
		}
		catch (Exception ex)
		{
			throw handleException(RETRIEVE_SUTS, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void createSystemUnderTest(SystemUnderTest sut, Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			verifyRepositoryPermission(repository, Permission.WRITE);

			SystemUnderTest newSut = sutDao.create(sut);

			sessionService.commitTransaction();
			log.debug("Updated SystemUnderTest: " + newSut.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SUT_CREATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void updateSystemUnderTest(String oldSystemUnderTestName, SystemUnderTest sut, Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			verifyRepositoryPermission(repository, Permission.WRITE);

			sutDao.update(oldSystemUnderTestName, sut);

			sessionService.commitTransaction();
			log.debug("Updated SystemUnderTest: " + oldSystemUnderTestName);
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SUT_UPDATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void removeSystemUnderTest(SystemUnderTest sut, Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			verifyRepositoryPermission(repository, Permission.WRITE);

			sutDao.remove(sut.getProject().getName(), sut.getName());

			sessionService.commitTransaction();
			log.debug("Removed SystemUnderTest: " + sut.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SUT_DELETE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void setSystemUnderTestAsDefault(SystemUnderTest sut, Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			verifyRepositoryPermission(repository, Permission.WRITE);

			sutDao.setAsDefault(sut);

			sessionService.commitTransaction();
			log.debug("Setted as default SystemUnderTest: " + sut.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SUT_SET_DEFAULT_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NOT SECURED FOR SYNCHRONIZATION PURPOSES
	 */
	public void removeRequirement(Requirement requirement)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			documentDao.removeRequirement(requirement);

			sessionService.commitTransaction();
			log.debug("Removed Requirement: " + requirement.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(REQUIREMENT_REMOVE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public Specification getSpecification(Specification specification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Specification specificationFound = documentDao.getSpecificationByName(
					specification.getRepository().getUid(), specification.getName());

			if (specificationFound != null)
			{
				log.debug("Specification found : " + specificationFound.getName());
			}
			
			return specificationFound;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_NOT_FOUND, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public Specification getSpecificationById(Long id)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Specification specificationFound = documentDao.getSpecificationById(id);

			if (specificationFound != null)
			{
				log.debug("Specification found : " + specificationFound.getName());
			}

			return specificationFound;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_NOT_FOUND, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public List<Specification> getSpecifications(SystemUnderTest systemUnderTest, Repository repository)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			verifyRepositoryPermission(repository, Permission.READ);

			List<Specification> specifications = documentDao.getSpecifications(systemUnderTest, repository);

			log.debug("Retrieved specifications for sut: " + systemUnderTest.getName() + " and repoUID:" +
					  repository.getUid());
			return specifications;
		}
		catch (Exception ex)
		{
			throw handleException(SPECIFICATIONS_NOT_FOUND, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Vector<Object> getListOfSpecificationLocations(String repositoryUID, String systemUnderTestName)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			Repository repository = loadRepository(repositoryUID);
			verifyRepositoryPermission(repository, Permission.READ);

			Vector<Object> locations = new Vector<Object>();
			SystemUnderTest systemUnderTest = sutDao.getByName(repository.getProject().getName(), systemUnderTestName);
			List<Specification> specifications = documentDao.getSpecifications(systemUnderTest, repository);

			for (Specification specification : specifications)
			{
				Vector<String> specsDefinition = new Vector<String>();
				EnvironmentType env = systemUnderTest.getRunner().getEnvironmentType();
				specsDefinition.add(specification.getRepository().getType().getRepositoryTypeClass(env));
				specsDefinition.add(specification.getRepository().getBaseTestUrl());
				specsDefinition.add(StringUtil.toEmptyIfNull(specification.getRepository().getUsername()));
				specsDefinition.add(StringUtil.toEmptyIfNull(specification.getRepository().getPassword()));
				specsDefinition.add(specification.getName());
				locations.add(specsDefinition);
			}

			log.debug("Retrieved specification list: " + repository.getName());
			return locations;
		}
		catch (Exception ex)
		{
			throw handleException(SPECIFICATIONS_NOT_FOUND, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();

			String user = repository.getUsername();
			String pwd = repository.getPassword();

			repository = loadRepository(repository.getUid());
			verifyRepositoryPermission(repository, Permission.READ);

			SystemUnderTest systemUnderTestDb =
					sutDao.getByName(repository.getProject().getName(), systemUnderTest.getName());
			DocumentRepository docRepo =
					repository.asDocumentRepository(EnvironmentType.newInstance("JAVA"), user, pwd);

			log.debug("Retrieved specification Hierarchy: " + repository.getName());
			DocumentNode hierarchy =
					XmlRpcDataMarshaller.toDocumentNode(new Vector<Object>(docRepo.listDocumentsInHierarchy()));
			setExecutionEnable(hierarchy, repository.getUid(), systemUnderTestDb);
			return hierarchy;
		}
		catch (Exception ex)
		{
			throw handleException(SPECIFICATIONS_NOT_FOUND, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Specification createSpecification(Specification specification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(specification.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.WRITE);

			Specification specificationDb = documentDao
					.createSpecification(null, specification.getRepository().getUid(), specification.getName());

			sessionService.commitTransaction();
			log.debug("Created Specification: " + specification.getName());
			return specificationDb;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_CREATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NOT SECURED FOR SYNCHRONIZATION PURPOSES
	 */
	public void updateSpecification(Specification oldSpecification, Specification newSpecification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			documentDao.updateSpecification(oldSpecification, newSpecification);

			sessionService.commitTransaction();
			log.debug("Updated Specification: " + oldSpecification.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_UPDATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NOT SECURED FOR SYNCHRONIZATION PURPOSES
	 */
	public void removeSpecification(Specification specification)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			documentDao.removeSpecification(specification);

			sessionService.commitTransaction();
			log.debug("Removed specification: " + specification.getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_REMOVE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void createReference(Reference reference)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(reference.getSpecification().getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.WRITE);

			documentDao.createReference(reference);

			sessionService.commitTransaction();
			log.debug("Created Reference: " + reference.getRequirement().getName() + "," +
					  reference.getSpecification().getName());
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(REFERENCE_CREATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Reference updateReference(Reference oldReference, Reference newReference)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(oldReference.getSpecification().getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.WRITE);

			newReference = documentDao.updateReference(oldReference, newReference);

			log.debug("Updated Reference: " + newReference.getRequirement().getName() + "," +
					  newReference.getSpecification().getName());
			sessionService.commitTransaction();

			return newReference;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(REFERENCE_UPDATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public void removeReference(Reference reference)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(reference.getSpecification().getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.WRITE);

			documentDao.removeReference(reference);

			log.debug("Removed Reference: " + reference.getRequirement().getName() + "," +
					  reference.getSpecification().getName());
			sessionService.commitTransaction();
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(REFERENCE_REMOVE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Execution createExecution(SystemUnderTest systemUnderTest, Specification specification, XmlReport xmlReport)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(specification.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.WRITE);

			Execution execution = Execution.newInstance(specification, systemUnderTest, xmlReport);

			documentDao.createExecution(execution);

			sessionService.commitTransaction();

			return execution;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(EXECUTION_CREATE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Execution runSpecification(SystemUnderTest systemUnderTest, Specification specification,
									  boolean implementedVersion, String locale)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(specification.getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.EXECUTE);

			Execution exe = documentDao.runSpecification(systemUnderTest, specification, implementedVersion, locale);
			log.debug("Runned Specification: " + specification.getName() + " ON System: " + systemUnderTest.getName());
			sessionService.commitTransaction();

			return exe;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(SPECIFICATION_RUN_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc SECURED
	 */
	public Reference runReference(Reference reference, String locale)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			Repository repository = loadRepository(reference.getSpecification().getRepository().getUid());
			verifyRepositoryPermission(repository, Permission.EXECUTE);

			reference = documentDao.runReference(reference, locale);

			log.debug("Runned Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName() +
					  " ON System: " + reference.getSystemUnderTest().getName());
			sessionService.commitTransaction();

			return reference;
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(RUN_REFERENCE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	/**
	 * @inheritDoc NO NEEDS TO SECURE THIS
	 */
	public void removeProject(Project project, boolean cascade)
			throws GreenPepperServerException
	{
		try
		{
			sessionService.startSession();
			sessionService.beginTransaction();

			if (cascade)
			{
				List<Repository> repositories = repositoryDao.getAll(project.getName());

				for (Repository repo : repositories)
				{
					repositoryDao.remove(repo.getUid());
				}

				List<SystemUnderTest> systemUnderTests = sutDao.getAllForProject(project.getName());

				for (SystemUnderTest sut : systemUnderTests)
				{
					sutDao.remove(sut.getProject().getName(), sut.getName());
				}
			}

			projectDao.remove(project.getName());

			log.debug("Removed Project: " + project.getName());
			
			sessionService.commitTransaction();
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			throw handleException(PROJECT_REMOVE_FAILED, ex);
		}
		finally
		{
			sessionService.closeSession();
		}
	}

	private GreenPepperServerException handleException(String id, Exception ex)
	{
		log.error(id, ex);

		if (ex instanceof GreenPepperServerException)
		{
			return (GreenPepperServerException)ex;
		}
		else
		{
			return new GreenPepperServerException(id, ex);
		}
	}

	private Repository loadRepository(String uid)
			throws GreenPepperServerException
	{
		Repository repoDb = repositoryDao.getByUID(uid);

		if (repoDb == null)
		{
			throw new GreenPepperServerException(REPOSITORY_NOT_FOUND, "Repository not registered");
		}

		return repoDb;
	}

	private void setExecutionEnable(DocumentNode node, String repoUID, SystemUnderTest systemUnderTest)
	{
		if (node.isExecutable())
		{
			return;
		}

		if (node instanceof ReferenceNode)
		{
			ReferenceNode refNode = (ReferenceNode)node;
			Specification spec = documentDao.getSpecificationByName(refNode.getRepositoryUID(), refNode.getTitle());
			node.setIsExecutable(spec != null && refNode.getSutName().equals(systemUnderTest.getName()));
		}
		else
		{
			Specification spec = documentDao.getSpecificationByName(repoUID, node.getTitle());
			node.setIsExecutable(spec != null && spec.getTargetedSystemUnderTests().contains(systemUnderTest));
			for (DocumentNode children : node.getChildren())
			{
				setExecutionEnable(children, repoUID, systemUnderTest);
			}
		}
	}
}