package com.greenpepper.server.license;

import java.util.Date;
import javax.security.auth.x500.X500Principal;

import com.greenpepper.server.GreenPepperServer;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.util.Period;
import de.schlichtherle.license.LicenseContent;

@SuppressWarnings("serial")
public class License extends LicenseContent
{
    private Date noSupportAfter;
    private String licenseType;
    private int maxUsers;
    
    public static License openSource(String holderName, Date effectiveDate, Date supportExpiry)
    {
        License license = licenseTemplate(holderName, 0, effectiveDate);
        license.setLicenseType(LicenseType.OPEN_SOURCE);
        license.setNoSupportAfter(supportExpiry);
        license.setInfo("greenpepper.server.license.opensourcedesc");
        return license;
    }

    public static License academic(String holderName, Date effectiveDate, Date supportExpiry)
    {
        License license = licenseTemplate(holderName, 0, effectiveDate);
        license.setLicenseType(LicenseType.ACADEMIC);
        license.setNoSupportAfter(supportExpiry);
        license.setInfo("greenpepper.server.license.academicdesc");
        return license;
    }

    public static License evaluation(String holderName, Date effectiveDate, Date expiryDate)
    {
        License license = licenseTemplate(holderName, 0, effectiveDate);
        license.setLicenseType(LicenseType.EVALUATION);
        license.setInfo("greenpepper.server.license.evaluationdesc");
        license.setNotAfter(expiryDate);
        
        return license;
    }

    public static License evaluationExtention(String holderName, Date effectiveDate, Date expiryDate)
    {
        License license = licenseTemplate(holderName, 0, effectiveDate);
        license.setLicenseType(LicenseType.EVALUATION_EXT);
        license.setInfo("greenpepper.server.license.evaluationdesc");
        license.setNotAfter(expiryDate);
        
        return license;
    }

    public static License invalid(String holderName)
    {
        License license = licenseTemplate(holderName, 0, null);
        license.setLicenseType(LicenseType.INVALID);
        license.setInfo("greenpepper.server.license.invaliddesc");
        
        return license;
    }

    public static License commercial(String holderName, Date effectiveDate, Date supportExpiry, int gpUsers)
    {
        License license = licenseTemplate(holderName, gpUsers, effectiveDate);
        license.setLicenseType(LicenseType.COMMERCIAL);
        license.setInfo("greenpepper.server.license.commercialdesc");
        license.setNoSupportAfter(supportExpiry);
        
        return license;
    }

    public void verify(Repository repository, SecurityContext securityContext, Permission permission)
        throws GreenPepperLicenceException
    {
    	verify(GreenPepperServer.versionDate());
        if(getLicenseType().equals(LicenseType.COMMERCIAL))
        {
			if(securityContext.isMaxReached(repository, getGPMaxUsers()))
            {
                securityContext.denyAccess(repository);
                throw new GreenPepperLicenceException(LicenseErrorKey.MAX_USER_EXCEEDS, "Max users exceeded");
            }

            securityContext.grantAccess(repository);
        }
    }

    public void verify(Date versionDate) throws GreenPepperLicenceException
    {
        if(getLicenseType().equals(LicenseType.INVALID))
            throw new GreenPepperLicenceException(LicenseErrorKey.UPDATES_NOT_SUPPORTED, "Updates not supported");
        
        if(isExpiredOn(now()))
        {
            setExtra(LicenseErrorKey.EXPIRED);
            throw new GreenPepperLicenceException(LicenseErrorKey.EXPIRED, "Expired");
        }
        
        if(noSupportAfter != null)
        {
            if(isSupportExpiredOn(versionDate))
            {
                setExtra(LicenseErrorKey.UPDATES_NOT_SUPPORTED);
                throw new GreenPepperLicenceException(LicenseErrorKey.UPDATES_NOT_SUPPORTED, "Updates not supported");
            }
        }
    }

    public String getWarning()
    {
        return (String)getExtra();
    }

	private int getGPMaxUsers()
	{
		// version < 2.0 license was using the extra as the GPusers, if extra is empty >= version 2.0
		
		if (getExtra() == null)
		{
			return getMaxUsers();
		}

		return (Integer)getExtra();
	}

	private boolean isExpiredOn(Date when)
    {
        return !getValidityPeriod().includes(when);
    }

    private boolean isSupportExpiredOn(Date when)
    {
        return !getSupportPeriod().beforeEnd(when);
    }
    
    public Period getValidityPeriod()
    {
        return Period.fromTo(getNotBefore(), getNotAfter());
    }
    
    public Period getSupportPeriod()
    {
        return Period.fromTo(getNotBefore(), noSupportAfter);
    }
    
    public String getLicenseType()
    {
        return licenseType;
    }
    
    public void setLicenseType(String licenseType)
    {
        this.licenseType = licenseType;
    }

    public int getMaxUsers()
    {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers)
    {
        this.maxUsers = maxUsers;
    }

    public Date getNoSupportAfter()
    {
        return noSupportAfter;
    }

    public void setNoSupportAfter(Date noSupportAfter)
    {
        this.noSupportAfter = noSupportAfter;
    }

	public String getHolderName() {
		return fromCommonName(getHolder().getName());
	}

    private static License licenseTemplate(String holderName, int maxUsers, Date effectiveDate)
    {
        License license = new License();
        license.setHolder(new X500Principal(commonName(holderName)));
        license.setIssuer(licenseIssuer());
        license.setNotBefore(effectiveDate);
        license.setSubject("GreenPepper");
        license.setConsumerType("user");
        license.setConsumerAmount(1);
        license.setIssued(effectiveDate);
		license.setMaxUsers(maxUsers);
		license.setExtra(null);
		return license;
    }

    private static String commonName(String holderName)
    {
        return "CN=" + holderName;
    }

    private static String fromCommonName(String commonName)
    {
    	// Remove the CN= part
        return commonName.substring(3);
    }
    
    private static X500Principal licenseIssuer()
    {
        return new X500Principal("CN=GreenPepper, L=Laval, ST=Quebec, O=Pyxis Technologies Inc.,"
                + " OU=GreenPepper Software," + " C=Canada," + " STREET=120 Armand Frappier #120, "
                + " DC=H7V 4B4 UID=GreenPepper");
    }
    
    private Date now() { return new Date(); }
}