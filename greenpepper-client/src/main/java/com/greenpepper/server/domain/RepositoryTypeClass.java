package com.greenpepper.server.domain;

import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="REPOSITORY_TYPE_CLASS", uniqueConstraints = {@UniqueConstraint(columnNames={"ENVIRONMENT_TYPE_ID", "REPOSITORY_TYPE_ID"})})
@SuppressWarnings("serial")
public class RepositoryTypeClass extends AbstractEntity
{
	private String className;
	private EnvironmentType envType;
	private RepositoryType repositoryType;

	public static RepositoryTypeClass newInstance(RepositoryType repositoryType, EnvironmentType envType, String className)
	{
		RepositoryTypeClass repoTypeClass = new RepositoryTypeClass();
		repoTypeClass.setRepositoryType(repositoryType);
		repoTypeClass.setEnvType(envType);
		repoTypeClass.setClassName(className);
		
		return repoTypeClass;
	}
	
    @Basic
    @Column(name = "CLASSNAME", nullable = false, length=255)
	public String getClassName() 
	{
		return className;
	}
	
    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="ENVIRONMENT_TYPE_ID", nullable = false)
	public EnvironmentType getEnvType() 
	{
		return envType;
	}
	
    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="REPOSITORY_TYPE_ID", nullable = false)
	public RepositoryType getRepositoryType() 
	{
		return repositoryType;
	}
	
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	public void setEnvType(EnvironmentType envType)
	{
		this.envType = envType;
	}
	
	public void setRepositoryType(RepositoryType repositoryType) 
	{
		this.repositoryType = repositoryType;
	}

	public Vector<Object> marshallize() 
	{
		return null;
	}
    
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof RepositoryTypeClass))
        {
            return false;
        }
        
        RepositoryTypeClass typeCompared = (RepositoryTypeClass)o;
        if(getClassName().equals(typeCompared.getClassName()) &&
    	   getEnvType().equals(typeCompared.getEnvType()) &&
    	   getRepositoryType().equals(typeCompared.getRepositoryType()))
        {
            return true;
        }
        
        return false;
    }
    
    public int hashCode()
    {
        return getClassName().hashCode() << 1 + getEnvType().hashCode() << 1 + getRepositoryType().hashCode() << 1;
    }
}
