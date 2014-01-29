package com.greenpepper.server.domain.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Property;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.dao.ProjectDao;

public class HibernateProjectDao
		implements ProjectDao
{

	private SessionService sessionService;

	public HibernateProjectDao(SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @inheritDoc
	 */
	public Project getByName(String name)
	{
		final Criteria crit = sessionService.getSession().createCriteria(Project.class);
		crit.add(Property.forName("name").eq(name));
		Project project = (Project)crit.uniqueResult();
		HibernateLazyInitializer.init(project);
		return project;
	}

	@SuppressWarnings("unchecked")
	public List<Project> getAll()
	{
		final Criteria criteria = sessionService.getSession().createCriteria(Project.class);
		List<Project> list = criteria.list();
		HibernateLazyInitializer.initCollection(list);
		return list;
	}

	/**
	 * @inheritDoc
	 */
	public Project create(String name)
			throws GreenPepperServerException {

		if (getByName(name) != null)
		{
			throw new GreenPepperServerException( GreenPepperServerErrorKey.PROJECT_ALREADY_EXISTS,
												  "Project already exists");
		}

		Project project = Project.newInstance(name);
		sessionService.getSession().save(project);
		return project;
	}

	/**
	 * @inheritDoc
	 */
	public void remove(String name)
			throws GreenPepperServerException
	{
		Project project = getByName(name);
		if (project == null) return;

		if (project.getRepositories().size() > 0)
		{
			throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_REPOSITORY_ASSOCIATED,
												 "Repository associated");
		}

		if (project.getSystemUnderTests().size() > 0)
		{
			throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_SUTS_ASSOCIATED,
												 "System under tests associated");
		}

		sessionService.getSession().delete(project);
	}

	public Project update(String oldProjectName, Project project)
			throws GreenPepperServerException {

		if(!oldProjectName.equals(project.getName()) && getByName(project.getName()) != null)
			throw new GreenPepperServerException( GreenPepperServerErrorKey.PROJECT_ALREADY_EXISTS, "Project already exists");

		Project projectToUpdate = getByName(oldProjectName);
		if(projectToUpdate == null)
			throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, "Project not found");

		projectToUpdate.setName(project.getName());

		sessionService.getSession().update(projectToUpdate);
		return projectToUpdate;
	}
}