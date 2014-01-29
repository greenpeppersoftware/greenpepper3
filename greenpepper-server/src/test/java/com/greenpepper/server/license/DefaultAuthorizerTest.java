package com.greenpepper.server.license;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.prefs.Preferences;

import com.greenpepper.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.util.URIUtil;
import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

public class DefaultAuthorizerTest
		extends AbstractDBUnitHibernateMemoryTest
{
    private static final String DATAS = "/dbunit/datas/DefaultAuthorizerTest.xml";
    @SuppressWarnings("deprecation")
    private static final Date YEAR_TWO_THOUSAND =  new Date(100, 0, 0);
    
    private Date start;
    private Date end;
    private Authorizer authorizer;

    protected void setUp() throws Exception
    {
        super.setUp();
        insertIntoDatabase(DATAS);
        
        start = YEAR_TWO_THOUSAND;
        end = rollYears(start, 1000); // Good until year 3000 !
        
        generateLicence();
        Properties properties = new LicenceProperties(generateLicence());
        authorizer = new DefaultAuthorizer(this, properties);
        authorizer.initialize(start);
    }
    
    public void testShouldRenderDecisionBasedOnTheInstalledDefaultLicense() throws GreenPepperLicenceException
    {
        Repository myFirstJira = repositoryOf(50, "JIRA-1", "UID-1", "Prj");
        myFirstJira.setBaseUrl("URL-1");
        authorizer.verify(myFirstJira, Permission.EXECUTE);
        
        Repository mySecondJiraThatShouldBustTheMaxUsers = repositoryOf(50, "JIRA-2", "UID-2", "Prj");
        mySecondJiraThatShouldBustTheMaxUsers.setBaseUrl("URL-2");
        try
        {
            authorizer.verify(mySecondJiraThatShouldBustTheMaxUsers, Permission.EXECUTE);
            fail();
        }
        catch(Exception GreenPepperLicenceException)
        {
            assertTrue(true);
        }
    }
    
    private Repository repositoryOf(int maxUsers, String uid, String baseUrl, String type)
    {
        Repository repo = Repository.newInstance(uid);
        repo.setProject(Project.newInstance("Project"));
        repo.setType(RepositoryType.newInstance(type));
        repo.setBaseUrl(baseUrl);
        repo.setMaxUsers(maxUsers);
        
        return repo;
    }
    
    @SuppressWarnings("serial") 
    class LicenceProperties extends Properties
    {
        public LicenceProperties(File licenseFile)
        {
            super();
            setProperty("licence.keystore", "publicCerts.store");
            setProperty("licence.key.alias", "publiccert");
            setProperty("licence.keystore.pwd", "gr33np3pp3r");
            setProperty("licence.cipher.key", "gr33np3pp3r");
            setProperty("licence.subject", "GreenPepper");
        }
    }
    
    public File generateLicence() throws Exception 
    {
        File classFile = new File(DefaultAuthorizerTest.class.getResource("DefaultAuthorizerTest.class").getPath());
        File dir = classFile.getParentFile();
        
        File file = new File(URIUtil.decoded(dir.getPath()) + "/greenpepper.lic");
        License gpLicense = License.commercial("The Using Firm", start, end, 50);
        LicenseManager lm = new LicenseManager(getLicenseParam());
        lm.store(gpLicense, file);
        
        file.deleteOnExit();
        return file;
    }
    
    private LicenseParam getLicenseParam()
    {
        final KeyStoreParam privateKeyStoreParam = new KeyStoreParam() 
        {
            public InputStream getStream() throws IOException {
                final String resourceName = "privateKeys.store";
                final InputStream in = getClass().getResourceAsStream(resourceName);
                if (in == null)
                    throw new FileNotFoundException(resourceName);
                return in;
            }

            public String getAlias() {
                return "privatekey";
            }

            public String getStorePwd() {
                return "gr33np3pp3r";
            }

            public String getKeyPwd() {
                return getStorePwd();
            }
        };


        final CipherParam cipherParam = new CipherParam() {
            public String getKeyPwd() {
                return "gr33np3pp3r";
            }
        };

        final LicenseParam licenseParam = new LicenseParam() {
            public String getSubject() {
                return "GreenPepper";
            }

            public Preferences getPreferences() {
                return Preferences.userNodeForPackage(DefaultAuthorizer.class);
            }

            public KeyStoreParam getKeyStoreParam() {
                return privateKeyStoreParam;
            }

            public CipherParam getCipherParam() {
                return cipherParam;
            }
        };
        
        return licenseParam;
    }

    @SuppressWarnings("deprecation")
    private Date rollYears(Date date, int years)
    {
        Date result = new Date(date.getTime());
        result.setYear(date.getYear() + years);
        return result;
    }
}
