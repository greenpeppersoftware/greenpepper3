package com.greenpepper.server.domain;

import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.REFERENCE_LAST_EXECUTION_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.REFERENCE_REQUIREMENT_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.REFERENCE_SECTIONS_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.REFERENCE_SPECIFICATION_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.REFERENCE_SUT_IDX;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import com.greenpepper.util.StringUtil;

import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * TestCase Class.
 * Main association class between a requirement, a test docuement and a system under test.
 * <p/>
 * Copyright (c) 2006 Pyxis technologies inc. All Rights Reserved.
 * @author JCHUET
 */

@Entity
@Table(name="REFERENCE", uniqueConstraints = {@UniqueConstraint(columnNames={"REQUIREMENT_ID", "SPECIFICATION_ID", "SUT_ID", "SECTIONS"})})
@SuppressWarnings("serial")
public class Reference extends AbstractUniqueEntity implements Comparable
{
    private String sections;
    private Requirement requirement;
    private Specification specification;
    private SystemUnderTest systemUnderTest;
    private Execution lastExecution;

    public static Reference newInstance(Requirement requirement, Specification specification, SystemUnderTest sut)
    {
        return newInstance(requirement, specification, sut, null);
    }

    public static Reference newInstance(Requirement requirement, Specification specification, SystemUnderTest sut, String sections)
    {
        Reference reference = new Reference();
        reference.setSections(sections);

        reference.setRequirement(requirement);
        reference.setSpecification(specification);
        reference.setSystemUnderTest(sut);
        requirement.getReferences().add(reference);
        specification.getReferences().add(reference);

        return reference;
    }

    @Basic
    @Column(name = "SECTIONS", nullable = true, length=50)
    public String getSections()
    {
        return sections;
    }

    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.ALL} )
    @JoinColumn(name="REQUIREMENT_ID")
    public Requirement getRequirement()
    {
        return requirement;
    }

    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="SPECIFICATION_ID")
    public Specification getSpecification()
    {
        return specification;
    }

    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="SUT_ID")
    public SystemUnderTest getSystemUnderTest()
    {
        return systemUnderTest;
    }
    
    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="LAST_EXECUTION_ID")
    public Execution getLastExecution()
    {
        return lastExecution;
    }
    
    public void setSections(String sections)
    {
        this.sections = StringUtil.toNullIfEmpty(sections);
    }

    public void setRequirement(Requirement requirement)
    {
        this.requirement = requirement;
    }

    public void setSpecification(Specification specification)
    {
        this.specification = specification;
    }

    public void setSystemUnderTest(SystemUnderTest systemUnderTest)
    {
        this.systemUnderTest = systemUnderTest;
    }

    public void setLastExecution(Execution lastExecution)
    {
        this.lastExecution = lastExecution;
    }
    
    @Transient
    public String getStatus()
    {
        return lastExecution != null ? lastExecution.getStatus() : Execution.IGNORED; 
    }

    public Vector<Object> marshallize()
    {
        Vector<Object> parameters = new Vector<Object>();        
        parameters.add(REFERENCE_REQUIREMENT_IDX, requirement.marshallize());
        parameters.add(REFERENCE_SPECIFICATION_IDX, specification.marshallize());
        parameters.add(REFERENCE_SUT_IDX, systemUnderTest.marshallize());
        parameters.add(REFERENCE_SECTIONS_IDX, XmlRpcDataMarshaller.padNull(sections));
        
        parameters.add(REFERENCE_LAST_EXECUTION_IDX, lastExecution != null ? lastExecution.marshallize() : Execution.none().marshallize());
        return parameters;
    }
    
    public Execution execute(boolean implementedVersion, String locale)
    {
        return systemUnderTest.execute(specification, implementedVersion, sections, locale);
    }

    public int compareTo(Object o)
    {
        Reference referenceCompared = (Reference)o;
        int compare = specification.compareTo(referenceCompared.specification);
        if(compare != 0)
        {
            return compare;
        }

        compare = requirement.compareTo(referenceCompared.requirement);
        if(compare != 0)
        {
            return compare;
        }

        compare = systemUnderTest.compareTo(referenceCompared.systemUnderTest);
        if(compare != 0)
        {
            return compare;
        }

		return StringUtil.compare(sections, referenceCompared.sections);
	}
    
    public boolean equalsTo(Object o)
    {
        if(o == null || !(o instanceof Reference))
        {
            return false;
        }
        
        Reference refCompared = (Reference)o;
        if(!StringUtil.isEquals(sections, refCompared.sections)) return false;
        if(systemUnderTest == null || !systemUnderTest.equalsTo(refCompared.systemUnderTest)) return false;
        if(requirement == null || !requirement.equalsTo(refCompared.requirement)) return false;
        if(specification == null || !specification.equalsTo(refCompared.specification)) return false;
        
        return true;
    }

    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Reference))
        {
            return false;
        }

        return super.equals(o);
    }
}