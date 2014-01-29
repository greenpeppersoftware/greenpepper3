package com.greenpepper.server.database.hibernate.upgrades;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenpepper.runner.repository.AtlassianRepository;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.dao.RepositoryDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateRepositoryDao;

public class UpgradeOf_1_0 implements ServerVersionUpgrader
{
	private SessionService service;
	
	public String upgradedTo()
	{
		return "1.1";
	}

	public void upgrade(SessionService service) throws Exception
	{
		this.service = service;
		upgradeRepositoryTypes();
		upgradeRepositories();
		upgradeExecutions();
	}
	
	private void upgradeRepositoryTypes()
	{
	    RepositoryDao repoDao = new HibernateRepositoryDao(service);
	    List<RepositoryType> types = repoDao.getAllTypes();
	    for(RepositoryType type : types)
	    {
	    	if(type.getName().equals("JIRA"))
	    	{
	    		type.setRepositoryClass(AtlassianRepository.class.getName());
	            type.setDocumentUrlFormat("%s/browse/%s");
        		type.setTestUrlFormat(null);
	    	}
	    	else if(type.getName().equals("CONFLUENCE"))
	    	{
	    		type.setRepositoryClass(AtlassianRepository.class.getName());
	    		type.setDocumentUrlFormat("%s/%s");
        		type.setTestUrlFormat(null);
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	service.getSession().update(type);
	    }
	}
	
	private void upgradeRepositories()
	{
	    RepositoryDao repoDao = new HibernateRepositoryDao(service);
	    List<Repository> repos = repoDao.getAll();
	    for(Repository repo : repos)
	    {
	    	repo.setUsername(null);
	    	repo.setPassword(null);
	    	if(repo.getType().getName().equals("JIRA"))
	    	{
	    		repo.setBaseTestUrl(parseTestJIRAUrl(repo.getBaseTestUrl()));
	    	}
	    	else if(repo.getType().getName().equals("CONFLUENCE"))
	    	{
	    		repo.setBaseTestUrl(parseTestConfluenceUrl(repo.getBaseTestUrl()));
	    	}
	    	else
	    	{
	    		continue;
	    	}
	    	
	    	service.getSession().update(repo);
	    }
	}
	
	private void upgradeExecutions() throws Exception
	{
        String hqlUpdate = "update Execution set ignored = :ignored";
        service.getSession().createQuery( hqlUpdate ).setInteger("ignored", 0 ).executeUpdate();
    }
	
	private String parseTestJIRAUrl(String oldURL)
	{
		return "http://" + oldURL.replaceAll("#", "?handler=");
	}
	
	private String parseTestConfluenceUrl(String oldURL)
	{
		String baseUrl = "";
		String handler = "";
		String spaceKey = "";
		
        Pattern pattern = Pattern.compile( "(.+)[#](.+)[!](.+)" );
        Matcher matcher = pattern.matcher( oldURL );

        if (matcher.find() && matcher.groupCount() == 3)
        {
        	baseUrl = matcher.group( 1 );
        	handler = matcher.group( 2 );
        	spaceKey = matcher.group( 3);
        }
        
		return "http://" + baseUrl + "?handler=" + handler + "#" + spaceKey;
	}
}