package com.greenpepper.server.database.hibernate.upgrades;

import java.util.List;

import com.greenpepper.repository.FileSystemRepository;
import com.greenpepper.runner.repository.AtlassianRepository;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.dao.RepositoryDao;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateRepositoryDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemUnderTestDao;

public class UpgradeOf_1_1 implements ServerVersionUpgrader
{
	private SessionService service;
	
	public String upgradedTo() 
	{
		return "1.2";
	}
	
	public void upgrade(SessionService service) throws Exception 
	{
		this.service = service;
        
		upgradeRunners();
		upgradeRepositoryTypes();
		upgradeRepositoryUID();
	}

	private void upgradeRunners() 
	{
		SystemUnderTestDao sutDao = new HibernateSystemUnderTestDao(service);
		EnvironmentType java = sutDao.getEnvironmentTypeByName("JAVA");
		for(Runner runner : sutDao.getAllRunners())
			runner.setEnvironmentType(java);
	}

	private void upgradeRepositoryTypes()
	{
	    RepositoryDao repoDao = new HibernateRepositoryDao(service);
	    SystemUnderTestDao sutDao = new HibernateSystemUnderTestDao(service);
		EnvironmentType java = sutDao.getEnvironmentTypeByName("JAVA");
		EnvironmentType dotnet = sutDao.getEnvironmentTypeByName(".NET");
		
	    List<RepositoryType> types = repoDao.getAllTypes();
	    for(RepositoryType type : types)
	    {
	    	if(type.getName().equals("JIRA"))
	    	{
	            type.registerClassForEnvironment(AtlassianRepository.class.getName(),java);
	            type.registerClassForEnvironment("GreenPepper.Repositories.AtlassianRepository",dotnet);
	    	}
	    	else if(type.getName().equals("CONFLUENCE"))
	    	{
	            type.registerClassForEnvironment(AtlassianRepository.class.getName(),java);
	            type.registerClassForEnvironment("GreenPepper.Repositories.AtlassianRepository",dotnet);
	    	}
	    	else if(type.getName().equals("FILE"))
	    	{
	            type.registerClassForEnvironment(FileSystemRepository.class.getName(),java);
	            type.registerClassForEnvironment("GreenPepper.Repositories.FileSystemRepository",dotnet);
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	service.getSession().update(type);
	    }
	}
	
	private void upgradeRepositoryUID()
	{
	    RepositoryDao repoDao = new HibernateRepositoryDao(service);
		RepositoryType JIRA = RepositoryType.newInstance("JIRA");
		RepositoryType CONFLUENCE = RepositoryType.newInstance("CONFLUENCE");
		
	    for(Repository repo : repoDao.getAll())
	    {
	    	String uid = repo.getUid();
	    	if((repo.getType().equals(JIRA) || repo.getType().equals(CONFLUENCE)) && uid.indexOf("/") >= 0)
	    	{
		    	String firstPart = uid.substring(0, uid.lastIndexOf("/"));
		    	String lastPart = uid.substring(uid.lastIndexOf("/") + 1);
		    	repo.setUid(firstPart + "-" + lastPart);
		    	service.getSession().update(repo);
	    	}
	    }
	}
}
