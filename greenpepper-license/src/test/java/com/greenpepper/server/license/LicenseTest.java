package com.greenpepper.server.license;

import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.util.TestCase;


import java.util.Date;

public class LicenseTest extends TestCase
{
    @SuppressWarnings("deprecation")
    private static final Date SIX_MONTH_AGO =  rollMonths(new Date(), -6);
    private static final Date A_YEAR_AGO =  rollYears(new Date(), -1);
    private static final Date IN_USAGE_PERIOD = rollMonths(SIX_MONTH_AGO, 4);
    private static final Date ONE_YEAR_AFTER = rollYears(SIX_MONTH_AGO, 1);
    private static final Date TWO_YEARS_AFTER = rollYears(SIX_MONTH_AGO, 2);

    public void testOpenSourceLicenseShouldHaveAllPermissionsFromStartingDate()
    {
        License license = License.openSource("License Holder", SIX_MONTH_AGO, ONE_YEAR_AFTER);

        assertHasAllPermissions(license, securityContextOn(SIX_MONTH_AGO)); // Inclusive
        assertHasAllPermissions(license, securityContextOn(IN_USAGE_PERIOD));
        assertSupportPeriodRespected(license);
    }

	public void testAcademicLicenseShouldHaveAllPermissionsFromStartingDate()
    {
        License license = License.academic("License Holder", SIX_MONTH_AGO, ONE_YEAR_AFTER);

        assertHasAllPermissions(license, securityContextOn(SIX_MONTH_AGO)); // Inclusive
        assertHasAllPermissions(license, securityContextOn(IN_USAGE_PERIOD));
        assertSupportPeriodRespected(license);
    }

    public void testEvaluationLicenseShouldHaveAllPermissionsInValidityPeriod()
    {        
        License license = License.evaluation("License Holder", SIX_MONTH_AGO, ONE_YEAR_AFTER);

        assertHasAllPermissions(license, securityContextOn(SIX_MONTH_AGO)); // Inclusive
        assertHasAllPermissions(license, securityContextOn(IN_USAGE_PERIOD));
        
        license = License.evaluation("License Holder", A_YEAR_AGO, SIX_MONTH_AGO);
        assertHasNoPermission(license, securityContextOn(SIX_MONTH_AGO));   // Exclusive
        assertHasNoPermission(license, securityContextOn(ONE_YEAR_AFTER));
    }

    public void testCommercialLicenseShouldHaveAllPermissionsFromStartingDate()
    {
        final Date start = SIX_MONTH_AGO;
        final Date inUsagePeriod = rollYears(start, 5);
        final Date supportEnd = rollYears(start, 2);
        int maxUsers = 75;
        
        License license = License.commercial("License Holder", start, supportEnd, maxUsers);

        assertHasAllPermissions(license, securityContextOn(start)); // Inclusive
        assertHasAllPermissions(license, securityContextOn(inUsagePeriod));
        assertSupportPeriodRespected(license);
    }
    
    public void testCommercialLicenseShouldHaveAllPermissionsIfMaxUsersNotReached()
    {
        final Date start = SIX_MONTH_AGO;
        final Date inUsagePeriod = rollYears(start, 5);
        final Date supportEnd = rollYears(start, 2);
        int maxUsers = 50;
        
        License license = License.commercial("License Holder", start, supportEnd, maxUsers);
        
        Repository appWithLessThenMaxUsers = repositoryOf(25, "JIRA-1", "url1", "JIRA");
        assertHasAllPermissions(license, securityContextOn(inUsagePeriod), appWithLessThenMaxUsers); 
        
        Repository appWithMoreThenMaxUsers = repositoryOf(75, "JIRA-1", "url1", "JIRA");
        assertHasNoPermission(license, securityContextOn(inUsagePeriod), appWithMoreThenMaxUsers); 
    }
    
    public void testCommercialDontAllowUpdatesAfterExpiryDate()
    {
        final Date start = SIX_MONTH_AGO;
        final Date inUsagePeriod = rollYears(start, 2);
        final Date inUpdateExpiryPeriod = rollYears(start, 10);
        final Date supportEnd = rollYears(start, 5);

        License license = License.commercial("License Holder", start, supportEnd, 50);
        try
        {
            license.verify(inUsagePeriod);
            license.verify(inUpdateExpiryPeriod);
            fail();
        }
        catch (GreenPepperLicenceException e)
        {
            assertTrue(true);
        } 
    }
    
    public void testTalliesTheLimitOfUsersByApplicationTypes()
    {
        final Date start = SIX_MONTH_AGO;
        final Date inUsagePeriod = rollYears(start, 5);
        final Date end = rollYears(start, 2);
        SecurityContext securityCtx = securityContextOn(inUsagePeriod);
        int maxUsers = 50;
        
        License license = License.commercial("License Holder", start, end, maxUsers);

        Repository jiraWithLessThenMaxUsers = repositoryOf(50, "JIRA-1", "url1", "JIRA");
        assertHasAllPermissions(license, securityCtx, jiraWithLessThenMaxUsers); 

        Repository confluenceWithLessThenMaxUsers = repositoryOf(50, "JIRA-2", "url2", "CONFLUENCE");
        assertHasAllPermissions(license, securityCtx, confluenceWithLessThenMaxUsers); 

        Repository anotherJiraThatWillBustTheUserLimit = repositoryOf(50, "JIRA-2", "url2", "JIRA");
        assertHasNoPermission(license, securityCtx, anotherJiraThatWillBustTheUserLimit); 
    }
    
    public void testSameApplicationIsNotCountedMoreThanOnce()
    {
        final Date start = SIX_MONTH_AGO;
        final Date inUsagePeriod = rollYears(start, 5);
        final Date end = rollYears(start, 2);
        SecurityContext securityCtx = securityContextOn(inUsagePeriod);
        int maxUsers = 50;
        
        License license = License.commercial("License Holder", start, end, maxUsers);

        Repository app1WithLessThenMaxUsers = repositoryOf(50, "JIRA-PROJECT-1", "url1", "JIRA");
        Repository app2WithLessThenMaxUsers = repositoryOf(50, "JIRA-PROJECT-2", "url1", "JIRA");
        assertHasAllPermissions(license, securityCtx, app1WithLessThenMaxUsers);
        assertHasAllPermissions(license, securityCtx, app2WithLessThenMaxUsers);
        assertHasAllPermissions(license, securityCtx, app1WithLessThenMaxUsers);
        assertHasAllPermissions(license, securityCtx, app2WithLessThenMaxUsers);
    }
    
    public void testChangesInTheMaxNumberOfUsersForAnAppLicationIsDetected()
    {
        final Date start = SIX_MONTH_AGO;
        final Date inUsagePeriod = rollYears(start, 5);
        final Date end = rollYears(start, 2);
        SecurityContext securityCtx = securityContextOn(inUsagePeriod);
        int maxUsers = 50;
        
        License license = License.commercial("License Holder", start, end, maxUsers);

        Repository appWithChangingMaxUsers = repositoryOf(50, "JIRA-1", "url1", "JIRA");
        assertHasAllPermissions(license, securityCtx, appWithChangingMaxUsers);
        appWithChangingMaxUsers.setMaxUsers(75);
        assertHasNoPermission(license, securityCtx, appWithChangingMaxUsers); 
        appWithChangingMaxUsers.setMaxUsers(10);
        assertHasAllPermissions(license, securityCtx, appWithChangingMaxUsers); 
    }

    private void assertHasAllPermissions(License license, SecurityContext securityContext, Repository repository)
    {
        for (Permission permission : Permission.values())
        {
            try
            {
                license.verify(repository, securityContext, permission);
                assertTrue(true);
            }
            catch(GreenPepperLicenceException ex)
            {
                fail();
            }
        }
    }

    private void assertHasAllPermissions(License license, SecurityContext securityContext)
    {
        assertHasAllPermissions(license, securityContext, Repository.newInstance("any"));
    }

    private void assertHasNoPermission(License license, SecurityContext securityContext, Repository repository)
    {
        for (Permission permission : Permission.values())
        {
            try
            {
                license.verify(repository, securityContext, permission);
                fail();
            }
            catch(GreenPepperLicenceException ex)
            {
                assertTrue(true);
            }
        }
    }

    private void assertHasNoPermission(License license, SecurityContext securityContext)
    {
        assertHasNoPermission(license, securityContext, Repository.newInstance("any"));
    }

	private void assertSupportPeriodRespected(License license) 
	{
        
        try 
        {
			license.verify(IN_USAGE_PERIOD);
		} 
        catch (GreenPepperLicenceException e) 
        {
        	fail();
		}
        
        try 
        {
    		license.verify(TWO_YEARS_AFTER);
        	fail();
		} 
        catch (GreenPepperLicenceException e) 
        {
		}
	}
    
    private SecurityContext securityContextOn(final Date date)
    {
        return new SecurityContext(new TimeSource(){ public Date now(){ return date; }});
    }
    
    private Repository repositoryOf(int maxUsers, String uid, String baseUrl, String type)
    {
        Repository repo = Repository.newInstance(uid);
        repo.setProject(Project.newInstance("Project"));
        repo.setType(RepositoryType.newInstance(type));
        repo.setName(uid);
        repo.setBaseUrl(baseUrl);
        repo.setMaxUsers(maxUsers);
        
        return repo;
    }

    @SuppressWarnings("deprecation")
    private static Date rollYears(Date date, int years)
    {
        Date result = new Date(date.getTime());
        result.setYear(date.getYear() + years);
        return result;
    }

    @SuppressWarnings("deprecation")
    private static Date rollMonths(Date date, int months)
    {
        Date result = new Date(date.getTime());
        result.setMinutes(date.getMonth() + months);
        return result;
    }
    
    public void testCanAccessHolderNameFromLicense()
    {
        final Date start = SIX_MONTH_AGO;
        final Date oneYearAfterStart = rollYears(start, +1);
        License license = License.openSource("License Holder", start, oneYearAfterStart);
    
        assertEquals("License Holder", license.getHolderName());
    }
}
