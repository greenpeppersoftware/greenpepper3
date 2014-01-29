package com.greenpepper.server.license;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.prefs.Preferences;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.greenpepper.server.license.License;
import com.greenpepper.util.FormatedDate;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

public class LicenceGenerator
{
    public static boolean deleteFiles = true;
    @SuppressWarnings("deprecation")
    private static Date _2006 = new Date(106, 0, 0);
    @SuppressWarnings("deprecation")
    private static Date _2006_12 = new Date(106, 8, 0);
    @SuppressWarnings("deprecation")
    private static Date _2007 = new Date(107, 0, 0);
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        buildOpenSource();
        buildAcademic();
        buildEvaluation(_2007);
        buildEvaluation(_2006_12);
        buildCommercial(50, _2007);
        buildCommercial(2, _2007);
        buildCommercial(1, _2007);
    }
    
    private static void buildOpenSource() throws Exception
    {
        File file = File.createTempFile("opensource", ".lic");
        License license = License.openSource("My Open Source Project", _2006, _2006);
        LicenseManager lm = new LicenseManager(getLicenseParam());
        lm.store(license, file);
        if(deleteFiles)file.deleteOnExit();
        System.out.println("# Open source");
        System.out.println(new String(Base64.encodeBase64(FileUtils.readFileToByteArray(file))));
        System.out.println("");
    }
    
    private static void buildAcademic() throws Exception
    {
        File file = File.createTempFile("academic", ".lic");
        License license = License.academic("My School", _2006, _2006);
        LicenseManager lm = new LicenseManager(getLicenseParam());
        lm.store(license, file);
        if(deleteFiles)file.deleteOnExit();
        System.out.println("# Academic");
        System.out.println(new String(Base64.encodeBase64(FileUtils.readFileToByteArray(file))));
        System.out.println("");
    }
    
    private static void buildEvaluation(Date experyDate) throws Exception
    {
        File file = File.createTempFile("evaluation", ".lic");
        License license = License.evaluation("Some peeps evaluating", _2006, experyDate);
        LicenseManager lm = new LicenseManager(getLicenseParam());
        lm.store(license, file);
        if(deleteFiles)file.deleteOnExit();
        System.out.println("# Evaluation Expery: " + new FormatedDate(experyDate).getFormatedDate());
        System.out.println(new String(Base64.encodeBase64(FileUtils.readFileToByteArray(file))));
        System.out.println("");
    }
    
    private static void buildCommercial(int users, Date supportDate) throws Exception
    {
        File file = File.createTempFile("commercial", ".lic");
        License license = License.commercial("My Paying Company", _2006, supportDate, users);
        LicenseManager lm = new LicenseManager(getLicenseParam());
        lm.store(license, file);
        if(deleteFiles)file.deleteOnExit();
        System.out.println("# Commercial " + users + " USERS - Expery: " + new FormatedDate(supportDate).getFormatedDate());
        System.out.println(new String(Base64.encodeBase64(FileUtils.readFileToByteArray(file))));
        System.out.println("");
    }

    private static LicenseParam getLicenseParam()
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
                return Preferences.userNodeForPackage(LicenceGenerator.class);
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
}
