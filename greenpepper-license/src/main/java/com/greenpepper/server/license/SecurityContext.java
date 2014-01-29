package com.greenpepper.server.license;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.greenpepper.server.domain.Repository;

public class SecurityContext
{
    private final static TimeSource DEFAULT_TIME_SOURCE = new TimeSource() { public Date now() { return new Date(); } };

    private Set<Repository> grantedApplications = new HashSet<Repository>();
    private TimeSource timeSource;

    public boolean isMaxReached(Repository repository, int licenseMaxUsers)
    {
        Set<Repository> clonedApp = new HashSet<Repository>(grantedApplications);
        
        int counter = 0;
        clonedApp.remove(repository);
        for (Repository repo : clonedApp)
        {
            if (repo.getType().equals(repository.getType()) && !repo.getBaseUrl().equals(repository.getBaseUrl()))
            {
                counter += repo.getMaxUsers();
            }
        }
        
        return repository.getMaxUsers() + counter > licenseMaxUsers;
    }

    public void grantAccess(Repository repository)
    {
        grantedApplications.add(repository);
    }

    public void denyAccess(Repository repository)
    {
        grantedApplications.remove(repository);
    }

    public SecurityContext()
    {
        this(DEFAULT_TIME_SOURCE);
    }

    public SecurityContext(TimeSource timeSource)
    {
        this.timeSource = timeSource;
    }

    public Date now()
    {
        return timeSource.now();
    }
}
