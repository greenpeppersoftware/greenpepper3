package com.greenpepper.server.domain;

import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name="SYSTEM_INFO")
@SuppressWarnings("serial")
public class SystemInfo extends AbstractUniqueEntity
{
    private String license;
    private String gpVersion; 

    @Lob
    @Column(name = "LICENSE", nullable = true, length = 4096)
    public String getLicense()
    {
        return license;
    }

    @Basic
    @Column(name = "GPVERSION")
    public String getGpVersion()
    {
    	return gpVersion;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }
    
    public void setGpVersion(String gpVersion)
    {
        this.gpVersion = gpVersion;
    }

    public Vector<Object> marshallize()
    {
    	return new Vector<Object>();
    }

    public boolean equals(Object o)
    {
        if (super.equals(o))
        {
            return o instanceof SystemInfo;
        }

        return false;
    }
}
