package com.greenpepper.server.domain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Vector;

import com.greenpepper.repository.FileSystemRepository;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import com.greenpepper.util.StringUtil;
import com.greenpepper.util.TestCase;
import com.greenpepper.util.URIUtil;

public class RunnerTest extends TestCase
{
    private static String EN = Locale.ENGLISH.getLanguage();
    private File input;
    
    public void setUp() throws URISyntaxException
    {
        input = new File(RunnerTest.class.getResource("test.html").getPath());
    }
    
    public void testThatRunnerIsProperlyMarshalized()
    {
        Runner runner = Runner.newInstance("RUNNER-1");
        runner.setServerName("SERVER_NAME");
        runner.setServerPort("SERVER_PORT");
        runner.setMainClass("MAINCLASS");
        runner.setCmdLineTemplate("CMD_TEMPLATE");
        runner.setEnvironmentType(EnvironmentType.newInstance("ENVTYPE-1"));
        ClasspathSet classPaths = new ClasspathSet();
        classPaths.add("CLASSPATH-1");
        classPaths.add("CLASSPATH-2");
        runner.setClasspaths(classPaths);
        runner.setSecured(true);
        
        Vector<Object> params = new Vector<Object>();
        params.add(XmlRpcDataMarshaller.RUNNER_NAME_IDX, "RUNNER-1");
        params.add(XmlRpcDataMarshaller.RUNNER_CMDLINE_IDX, "CMD_TEMPLATE");
        Vector<Object> envType = new Vector<Object>();
        envType.add(0, "ENVTYPE-1");
        params.add(XmlRpcDataMarshaller.RUNNER_ENVTYPE_IDX, envType);
        params.add(XmlRpcDataMarshaller.RUNNER_SERVER_NAME_IDX, "SERVER_NAME");
        params.add(XmlRpcDataMarshaller.RUNNER_SERVER_PORT_IDX, "SERVER_PORT");
        params.add(XmlRpcDataMarshaller.RUNNER_MAINCLASS_IDX, "MAINCLASS");
        Vector<Object> cp = new Vector<Object>();
        cp.add(0, "CLASSPATH-1");
        cp.add(1, "CLASSPATH-2");
        params.add(XmlRpcDataMarshaller.RUNNER_CLASSPATH_IDX, cp);
        params.add(XmlRpcDataMarshaller.RUNNER_SECURED_IDX, true);
        
        assertEquals(params, runner.marshallize());
    }
    
    public void testThatWeCanExecuteASpecificationForAGivenSut()
    {
        Runner runner = Runner.newInstance("RUNNER");
        runner.setMainClass(FakeRunner.class.getName());
        runner.setCmdLineTemplate("java -cp ${classpaths} ${mainClass} ${inputPath} ${outputPath} --xml");
        ClasspathSet classPaths = new ClasspathSet();
        classPaths.add(getRootPath());
        runner.setClasspaths(classPaths);
        
        Execution exe = runner.execute(getSpecification(), getSystemUnderTest(), false, null, EN);
        assertEquals(1, exe.getSuccess());
        assertEquals(2, exe.getFailures());
        assertEquals(3, exe.getErrors());
        assertEquals("Some Test", exe.getResults());
    }
    
    public void testThatAnExecutionErrorIsRecievedIfAnErrorOccuresInTheExecution()
    {
        Runner runner = Runner.newInstance("RUNNER");
        runner.setMainClass("Class not found");
        runner.setCmdLineTemplate("java -cp ${classpaths} ${mainClass} ${inputPath} ${outputPath} --xml");
        ClasspathSet classPaths = new ClasspathSet();
        classPaths.add(getRootPath());
        runner.setClasspaths(classPaths);
        
        Execution exe = runner.execute(getSpecification(), getSystemUnderTest(), false, null, EN);
        assertTrue(!StringUtil.isEmpty(exe.getExecutionErrorId()));
        assertTrue(exe.getExecutionErrorId().indexOf("Class not found") > 0);
    }
    
    public void testAgentUrlIsBuildCorrectly()
    {
        Runner runner = Runner.newInstance("RUNNER");
        
        runner.setServerName("localhost");
        runner.setServerPort("port");
        assertEquals("http://localhost:port", runner.agentUrl());
        
        runner.setSecured(true);
        assertEquals("https://localhost:port", runner.agentUrl());
    }
    
    private String getRootPath()
    {
    	String path = input.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath();
        return URIUtil.decoded(path);
    }

    private Specification getSpecification()
    {
        Specification spec = Specification.newInstance("test.html");
        spec.setRepository(getRepository());

        return spec;
    }
    
    private Repository getRepository()
    {
        Repository repo = Repository.newInstance("REPOSITORY");
        repo.setType(getFileType());
        repo.setBaseTestUrl(input.getParentFile().getAbsolutePath());
        repo.setUid("UID");

        return repo;
    }
    
    private RepositoryType getFileType()
    {
    	EnvironmentType JAVA = EnvironmentType.newInstance("JAVA");
        RepositoryType type = RepositoryType.newInstance("FILE");
        type.setTestUrlFormat("%s%s");
        type.registerClassForEnvironment(FileSystemRepository.class.getName(),JAVA);
        return type;
    }
    
    private SystemUnderTest getSystemUnderTest()
    {
        return SystemUnderTest.newInstance("SUT");
    }
    
    public static class FakeRunner
    {
        public static void main(String[] args) throws IOException
        {
            File output = new File(args[1]);
            FileWriter writer = new FileWriter(output);
            
            writer.write("<documents>");
            writer.write("  <document>");
            writer.write("      <statistics>");
            writer.write("          <success>1</success>");
            writer.write("          <failure>2</failure>");
            writer.write("          <error>3</error>");
            writer.write("          <ignored>4</ignored>");
            writer.write("      </statistics>");
            writer.write("      <results>Some Test</results>");
            writer.write("  </document>");
            writer.write("</documents>");
            
            writer.close();
        }
    }
}
