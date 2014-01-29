package com.greenpepper.server.license;

import java.util.Vector;

import com.greenpepper.server.domain.Marshalizable;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_TYPE_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_EXPIRY_DATE_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_SUPPORT_EXPIRY_DATE_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_MAX_USERS_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_EXTRA_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_INFO_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_VERSION_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_HOLDER_NAME_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.LICENSE_EFFECTIVE_DATE_IDX;
import com.greenpepper.util.StringUtil;

public class LicenseBean implements Marshalizable
{
    private String licenseType;
	private String notBefore;
	private String noSupportAfter;
    private String notAfter;
    private String version;
    private int maxUsers;
    private String holderName;
    private String info;

	public String getLicenseType()
    {
        return licenseType;
    }
    
    public void setLicenseType(String licenseType)
    {
        this.licenseType = licenseType;
    }

	public boolean hasMaxUsers()
	{
		return maxUsers > 0;
	}
	
	public int getMaxUsers()
    {
        return maxUsers;
    }
    
    public void setMaxUsers(int maxUsers)
    {
        this.maxUsers = maxUsers;
    }

	public String getNotBefore()
	{
		return notBefore;
	}

	public void setNotBefore(String notBefore)
	{
		this.notBefore = notBefore;
	}

	public String getNotAfter()
    {
        return notAfter;
    }
    
    public void setNotAfter(String notAfter)
    {
        this.notAfter = notAfter;
    }

    public String getNoSupportAfter()
    {
        return noSupportAfter;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public void setNoSupportAfter(String noSupportAfter)
    {
        this.noSupportAfter = noSupportAfter;
    }
    
    public boolean hasLicenseType()
    {
        return !StringUtil.isEmpty(licenseType);
    }
    
    public boolean hasNotAfter()
    {
        return !StringUtil.isEmpty(notAfter);
    }
    
    public boolean hasNoSupportAfter()
    {
        return !StringUtil.isEmpty(noSupportAfter);
    }

	public boolean hasNotBefore()
	{
		return !StringUtil.isEmpty(notBefore);
	}

	public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public boolean hasHolderName()
    {
        return !StringUtil.isEmpty(holderName);
    }
    
	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public Vector<Object> marshallize()
    {
        Vector<Object> parameters = new Vector<Object>();
        parameters.add(LICENSE_TYPE_IDX, licenseType);
        parameters.add(LICENSE_EXPIRY_DATE_IDX, notAfter);
        parameters.add(LICENSE_SUPPORT_EXPIRY_DATE_IDX, noSupportAfter);
        parameters.add(LICENSE_MAX_USERS_IDX, maxUsers);
        parameters.add(LICENSE_INFO_IDX, info);
        parameters.add(LICENSE_VERSION_IDX, version);
        parameters.add(LICENSE_EXTRA_IDX, 0);
        parameters.add(LICENSE_HOLDER_NAME_IDX, holderName);
		parameters.add(LICENSE_EFFECTIVE_DATE_IDX, notBefore);
		return parameters;
    }
}