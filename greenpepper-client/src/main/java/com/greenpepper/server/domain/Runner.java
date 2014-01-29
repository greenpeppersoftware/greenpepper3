package com.greenpepper.server.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.greenpepper.report.XmlReport;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_CLASSPATH_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_CMDLINE_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_ENVTYPE_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_MAINCLASS_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_NAME_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_SECURED_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_SERVER_NAME_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.RUNNER_SERVER_PORT_IDX;
import com.greenpepper.server.rpc.xmlrpc.client.XmlRpcClientExecutor;
import com.greenpepper.server.rpc.xmlrpc.client.XmlRpcClientExecutorFactory;
import com.greenpepper.util.CollectionUtil;
import com.greenpepper.util.ExceptionUtils;
import com.greenpepper.util.IOUtil;
import static com.greenpepper.util.IOUtils.uniquePath;
import com.greenpepper.util.StringUtil;
import com.greenpepper.util.URIUtil;
import com.greenpepper.util.cmdline.CommandLineBuilder;
import com.greenpepper.util.cmdline.CommandLineExecutor;

/**
 * Runner Class.
 * Definition of a Runner.
 * <p/>
 * Copyright (c) 2006-2007 Pyxis technologies inc. All Rights Reserved.
 * @author JCHUET
 */

@Entity
@Table(name="RUNNER")
@SuppressWarnings("serial")
public class Runner extends AbstractVersionedEntity implements Comparable
{
	private static final String AGENT_HANDLER = "greenpepper-agent1";
    private String name;
    private String cmdLineTemplate;
    private String mainClass;
    private EnvironmentType envType;
    
    private String serverName;
    private String serverPort;
    private Boolean secured;
    
    private SortedSet<String> classpaths = new TreeSet<String>();
    
    public static Runner newInstance(String name)
    {
        Runner runner = new Runner();
        runner.setName(name);
        
        return runner;
    }

    @Basic
    @Column(name = "NAME", unique = true, nullable = false, length=255)
    public String getName()
    {
        return name;
    }

    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="ENVIRONMENT_TYPE_ID")
    public EnvironmentType getEnvironmentType()
    {
        return envType;
    }

    @Basic
    @Column(name = "SERVER_NAME", nullable = true, length=255)
    public String getServerName()
    {
        return serverName;
    }
    
    @Basic
    @Column(name = "SERVER_PORT", nullable = true, length=8)
    public String getServerPort()
    {
        return serverPort;
    }

    @Basic
    @Column(name = "SECURED", nullable = true)
    public boolean isSecured()
    {
    	return secured != null && secured.booleanValue();
    }
    
    @Basic
    @Column(name = "CMD_LINE_TEMPLATE", nullable = true, length=510)
    public String getCmdLineTemplate()
    {
        return cmdLineTemplate;
    }

    @Basic
    @Column(name = "MAIN_CLASS", nullable = true, length=255)
    public String getMainClass()
    {
        return mainClass;
    }

    @CollectionOfElements
	@JoinTable( name="RUNNER_CLASSPATHS", joinColumns={@JoinColumn(name="RUNNER_ID")} )
	@Column(name = "elt", nullable = true, length=255)
	@Sort(type = SortType.COMPARATOR, comparator = ClasspathComparator.class)
	public SortedSet<String> getClasspaths()
    {
        return classpaths;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEnvironmentType(EnvironmentType envType)
    {
        this.envType = envType;
    }

    public void setServerName(String serverName)
    {
        this.serverName = StringUtil.toNullIfEmpty(serverName);
    }

    public void setServerPort(String serverPort)
    {
        this.serverPort = StringUtil.toNullIfEmpty(serverPort);
    }
    
    public void setSecured(Boolean secured)
    {
        this.secured = secured != null && secured.booleanValue();
    }

    public void setCmdLineTemplate(String cmdLineTemplate)
    {
        this.cmdLineTemplate = StringUtil.toNullIfEmpty(cmdLineTemplate);
    }
    
    public void setMainClass(String mainClass)
    {
        this.mainClass = StringUtil.toNullIfEmpty(mainClass);
    }

    public void setClasspaths(SortedSet<String> classpaths)
    {
        this.classpaths = classpaths;
    }

    public Execution execute(Specification specification, SystemUnderTest systemUnderTest, boolean implementedVersion, String sections, String locale)
    {
    	if(isRemote())
    	{
    		return executeRemotely(specification, systemUnderTest, implementedVersion, sections, locale);
    	}
    	else
    	{
    		return executeLocally(specification, systemUnderTest, implementedVersion, sections, locale);
    	}
    }
    
    @SuppressWarnings("unchecked")
	private Execution executeRemotely(Specification specification, SystemUnderTest systemUnderTest, boolean implementedVersion, String sections, String locale)
    {
        try
        {
        	sections = (String)XmlRpcDataMarshaller.padNull(sections);
        	locale = (String)XmlRpcDataMarshaller.padNull(locale);

	        XmlRpcClientExecutor xmlrpc = XmlRpcClientExecutorFactory.newExecutor(agentUrl());

	        Vector params = CollectionUtil.toVector(marshallize(), systemUnderTest.marshallize(), specification.marshallize(), implementedVersion, sections, locale);
	        Vector<Object> execParams = (Vector<Object>)xmlrpc.execute(AGENT_HANDLER+".execute", params);
	        
			Execution execution = XmlRpcDataMarshaller.toExecution(execParams);
	        execution.setSystemUnderTest(systemUnderTest);
	        execution.setSpecification(specification);
			execution.setRemotelyExecuted();
			return execution;
        }
        catch (Exception e)
        {
            return Execution.error(specification, systemUnderTest, sections, ExceptionUtils.stackTrace(e, "<br>", 15));
        }
    }
    
    private Execution executeLocally(Specification specification, SystemUnderTest systemUnderTest, boolean implementedVersion, String sections, String locale)
    {
        File outputFile = null;
        
        try
        {
            String outpuPath = uniquePath("GreenPepperTest", ".tst");
			outputFile = new File(outpuPath);

            String[] cmdLine = compileCmdLine(specification, systemUnderTest, outpuPath, implementedVersion, sections, locale);
            new CommandLineExecutor(cmdLine).executeAndWait();

            return Execution.newInstance(specification, systemUnderTest, XmlReport.parse(outputFile));
        }
        catch (GreenPepperServerException e)
        {
            return Execution.error(specification, systemUnderTest, sections, e.getId());
        }
        catch (Exception e)
        {
            return Execution.error(specification, systemUnderTest, sections, ExceptionUtils.stackTrace(e, "<br>", 15));
        }
        finally
        {
			IOUtil.deleteFile(outputFile);
        }
    }

	public Vector<Object> marshallize()
    {
        Vector<Object> parameters = new Vector<Object>();
        parameters.add(RUNNER_NAME_IDX, name);
        parameters.add(RUNNER_CMDLINE_IDX, XmlRpcDataMarshaller.padNull(cmdLineTemplate));
        parameters.add(RUNNER_ENVTYPE_IDX, envType != null ? envType.marshallize() : EnvironmentType.newInstance("").marshallize());
        parameters.add(RUNNER_SERVER_NAME_IDX, XmlRpcDataMarshaller.padNull(serverName));
        parameters.add(RUNNER_SERVER_PORT_IDX, XmlRpcDataMarshaller.padNull(serverPort));
        parameters.add(RUNNER_MAINCLASS_IDX, XmlRpcDataMarshaller.padNull(mainClass));
        parameters.add(RUNNER_CLASSPATH_IDX, new Vector<String>(classpaths));
        parameters.add(RUNNER_SECURED_IDX, isSecured());
        return parameters;
    }

    public String agentUrl() 
	{
		return ( isSecured() ? "https://" : "http://" ) + serverName + ":" + serverPort;
	}
    
    public int compareTo(Object o)
    {
        return this.getName().compareTo(((Runner)o).getName());
    }

    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Runner))
        {
            return false;
        }

        Runner runnerCompared = (Runner)o;
		return getName().equals(runnerCompared.getName());
	}

    public int hashCode()
    {
        return getName().hashCode();
    }
    
    private String[] compileCmdLine(Specification spec, SystemUnderTest sut, String outpuPath, boolean implementedVersion, String sections, String locale) throws Exception
    {
        CommandLineBuilder cmdBuilder = new CommandLineBuilder(cmdLineTemplate);
        cmdBuilder.setDependencies(mergedDependencies(sut));
        cmdBuilder.setMainClass(mainClass);
        cmdBuilder.setInputPath(URIUtil.raw(spec.getName()) + (implementedVersion ? "" : "?implemented=false"));
        cmdBuilder.setOutpuPath(outpuPath);
        cmdBuilder.setRepository(spec.getRepository().asCmdLineOption(envType));
        cmdBuilder.setFixtureFactory(sut.fixtureFactoryCmdLineOption());
		cmdBuilder.setProjectDependencyDescriptor(sut.getProjectDependencyDescriptor());
		cmdBuilder.setSections(sections);
        cmdBuilder.setLocale(locale);
        
        return cmdBuilder.getCmdLine();
    }

    private Collection<String> mergedDependencies(SystemUnderTest systemUnderTest)
    {
        Collection<String> dependencies = new ArrayList<String>();
        dependencies.addAll(getClasspaths());
        dependencies.addAll(systemUnderTest.getFixtureClasspaths());
        dependencies.addAll(systemUnderTest.getSutClasspaths());
        return dependencies;
    }
    
    @Transient
    private boolean isRemote()
    {
    	return !StringUtil.isEmpty(serverName) && !StringUtil.isEmpty(serverPort);
    }
}
