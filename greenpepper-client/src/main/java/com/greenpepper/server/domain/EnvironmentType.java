package com.greenpepper.server.domain;

import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.ENVTYPE_NAME_IDX;

import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;

@Entity
@Table(name="ENVIRONMENT_TYPE")
@SuppressWarnings("serial")
public class EnvironmentType extends AbstractEntity implements Comparable<EnvironmentType>
{
	private String name;
	
    public static EnvironmentType newInstance(String name)
    {
    	EnvironmentType env = new EnvironmentType();
    	env.setName(name);
        return env;
    }

    @Basic
    @Column(name = "NAME", unique = true, nullable = false, length=255)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

	public Vector<Object> marshallize()
	{
        Vector<Object> parameters = new Vector<Object>();
        parameters.add(ENVTYPE_NAME_IDX, XmlRpcDataMarshaller.padNull(name));
		return parameters;
	}

    public int compareTo(EnvironmentType envCompared)
    {
        return this.getName().compareTo(envCompared.getName());
    }

    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof EnvironmentType))
        {
            return false;
        }

        EnvironmentType envCompared = (EnvironmentType)o;
        return getName().equals(envCompared.getName());
    }

    public int hashCode()
    {
		return getName() == null ? 0 : getName().hashCode();
    }
}
