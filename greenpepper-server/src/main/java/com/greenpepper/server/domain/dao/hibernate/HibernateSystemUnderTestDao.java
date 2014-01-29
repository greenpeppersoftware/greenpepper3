package com.greenpepper.server.domain.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;
import com.greenpepper.server.domain.dao.ProjectDao;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;

public class HibernateSystemUnderTestDao implements SystemUnderTestDao
{
    private SessionService sessionService;
    private ProjectDao projectDao;
    
    public HibernateSystemUnderTestDao(SessionService sessionService, ProjectDao projectDao)
    {
        this.sessionService = sessionService;
        this.projectDao = projectDao;
    }

	public HibernateSystemUnderTestDao(SessionService sessionService) {
		this(sessionService, new HibernateProjectDao(sessionService));
	}

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")        
    public List<EnvironmentType> getAllEnvironmentTypes()
    {
		final Criteria crit = sessionService.getSession().createCriteria(EnvironmentType.class);
		crit.addOrder(Order.asc("name"));
		List<EnvironmentType> list = crit.list();
		HibernateLazyInitializer.initCollection(list);
		return list;
	}

    /**
     * @inheritDoc
     */
    public EnvironmentType getEnvironmentTypeByName(String name)
    {
        final Criteria crit = sessionService.getSession().createCriteria(EnvironmentType.class);
        crit.add(Property.forName("name").eq(name));
        EnvironmentType environmentType = (EnvironmentType) crit.uniqueResult();
		HibernateLazyInitializer.init(environmentType);
		return environmentType;
	}

    /**
     * @inheritDoc
     */
    public EnvironmentType create(EnvironmentType environmentType)
    {
        sessionService.getSession().save(environmentType);
        return environmentType;
    }

    /**
     * @inheritDoc
     */
    public Runner getRunnerByName(String name)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Runner.class);
        crit.add(Property.forName("name").eq(name));
        Runner runner = (Runner) crit.uniqueResult();
		HibernateLazyInitializer.init(runner);
		return runner;
	}

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
	public List<Runner> getAllRunners()
    {
		final Criteria crit = sessionService.getSession().createCriteria(Runner.class);
		crit.addOrder(Order.asc("name"));
		List<Runner> list = crit.list();
		HibernateLazyInitializer.initCollection(list);
		return list;
	}

    /**
     * @inheritDoc
     */
    public Runner create(Runner runner) throws GreenPepperServerException
    {
        if(getRunnerByName(runner.getName()) != null)
            throw new GreenPepperServerException( GreenPepperServerErrorKey.RUNNER_ALREADY_EXISTS, "Runner already exists");

        EnvironmentType envType = getEnvironmentTypeByName(runner.getEnvironmentType().getName());
        if(envType == null)
            throw new GreenPepperServerException( GreenPepperServerErrorKey.ENVTYPE_NOT_FOUND, "Environment not found");
        
        runner.setEnvironmentType(envType);
        sessionService.getSession().save(runner);
        return runner;
    }

    /**
     * @inheritDoc
     */
    public Runner update(String oldRunnerName, Runner runner) throws GreenPepperServerException
    {
        if(!runner.getName().equals(oldRunnerName) && getRunnerByName(runner.getName()) != null)
            throw new GreenPepperServerException( GreenPepperServerErrorKey.RUNNER_ALREADY_EXISTS, "Runner already exists");

        Runner runnerToUpdate = getRunnerByName(oldRunnerName);
        if(runnerToUpdate == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, "Runner not found");

        EnvironmentType newEnvType = getEnvironmentTypeByName(runner.getEnvironmentType().getName());
        if(newEnvType == null)
            throw new GreenPepperServerException( GreenPepperServerErrorKey.ENVTYPE_NOT_FOUND, "Environment not found");

        runnerToUpdate.setName(runner.getName());
        runnerToUpdate.setEnvironmentType(newEnvType);
        runnerToUpdate.setCmdLineTemplate(runner.getCmdLineTemplate());
        runnerToUpdate.setMainClass(runner.getMainClass());
        runnerToUpdate.setServerName(runner.getServerName());
        runnerToUpdate.setServerPort(runner.getServerPort());
        runnerToUpdate.setClasspaths(runner.getClasspaths());
        runnerToUpdate.setSecured(runner.isSecured());
        
		sessionService.getSession().update(runnerToUpdate);
        return runnerToUpdate;
    }

    /**
     * @inheritDoc
     */
    public void removeRunner(String runnerName) throws GreenPepperServerException
    {
        Runner runner = getRunnerByName(runnerName);
        
        if(runner == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, "Runner not found");

        if(!getAllForRunner(runnerName).isEmpty())
            throw new GreenPepperServerException(GreenPepperServerErrorKey.RUNNER_SUT_ASSOCIATED, "Runner is associated with suts");
            
        sessionService.getSession().delete(runner);
    }

    /**
     * @inheritDoc
     */
    public SystemUnderTest getByName(String projectName, String sutName)
    {
        final Criteria crit = sessionService.getSession().createCriteria(SystemUnderTest.class);
        crit.add(Property.forName("name").eq(sutName));
        crit.createAlias("project", "p");
        crit.add(Restrictions.eq("p.name", projectName));
        SystemUnderTest systemUnderTest = (SystemUnderTest) crit.uniqueResult();
		HibernateLazyInitializer.init(systemUnderTest);
		return systemUnderTest;
	}

    /**
     * @inheritDoc
     */
    public List<SystemUnderTest> getAllForProject(String projectName)
    {
        final Criteria crit = sessionService.getSession().createCriteria(SystemUnderTest.class);
        crit.createAlias("project", "p");
        crit.add(Restrictions.eq("p.name", projectName));
		crit.addOrder(Order.asc("name"));

		@SuppressWarnings("unchecked")
        List<SystemUnderTest> systemUnderTests = crit.list();
		HibernateLazyInitializer.initCollection(systemUnderTests);
        return systemUnderTests;
    }

    /**
     * @inheritDoc
     */
    public List<SystemUnderTest> getAllForRunner(String runnerName)
    {
        final Criteria crit = sessionService.getSession().createCriteria(SystemUnderTest.class);
        crit.createAlias("runner", "r");
        crit.add(Restrictions.eq("r.name", runnerName));
		crit.addOrder(Order.asc("name"));

		@SuppressWarnings("unchecked")
        List<SystemUnderTest> systemUnderTests = crit.list();
		HibernateLazyInitializer.initCollection(systemUnderTests);
        return systemUnderTests;
    }

    /**
     * @inheritDoc
     */
    public List<Reference> getAllReferences(SystemUnderTest sut)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Reference.class);
        crit.createAlias("systemUnderTest", "sut");
        crit.add(Restrictions.eq("sut.name", sut.getName()));
        crit.createAlias("sut.project", "sp");
        crit.add(Restrictions.eq("sp.name", sut.getProject().getName()));
		crit.addOrder(Order.asc("sp.name"));

		@SuppressWarnings("unchecked")
        List<Reference> references = crit.list();
		HibernateLazyInitializer.initCollection(references);
        return references;
    }

    /**
     * @inheritDoc
     */
    public List<Specification> getAllSpecifications(SystemUnderTest sut)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Specification.class);
        crit.createAlias("targetedSystemUnderTests", "sut");
        crit.add(Restrictions.eq("sut.name", sut.getName()));
        crit.createAlias("sut.project", "sp");
        crit.add(Restrictions.eq("sp.name", sut.getProject().getName()));
		crit.addOrder(Order.asc("sp.name"));

		@SuppressWarnings("unchecked")
        List<Specification> specifications = crit.list();
		HibernateLazyInitializer.initCollection(specifications);
        return specifications;
    }

    /**
     * @inheritDoc
     */
    public SystemUnderTest create(SystemUnderTest newSut) throws GreenPepperServerException
    {
        Runner runner = getRunnerByName(newSut.getRunner().getName());
        if(runner == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, "Runner not found");
        newSut.setRunner(runner);
        
        Project project = projectDao.getByName(newSut.getProject().getName());
        if(project == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, "project not found");
        project.addSystemUnderTest(newSut);
        
        
        sessionService.getSession().update(project);
        
        return newSut;
    }

    /**
     * @inheritDoc
     */
    public SystemUnderTest update(String oldSutName, SystemUnderTest updatedSut) throws GreenPepperServerException
    {
        if(!updatedSut.getName().equals(oldSutName) && getByName(updatedSut.getProject().getName(), updatedSut.getName()) != null)
            throw new GreenPepperServerException( GreenPepperServerErrorKey.SUT_ALREADY_EXISTS, "SUT already exists");

        SystemUnderTest sutToUpdate = getByName(updatedSut.getProject().getName(), oldSutName);
        Runner runner = getRunnerByName(updatedSut.getRunner().getName());
        if(runner == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.RUNNER_NOT_FOUND, "Runner not found");
        sutToUpdate.setRunner(runner);
        
        sutToUpdate.setName(updatedSut.getName());
        sutToUpdate.setSutClasspaths(updatedSut.getSutClasspaths());
        sutToUpdate.setFixtureClasspaths(updatedSut.getFixtureClasspaths());
        sutToUpdate.setFixtureFactory(updatedSut.getFixtureFactory());
        sutToUpdate.setFixtureFactoryArgs(updatedSut.getFixtureFactoryArgs());
        sutToUpdate.setIsDefault(updatedSut.isDefault());
		sutToUpdate.setProjectDependencyDescriptor(updatedSut.getProjectDependencyDescriptor());

		sessionService.getSession().update(sutToUpdate);
        return sutToUpdate;
    }

    /**
     * @inheritDoc
     */
    public void remove(String projectName, String sutName) throws GreenPepperServerException
    {
        SystemUnderTest sut = getByName(projectName, sutName);
        
        if(sut == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_NOT_FOUND, "SUT not found");
        
        if(getAllReferences(sut).size() > 0)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_REFERENCE_ASSOCIATED, "The SUT has associated references");
        
        if(getAllSpecifications(sut).size() > 0)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_SPECIFICATION_ASSOCIATED, "The SUT has associated specifications");

        if(getAllExecutions(sut).size() > 0)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.SUT_EXECUTION_ASSOCIATED, "The SUT has associated specifications");
        
        sut.getProject().removeSystemUnderTest(sut);
        sessionService.getSession().delete(sut);
    }

    /**
     * @inheritDoc
     */
    public void setAsDefault(SystemUnderTest systemUnderTest) throws GreenPepperServerException
    {
        Project project = projectDao.getByName(systemUnderTest.getProject().getName());
        if(project == null)
            throw new GreenPepperServerException(GreenPepperServerErrorKey.PROJECT_NOT_FOUND, "project not found");
        
        for(SystemUnderTest sut : project.getSystemUnderTests())
        {
            sut.setIsDefault(sut.getName().equals(systemUnderTest.getName()));
        }    
        
        sessionService.getSession().update(project);
    }
    
    public List<Execution> getAllExecutions(SystemUnderTest systemUnderTest)
    {
        final Criteria crit = sessionService.getSession().createCriteria(Execution.class);
        crit.createAlias("systemUnderTest", "sut");
        crit.add(Restrictions.eq("sut.name", systemUnderTest.getName()));
        crit.createAlias("sut.project", "sp");
        crit.add(Restrictions.eq("sp.name", systemUnderTest.getProject().getName()));

        @SuppressWarnings("unchecked")
        List<Execution> executions = crit.list();
		HibernateLazyInitializer.initCollection(executions);        
        return executions; 
    }
}
