package com.greenpepper.server.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.TreeSet;
import java.util.SortedSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SPECIFICATION_SUTS_IDX;

/**
 * Specification Class.
 * <p/>
 * Copyright (c) 2006 Pyxis technologies inc. All Rights Reserved.
 * @author JCHUET
 */

@Entity
@Table(name="SPECIFICATION", uniqueConstraints = {@UniqueConstraint(columnNames={"NAME", "REPOSITORY_ID"})})
@SuppressWarnings("serial")
public class Specification extends Document
{
    private SortedSet<SystemUnderTest> targetedSystemUnderTests = new TreeSet<SystemUnderTest>();
    protected Set<Reference> references = new HashSet<Reference>();
    private Set<Execution> executions = new HashSet<Execution>();

    public static Specification newInstance(String name)
    {
        Specification specification = new Specification();
        specification.setName(name);
        return specification;
    }
    
    @ManyToMany( targetEntity= SystemUnderTest.class, cascade={CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinTable( name="SUT_SPECIFICATION", joinColumns={@JoinColumn(name="SPECIFICATION_ID")}, inverseJoinColumns={@JoinColumn(name="SUT_ID")} )
	@Sort(type=SortType.COMPARATOR, comparator=SystemUnderTestByNameComparator.class)
    public SortedSet<SystemUnderTest> getTargetedSystemUnderTests()
    {
        return targetedSystemUnderTests;
    }

    @OneToMany(mappedBy="specification", cascade=CascadeType.ALL)
    public Set<Execution> getExecutions()
    {
        return this.executions;
    }

    @OneToMany(mappedBy="specification", cascade=CascadeType.ALL)
    public Set<Reference> getReferences()
    {
        return references;
    }

    public void setTargetedSystemUnderTests(SortedSet<SystemUnderTest> targetedSystemUnderTests)
    {
		this.targetedSystemUnderTests = targetedSystemUnderTests;
    }

    public void setExecutions(Set<Execution> executions)
    {
        this.executions = executions;
    }
    
    public void setReferences(Set<Reference> references)
    {
        this.references = references;
    }

    public void addSystemUnderTest(SystemUnderTest systemUnderTest)
    {
        targetedSystemUnderTests.add(systemUnderTest);
    }

    public void addExecution(Execution execution)
    {
        execution.setSpecification(this);
        executions.add(execution);
    }

    public void removeSystemUnderTest(SystemUnderTest systemUnderTest)
    {
        targetedSystemUnderTests.remove(systemUnderTest);
        if(targetedSystemUnderTests.isEmpty())
            addSystemUnderTest(getRepository().getProject().getDefaultSystemUnderTest());
    }

    public void removeReference(Reference reference) throws GreenPepperServerException
    {
        if(!references.contains(reference))
        {
            throw new GreenPepperServerException( GreenPepperServerErrorKey.REFERENCE_NOT_FOUND, "Reference not found");
        }

        references.remove(reference);
        reference.setSpecification(null);
    }

    public Vector<Object> marshallize()
    {
        Vector<Object> parameters = super.marshallize();
        Vector<Object> suts = XmlRpcDataMarshaller.toXmlRpcSystemUnderTestsParameters(targetedSystemUnderTests);
        parameters.add(SPECIFICATION_SUTS_IDX, suts);
        return parameters;
    }

    public boolean equals(Object o)
    {
		return super.equals(o) && o instanceof Specification;
	}
}
