package com.greenpepper.server.domain;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_CLASSPATH_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_FIXTURE_CLASSPATH_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_FIXTURE_FACTORY_ARGS_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_FIXTURE_FACTORY_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_IS_DEFAULT_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_NAME_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_PROJECT_DEPENDENCY_DESCRIPTOR_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_PROJECT_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.SUT_RUNNER_IDX;
import com.greenpepper.systemunderdevelopment.DefaultSystemUnderDevelopment;
import com.greenpepper.util.StringUtil;

/**
 * SystemUnderTest Class.
 * <p/>
 * Copyright (c) 2006 Pyxis technologies inc. All Rights Reserved.
 * @author JCHUET
 */

@Entity
@Table(name="SUT", uniqueConstraints = {@UniqueConstraint(columnNames={"NAME", "PROJECT_ID"})})
@SuppressWarnings("serial")
public class SystemUnderTest extends AbstractUniqueEntity implements Comparable
{
	private static final transient String DEFAULT_JAVA_FIXTURE_FACTORY = DefaultSystemUnderDevelopment.class.getName();
	private static final transient String DEFAULT_DOTNET_FIXTURE_FACTORY = "GreenPepper.Fixtures.PlainOldSystemUnderDevelopment";
	
	private String name;
    private Project project;
    private Runner runner;
	private SortedSet<String> sutClasspaths = new TreeSet<String>();
	private SortedSet<String> fixtureClasspaths = new TreeSet<String>();

    private String fixtureFactory;
    private String fixtureFactoryArgs;

    private byte selected = 0;

	private String projectDependencyDescriptor;

	public static SystemUnderTest newInstance(String name)
    {
        SystemUnderTest sut = new SystemUnderTest();
        sut.setName(name);
        return sut;
    }

    @Basic
    @Column(name = "NAME", nullable = false, length=255)
    public String getName()
    {
        return name;
    }

    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="RUNNER_ID")
    public Runner getRunner()
    {
        return runner;
    }

    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="PROJECT_ID")
    public Project getProject()
    {
        return project;
    }

    @CollectionOfElements
	@JoinTable( name="SUT_FIXTURE_CLASSPATHS", joinColumns={@JoinColumn(name="SUT_ID")} )
	@Column(name = "elt", nullable = true, length=255)
	@Sort(type = SortType.COMPARATOR, comparator = ClasspathComparator.class)
	public SortedSet<String> getFixtureClasspaths()
    {
        return fixtureClasspaths;
    }

    @CollectionOfElements
	@JoinTable( name="SUT_CLASSPATHS", joinColumns={@JoinColumn(name="SUT_ID")} )
	@Column(name = "elt", nullable = true, length=255)
	@Sort(type = SortType.COMPARATOR, comparator = ClasspathComparator.class)
	public SortedSet<String> getSutClasspaths()
    {
        return sutClasspaths;
    }

    @Basic
    @Column(name = "FIXTURE_FACTORY", nullable = true, length=255)
    public String getFixtureFactory()
    {
        return fixtureFactory;
    }

    @Basic
    @Column(name = "FIXTURE_FACTORY_ARGS", nullable = true, length=255)
    public String getFixtureFactoryArgs()
    {
        return fixtureFactoryArgs;
    }

    @Basic
    @Column(name = "SELECTED")
    public byte getSelected()
    {
        return selected;
    }

	@Basic
	@Column(name = "PROJECT_DEPENDENCY_DESCRIPTOR", nullable = true, length=255)
	public String getProjectDependencyDescriptor()
	{
		return projectDependencyDescriptor;
	}

	public void setName(String name)
    {
        this.name = name;
    }

    public void setRunner(Runner runner)
    {
        this.runner = runner;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    public void setFixtureClasspaths(SortedSet<String> fixturesClasspaths)
    {
        this.fixtureClasspaths = fixturesClasspaths;
    }

    public void setSutClasspaths(SortedSet<String> sutClasspaths)
    {
        this.sutClasspaths = sutClasspaths;
    }

    public void setFixtureFactory(String fixtureFactory)
    {
        this.fixtureFactory = fixtureFactory;
    }

    public void setFixtureFactoryArgs(String fixtureFactoryArgs)
    {
        this.fixtureFactoryArgs = fixtureFactoryArgs;
    }

    public void setSelected(byte selected)
    {
        this.selected = selected;
    }

	public void setProjectDependencyDescriptor(String projectDependencyDescriptor)
	{
		this.projectDependencyDescriptor = projectDependencyDescriptor;
	}

	@Transient
    public boolean isDefault()
    {
        return selected == (byte)1;
    }

    public void setIsDefault(boolean isSelected)
    {
        this.selected = isSelected ? (byte)1 : (byte)0;
    }

    public Execution execute(Specification specification, boolean implementedVersion, String sections, String locale)
    {
        return runner.execute(specification, this, implementedVersion, sections, locale);
    }

    public String fixtureFactoryCmdLineOption()
    {
        if(StringUtil.isEmpty(fixtureFactory)) return getDefaultFixtureFactory();
		if(StringUtil.isEmpty(fixtureFactoryArgs)) return fixtureFactory;
        return fixtureFactory + ";" + fixtureFactoryArgs;
    }

	@Transient
	private String getDefaultFixtureFactory()
	{
		if (runner == null || runner.getEnvironmentType().getName().equals("JAVA"))
		{
			return DEFAULT_JAVA_FIXTURE_FACTORY;
		}
		else
		{
			return DEFAULT_DOTNET_FIXTURE_FACTORY;
		}
	}

	public Vector<Object> marshallize()
    {
        Vector<Object> parameters = new Vector<Object>();
        parameters.add(SUT_NAME_IDX, name);
        parameters.add(SUT_PROJECT_IDX, project.marshallize());
        parameters.add(SUT_CLASSPATH_IDX, new Vector<String>(sutClasspaths));
        parameters.add(SUT_FIXTURE_CLASSPATH_IDX, new Vector<String>(fixtureClasspaths));
        parameters.add(SUT_FIXTURE_FACTORY_IDX, XmlRpcDataMarshaller.padNull(fixtureFactory));
        parameters.add(SUT_FIXTURE_FACTORY_ARGS_IDX, XmlRpcDataMarshaller.padNull(fixtureFactoryArgs));
        parameters.add(SUT_IS_DEFAULT_IDX, isDefault());
        parameters.add(SUT_RUNNER_IDX, runner != null ? runner.marshallize() : Runner.newInstance("N/A").marshallize());
		parameters.add(SUT_PROJECT_DEPENDENCY_DESCRIPTOR_IDX, XmlRpcDataMarshaller.padNull(projectDependencyDescriptor));
		return parameters;
    }
    
    public int compareTo(Object o)
    {
        if(isDefault()) return -1;
        if(((SystemUnderTest)o).isDefault()) return 1;
        return name.compareTo(((SystemUnderTest)o).name);
    }
    
    public boolean equalsTo(Object o)
    {
        if(o == null || !(o instanceof SystemUnderTest))
        {
            return false;
        }
        
        SystemUnderTest sutCompared = (SystemUnderTest)o;
        if(name == null || !name.equals(sutCompared.getName())) return false;
        if(project == null || !project.equals(sutCompared.getProject())) return false;
        
        return true;
    }

    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof SystemUnderTest))
        {
            return false;
        }

        return super.equals(o);
    }
}