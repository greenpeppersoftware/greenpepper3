package com.greenpepper.server.domain.dao.hibernate;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.Requirement;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;
import com.greenpepper.server.domain.component.ContentType;
import com.greenpepper.server.domain.dao.DocumentDao;
import com.greenpepper.server.domain.dao.RepositoryDao;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;
import com.greenpepper.util.StringUtil;

public class HibernateDocumentDao implements DocumentDao
{
    private SessionService sessionService;
    private RepositoryDao repositoryDao;
    private SystemUnderTestDao systemUnderTestDao;

	public HibernateDocumentDao(SessionService sessionService, RepositoryDao repositoryDao,
								SystemUnderTestDao systemUnderTestDao)
	{
		this.sessionService = sessionService;
		this.repositoryDao = repositoryDao;
		this.systemUnderTestDao = systemUnderTestDao;
	}

    public HibernateDocumentDao(SessionService sessionService)
    {
		this(sessionService, new HibernateRepositoryDao(sessionService),
			 new HibernateSystemUnderTestDao(sessionService));
    }

    /**
     * @inheritDoc
     */
    public Requirement getRequirementByName(String repositoryUid, String requirementName)
    {
        Criteria crit = sessionService.getSession().createCriteria(Requirement.class);
        crit.add(Restrictions.eq("name", requirementName));
        crit.createAlias("repository", "r");
        crit.add(Restrictions.eq("r.uid", repositoryUid));

        Requirement requirement = (Requirement) crit.uniqueResult();
		HibernateLazyInitializer.init(requirement);
		return requirement;
    }

    /**
     * @inheritDoc
     */
    public Requirement createRequirement(String repositoryUid, String requirementName) throws GreenPepperServerException
    {
        Repository repository = repositoryDao.getByUID(repositoryUid);        
        if(repository == null) 
            throw new GreenPepperServerException(GreenPepperServerErrorKey.REPOSITORY_NOT_FOUND, "Repo not found");
        
        Requirement requirement = Requirement.newInstance(requirementName);
        repository.addRequirement(requirement);
        sessionService.getSession().save(repository);
        return requirement;
    }

    /**
     * @inheritDoc
     */
    public Requirement getOrCreateRequirement(String repositoryUid, String requirementName) throws GreenPepperServerException
    {
        Requirement requirement = getRequirementByName(repositoryUid, requirementName);
        if(requirement == null)
        {
            return createRequirement(repositoryUid, requirementName);
        }

		HibernateLazyInitializer.init(requirement);
		return requirement;
    }

    /**
     * @throws GreenPepperServerException 
     * @inheritDoc
     */
    public void removeRequirement(Requirement requirement) throws GreenPepperServerException
    {
        requirement = getRequirementByName(requirement.getRepository().getUid(), requirement.getName());
        if(requirement != null) 
        {
            requirement.getRepository().removeRequirement(requirement);
            sessionService.getSession().delete(requirement);        
        }
    }

    /**
     * @inheritDoc
     */
    public Specification getSpecificationByName(String repositoryUid, String specificationName)
    {
        Criteria crit = sessionService.getSession().createCriteria(Specification.class);
        crit.add(Restrictions.ilike("name", specificationName, MatchMode.EXACT));
        crit.createAlias("repository", "r");
        crit.add(Restrictions.eq("r.uid", repositoryUid));

        Specification specification = (Specification) crit.uniqueResult();
		HibernateLazyInitializer.init(specification);
		return specification;
    }

	/**
	 * @inheritDoc
	 */
	public Specification getSpecificationById(Long id)
	{
		Criteria crit = sessionService.getSession().createCriteria(Specification.class);
		crit.add(Restrictions.eq("id", id));

		Specification specification = (Specification) crit.uniqueResult();
		HibernateLazyInitializer.init(specification);
		return specification;
	}

	/**
     * @inheritDoc
     */
    public Specification createSpecification(String systemUnderTestName, String repositoryUid, String specificationName) throws GreenPepperServerException
    {
        Specification specification = Specification.newInstance(specificationName);
        Repository repository = repositoryDao.getByUID(repositoryUid);
        if(repository == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.REPOSITORY_NOT_FOUND, "Repo not found");
        
        SystemUnderTest sut;
        if(systemUnderTestName != null)
        {
            sut = systemUnderTestDao.getByName(repository.getProject().getName(), systemUnderTestName);
            if(sut == null)
                throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_NOT_FOUND, "SystemUnderTest not found");
        }
        else
        {
            sut = repository.getProject().getDefaultSystemUnderTest();
            if(sut == null)
                throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_DEFAULT_SUT_NOT_FOUND, "Default sut not found");
         }
        
        repository.addSpecification(specification);
        specification.addSystemUnderTest(sut);
        
        if(repository.getContentType().equals(ContentType.REQUIREMENT))
        	repository.setContentType(ContentType.BOTH);
        	
        sessionService.getSession().save(repository);
        
        return specification;
    }

    /**
     * @inheritDoc
     */
    public Specification getOrCreateSpecification(String systemUnderTestName, String repositoryUid, String specificationName) throws GreenPepperServerException
    {
        Specification specification = getSpecificationByName(repositoryUid, specificationName);

		if (specification == null)
		{
			specification = createSpecification(systemUnderTestName, repositoryUid, specificationName);
		}

		return specification;
    }

    /**
     * @inheritDoc
     */
    public void updateSpecification(Specification oldSpecification, Specification newSpecification)throws GreenPepperServerException 
    {
        String oldUid = oldSpecification.getRepository().getUid();        
        Specification specificationToUpdate = getSpecificationByName(oldUid, oldSpecification.getName());
        
        if(specificationToUpdate == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SPECIFICATION_NOT_FOUND, "Specification not found");
        
        specificationToUpdate.setName(newSpecification.getName());
        sessionService.getSession().update(specificationToUpdate);
    }

    /**
     * @inheritDoc
     */
    public void removeSpecification(Specification specification) throws GreenPepperServerException
    {
        specification = getSpecificationByName(specification.getRepository().getUid(), specification.getName());
        if(specification != null) 
        {
            specification.getRepository().removeSpecification(specification);
            sessionService.getSession().delete(specification);        
        }
    }

    /** 
     * @inheritDoc
     */
    public void addSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification) throws GreenPepperServerException
    {
        SystemUnderTest sut = systemUnderTestDao.getByName(systemUnderTest.getProject().getName(), systemUnderTest.getName());
        if(sut == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_NOT_FOUND, "SystemUnderTest not found");
    
        specification = getSpecificationByName(specification.getRepository().getUid(), specification.getName());
        if(specification == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SPECIFICATION_NOT_FOUND, "Specification not found");

        specification.addSystemUnderTest(sut);
        sessionService.getSession().save(specification);
    }

    /**
     * @inheritDoc
     */
    public void removeSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification) throws GreenPepperServerException
    {
        if(!getAllReferences(systemUnderTest, specification).isEmpty())
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_REFERENCE_ASSOCIATED, "SystemUnderTest is in a reference");
            
        SystemUnderTest sut = systemUnderTestDao.getByName(systemUnderTest.getProject().getName(), systemUnderTest.getName());
        if(sut == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_NOT_FOUND, "SystemUnderTest not found");
            
        specification = getSpecificationByName(specification.getRepository().getUid(), specification.getName());
        if(specification == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SPECIFICATION_NOT_FOUND, "Specification not found");
        
        specification.removeSystemUnderTest(sut);
        sessionService.getSession().save(specification);
    }

    /**
     * @inheritDoc
     */
    public Reference get(Reference reference)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Reference.class);

		if (StringUtil.isEmpty(reference.getSections()))
		{
			crit.add(Restrictions.isNull("sections"));
		}
		else
		{
			crit.add(Restrictions.eq("sections", reference.getSections()));			
		}
		
		crit.createAlias("requirement", "req");
        crit.add(Restrictions.eq("req.name", reference.getRequirement().getName()));
        crit.createAlias("req.repository", "reqRepo");
        crit.add(Restrictions.eq("reqRepo.uid", reference.getRequirement().getRepository().getUid()));
        crit.createAlias("specification", "spec");
        crit.add(Restrictions.eq("spec.name", reference.getSpecification().getName()));
        crit.createAlias("spec.repository", "specRepo");
        crit.add(Restrictions.eq("specRepo.uid", reference.getSpecification().getRepository().getUid()));
        crit.createAlias("systemUnderTest", "sut");
        crit.add(Restrictions.eq("sut.name", reference.getSystemUnderTest().getName()));
        crit.createAlias("sut.project", "sp");
        crit.add(Restrictions.eq("sp.name", reference.getSystemUnderTest().getProject().getName()));

        Reference result = (Reference)crit.uniqueResult();
		HibernateLazyInitializer.init(result);
		return result;
	}

    /**
     * @inheritDoc
     */
    public List<Reference> getAllReferences(SystemUnderTest systemUnderTest, Specification specification)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Reference.class);
        crit.createAlias("specification", "spec");
        crit.add(Restrictions.eq("spec.name", specification.getName()));
        crit.createAlias("spec.repository", "repo");
        crit.add(Restrictions.eq("repo.uid", specification.getRepository().getUid()));
        crit.createAlias("systemUnderTest", "sut");
        crit.add(Restrictions.eq("sut.name", systemUnderTest.getName()));
        crit.createAlias("sut.project", "sp");
        crit.add(Restrictions.eq("sp.name", systemUnderTest.getProject().getName()));

        @SuppressWarnings("unchecked")
        List<Reference> references = crit.list();
		HibernateLazyInitializer.initCollection(references);
        return references;
    }

    /**
     * @inheritDoc
     */
    public List<Reference> getAllReferences(Specification specification)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Reference.class);
        crit.createAlias("specification", "spec");
        crit.add(Restrictions.eq("spec.name", specification.getName()));
        crit.createAlias("spec.repository", "repo");
        crit.add(Restrictions.eq("repo.uid", specification.getRepository().getUid()));
		crit.createAlias("requirement", "req");
		crit.addOrder(Order.asc("req.name"));
		crit.createAlias("systemUnderTest", "sut");
		crit.addOrder(Order.asc("sut.name"));

		@SuppressWarnings("unchecked")
        List<Reference> references = crit.list();
		HibernateLazyInitializer.initCollection(references);
        return references;
    }

    /**
     * @inheritDoc
     */
    public List<Reference> getAllReferences(Requirement requirement)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Reference.class);
        crit.createAlias("requirement", "req");
        crit.add(Restrictions.eq("req.name", requirement.getName()));
        crit.createAlias("req.repository", "repo");
        crit.add(Restrictions.eq("repo.uid", requirement.getRepository().getUid()));

        @SuppressWarnings("unchecked")
        List<Reference> references = crit.list();
		HibernateLazyInitializer.initCollection(references);
        return references;
    }

    /**
     * @inheritDoc
     */
    public Reference createReference(Reference reference) throws GreenPepperServerException
    {
        String projectName = reference.getSystemUnderTest().getProject().getName();
        String reqRepoUid = reference.getRequirement().getRepository().getUid();
        String requirementName = reference.getRequirement().getName();
        String testName = reference.getSpecification().getName();
        String testRepoUid = reference.getSpecification().getRepository().getUid();
        String sutName = reference.getSystemUnderTest().getName();
        String sections = reference.getSections();

        Requirement requirement = getOrCreateRequirement(reqRepoUid, requirementName);

		checkDuplicatedReference(requirement, testName, testRepoUid, sutName, sections);

        Specification specification = getOrCreateSpecification(sutName, testRepoUid, testName);
        SystemUnderTest sut = systemUnderTestDao.getByName(projectName, sutName);
            
        reference = Reference.newInstance(requirement, specification, sut, sections);
        sessionService.getSession().save(reference);

        return reference;
    }

	private void checkDuplicatedReference(Requirement requirement, String testName, String testRepoUid, String sutName, String sections)
			throws GreenPepperServerException
	{
		Set<Reference> references = requirement.getReferences();

		for (Reference reference : references)
		{
			if (reference.getSystemUnderTest().getName().equalsIgnoreCase(sutName)
				&& reference.getSpecification().getRepository().getUid().equalsIgnoreCase(testRepoUid)
				&& reference.getSpecification().getName().equalsIgnoreCase(testName)
				&& StringUtil.isEquals(reference.getSections(), sections))
			{
				throw new GreenPepperServerException(GreenPepperServerErrorKey.REFERENCE_CREATE_ALREADYEXIST,
													 "Reference already exist");
			}
		}
	}

    /**
     * @inheritDoc
     */
    public void removeReference(Reference reference) throws GreenPepperServerException
    {
        reference = get(reference);

		if (reference != null)
		{
			Requirement requirement = reference.getRequirement();
			requirement.removeReference(reference);
			reference.getSpecification().removeReference(reference);
			if(requirement.getReferences().isEmpty())
			{
				requirement.getRepository().removeRequirement(requirement);
				sessionService.getSession().delete(requirement);
			}

			sessionService.getSession().delete(reference);
		}
    }

    /**
     * @inheritDoc
     */
    public Reference updateReference(Reference oldReference, Reference newReference) throws GreenPepperServerException
    {
        removeReference(oldReference);
        return createReference(newReference);
    }

	/**
	 * @inheritDoc
	 */
	public Execution createExecution(Execution execution) throws GreenPepperServerException
	{
		sessionService.getSession().save(execution);
		return execution;
	}

	/**
     * @inheritDoc
     */
    public Execution runSpecification(SystemUnderTest systemUnderTest, Specification specification, boolean implemeted, String locale) throws GreenPepperServerException
    {
		specification = getOrCreateSpecification(systemUnderTest.getName(), specification.getRepository().getUid(),
												 specification.getName());
		systemUnderTest = systemUnderTestDao.getByName(systemUnderTest.getProject().getName(), systemUnderTest.getName());
		if (systemUnderTest == null)
		{
			throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_NOT_FOUND, "SystemUnderTest not found");
		}

		Execution exe = systemUnderTest.execute(specification, implemeted, null, locale);

		if (exe.wasRunned())
		{
			if (exe.wasRemotelyExecuted())
			{
				exe.setSpecification(getOrCreateSpecification(systemUnderTest.getName(), specification.getRepository().getUid(), specification.getName()));
				exe.setSystemUnderTest(systemUnderTestDao.getByName(systemUnderTest.getProject().getName(), systemUnderTest.getName()));
			}

			specification.addExecution(exe);
			sessionService.getSession().save(exe);
		}

		return exe;
	}

    /**
     * @inheritDoc
     */
    public Reference runReference(Reference reference, String locale) throws GreenPepperServerException
    {
		Reference loadedReference = get(reference);

		if (loadedReference == null)
		{
			throw new GreenPepperServerException(GreenPepperServerErrorKey.REFERENCE_NOT_FOUND, "Reference not found");
		}

		Execution exe = loadedReference.execute(false, locale);

		if (exe.wasRunned())
		{
			loadedReference.getSpecification().addExecution(exe);
			loadedReference.setLastExecution(exe);
			sessionService.getSession().save(exe);
		}

		return loadedReference;
	}

    /**
     * @inheritDoc
     */
    public List<Specification> getSpecifications(SystemUnderTest sut, Repository repository)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Specification.class);
        crit.createAlias("repository", "repo");
        crit.add(Restrictions.eq("repo.uid", repository.getUid()));
                
        crit.createAlias("targetedSystemUnderTests", "suts");
        crit.add(Restrictions.eq("suts.name", sut.getName()));
        crit.createAlias("suts.project", "sp");
        crit.add(Restrictions.eq("sp.name", sut.getProject().getName()));
        crit.addOrder(Order.asc("name"));

        @SuppressWarnings("unchecked")
        List<Specification> specifications = crit.list();
        HibernateLazyInitializer.initCollection(specifications);
        return specifications;
    }


	/**
	 * @inheritDoc
	 */
	public List<Execution> getSpecificationExecutions(Specification specification, SystemUnderTest sut, int maxResults)
	{
		final Criteria crit = sessionService.getSession().createCriteria(Execution.class);
		
		crit.add(Restrictions.eq("specification.id", specification.getId()));

		if (sut != null)
		{
			crit.createAlias("systemUnderTest", "sut");
			crit.add(Restrictions.eq("sut.name", sut.getName()));
		}

		/*
		crit.add(Restrictions.or(Restrictions.or(Restrictions.not(Restrictions.eq("errors", 0)),
												 Restrictions.not(Restrictions.eq("success", 0))),
								 Restrictions.or(Restrictions.not(Restrictions.eq("ignored", 0)),
												 Restrictions.not(Restrictions.eq("failures", 0)))));
		*/

		crit.addOrder(Order.desc("executionDate"));
		crit.setMaxResults(maxResults);

		@SuppressWarnings("unchecked")
		List<Execution> executions = crit.list();
		HibernateLazyInitializer.initCollection(executions);
		Collections.reverse(executions);
		return executions;
	}

	/**
	 * @inheritDoc
	 */
	public Execution getSpecificationExecution(Long id)
	{
		final Criteria crit = sessionService.getSession().createCriteria(Execution.class);

		crit.add(Restrictions.eq("id", id));

		@SuppressWarnings("unchecked")
		Execution execution = (Execution)crit.uniqueResult();
		HibernateLazyInitializer.init(execution);
		return execution;
	}
}