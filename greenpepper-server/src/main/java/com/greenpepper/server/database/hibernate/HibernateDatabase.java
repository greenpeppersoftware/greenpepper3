package com.greenpepper.server.database.hibernate;

import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.RepositoryTypeClass;
import com.greenpepper.server.domain.Requirement;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.SystemUnderTest;

public class HibernateDatabase
{
    private final AnnotationConfiguration cfg;

    public HibernateDatabase(Properties properties) throws HibernateException
    {
        cfg = new AnnotationConfiguration();        
        cfg.setProperties(properties); 
        setAnnotadedClasses();
    }
    
    public void createDatabase() throws HibernateException
    {
        new SchemaExport(cfg).create(false, true);
    }

    public void dropDatabase() throws HibernateException
    {
        new SchemaExport(cfg).drop(false, true);
    }
    
    public Configuration getConfiguration()
    {
        return cfg;
    }    

    public SessionFactory getSessionFactory() throws HibernateException
    {
        return cfg.buildSessionFactory();
    }
    
    private void setAnnotadedClasses()
    {
        cfg.addAnnotatedClass(SystemInfo.class)
        .addAnnotatedClass(Project.class)
        .addAnnotatedClass(Runner.class)
        .addAnnotatedClass(EnvironmentType.class)
        .addAnnotatedClass(Repository.class)
        .addAnnotatedClass(RepositoryType.class)
        .addAnnotatedClass(RepositoryTypeClass.class)
        .addAnnotatedClass(SystemUnderTest.class)
        .addAnnotatedClass(Requirement.class)
        .addAnnotatedClass(Specification.class)
        .addAnnotatedClass(Reference.class)
        .addAnnotatedClass(Execution.class);
    }

}
