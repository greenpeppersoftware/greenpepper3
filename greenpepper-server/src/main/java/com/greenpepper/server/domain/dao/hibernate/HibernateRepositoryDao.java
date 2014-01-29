package com.greenpepper.server.domain.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.component.ContentType;
import com.greenpepper.server.domain.dao.ProjectDao;
import com.greenpepper.server.domain.dao.RepositoryDao;

public class HibernateRepositoryDao implements RepositoryDao
{
    private ProjectDao projectDao;
    private SessionService sessionService;

	public HibernateRepositoryDao(SessionService sessionService, ProjectDao projectDao) {
		this.sessionService = sessionService;
		this.projectDao = projectDao;
	}

    public HibernateRepositoryDao(SessionService sessionService)
    {
		this(sessionService, new HibernateProjectDao(sessionService));
    }

    @SuppressWarnings("unchecked")
    public List<Repository> getAll()
    {
        final Criteria criteria = sessionService.getSession().createCriteria(Repository.class);
        List<Repository> list = criteria.list();
		HibernateLazyInitializer.initCollection(list);
		return list;
	}

    /**
     * @inheritDoc
     */
    public Repository getByUID(String repositoryUid)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Repository.class);
        crit.add(Property.forName("uid").eq(repositoryUid));
        Repository repository = (Repository)crit.uniqueResult();
		HibernateLazyInitializer.init(repository);
		return repository;
	}

    /**
     * @inheritDoc
     */
    public Repository getByName(String projectName, String repositoryName)
    {
       final Criteria crit = sessionService.getSession().createCriteria(Repository.class);
        crit.add(Property.forName("name").eq(repositoryName));
        crit.createAlias("project", "p");
        crit.add(Restrictions.eq("p.name", projectName));
		Repository repository = (Repository)crit.uniqueResult();
		HibernateLazyInitializer.init(repository);
		return repository;
    }

    /**
     * @inheritDoc
     */
    public RepositoryType getTypeByName(String repositoryTypeName)
    {
        final Criteria crit = sessionService.getSession().createCriteria(RepositoryType.class);
        crit.add(Property.forName("name").eq(repositoryTypeName));
        RepositoryType repositoryType = (RepositoryType) crit.uniqueResult();
		HibernateLazyInitializer.init(repositoryType);
		return repositoryType;
	}
    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
	public List<Repository> getAll(String projectName)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Repository.class);
        if (projectName != null)
        {
            crit.createAlias("project", "p");
            crit.add(Restrictions.eq("p.name", projectName));
        }
        List<Repository> list = crit.list();
		HibernateLazyInitializer.initCollection(list);
		return list;
	}
    
    /**
     * @inheritDoc
     */
    public List<Repository> getAllTestRepositories(String projectName)
    {
        return getAllRepositories(projectName, ContentType.TEST);
    }

    /**
     * @inheritDoc
     */
    public List<Repository> getAllRequirementRepositories(String projectName)
    {
        return getAllRepositories(projectName, ContentType.REQUIREMENT);
    }

    @SuppressWarnings("unchecked")
    public List<Repository> getAllRepositories(ContentType type)
    {
        return getAllRepositories(null, type);
    }

    @SuppressWarnings("unchecked")
    private List<Repository> getAllRepositories(String projectName, ContentType type)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Repository.class);
        SimpleExpression restriction = Restrictions.eq("contentType", type);
        SimpleExpression bothRestriction = Restrictions.eq("contentType", ContentType.BOTH);
        crit.add(Restrictions.or(restriction, bothRestriction));
        if (projectName != null)
        {
            crit.createAlias("project", "p");
            crit.add(Restrictions.eq("p.name", projectName));
        }
		List<Repository> list = crit.list();
		HibernateLazyInitializer.initCollection(list);
		return list;
    }

    /**
     * @throws GreenPepperServerException
     * @inheritDoc
     */
    public Repository create(Repository newRepository) throws GreenPepperServerException
    {
        Project project = projectDao.getByName(newRepository.getProject().getName());
        if(project == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, "project not found");

        RepositoryType type = getTypeByName(newRepository.getType().getName());
        if(type == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.REPOSITORY_TYPE_NOT_FOUND, "Type not found");

        newRepository.setType(type);
        project.addRepository(newRepository);
        sessionService.getSession().update(project);

        return newRepository;
    }

	public void update(Repository repository) throws GreenPepperServerException
	{
		Repository repositoryToUpdate = getByUID(repository.getUid());
        if(repositoryToUpdate == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.REPOSITORY_NOT_FOUND, "Repository not found");
        
        if(!repository.getProject().getName().equals(repositoryToUpdate.getProject().getName()))
		{
            if(!repositoryToUpdate.getSpecifications().isEmpty() || !repositoryToUpdate.getRequirements().isEmpty())
                throw new GreenPepperServerException(GreenPepperServerErrorKey.REPOSITORY_DOC_ASSOCIATED, "Doc associated");

            Project newProject = projectDao.getByName(repository.getProject().getName());
            if(newProject == null)
                throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, "project not found");

            Project oldProject = repositoryToUpdate.getProject();
            oldProject.removeRepository(repositoryToUpdate);
            
            if(oldProject.getRepositories().isEmpty())
            {
            	sessionService.getSession().delete(oldProject);
            }
            else
            {
            	sessionService.getSession().update(oldProject);
            }
            
            newProject.addRepository(repositoryToUpdate);
            sessionService.getSession().update(newProject);
		}

        repositoryToUpdate.setBaseRepositoryUrl(repository.getBaseRepositoryUrl());
        repositoryToUpdate.setBaseTestUrl(repository.getBaseTestUrl());
        repositoryToUpdate.setBaseUrl(repository.getBaseUrl());
        repositoryToUpdate.setContentType(repository.getContentType());
        repositoryToUpdate.setName(repository.getName());
        repositoryToUpdate.setUsername(repository.getUsername());
        repositoryToUpdate.setPassword(repository.getPassword());

        sessionService.getSession().update(repositoryToUpdate);
	}

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public List<RepositoryType> getAllTypes()
    {
        final Criteria crit = sessionService.getSession().createCriteria(RepositoryType.class);
        List<RepositoryType> list = crit.list();
		HibernateLazyInitializer.initCollection(list);
		return list;
	}

    /**
     * @inheritDoc
     */
    public RepositoryType create(RepositoryType repositoryType)
    {
        sessionService.getSession().save(repositoryType);
        return repositoryType;
    }

    /**
     * @inheritDoc
     */
    public void remove(String repositoryUid) throws GreenPepperServerException
    {
		Repository repositoryToDelete = getByUID(repositoryUid);
		if(repositoryToDelete == null) return;
		
		if(repositoryToDelete.getRequirements().size() > 0 || repositoryToDelete.getSpecifications().size() > 0)
			throw new GreenPepperServerException(GreenPepperServerErrorKey.REPOSITORY_DOC_ASSOCIATED, "Requirement or specifications associated");

		sessionService.getSession().delete(repositoryToDelete);
    }
}
