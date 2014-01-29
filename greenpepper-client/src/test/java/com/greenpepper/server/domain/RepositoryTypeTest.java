package com.greenpepper.server.domain;

import java.util.Hashtable;
import java.util.Vector;

import junit.framework.TestCase;

import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;

public class RepositoryTypeTest extends TestCase
{
    public void testTheBasicBehaviorOfTheEquals()
    {        
        assertFalse(RepositoryType.newInstance("TYPE-1").equals(null));
        assertFalse(RepositoryType.newInstance("TYPE-1").equals(new Integer(0)));
        assertFalse(RepositoryType.newInstance("TYPE-1").equals(RepositoryType.newInstance("TYPE-2")));
        assertTrue(RepositoryType.newInstance("TYPE-1").equals(RepositoryType.newInstance("TYPE-1")));
    }
    
    public void testThatTheRepositoryTypeIsAlphaComparable()
    {
        assertEquals(0, RepositoryType.newInstance("TYPE-1").compareTo(RepositoryType.newInstance("TYPE-1")));
        assertEquals(-1, RepositoryType.newInstance("TYPE-1").compareTo(RepositoryType.newInstance("TYPE-2")));
        assertEquals(1, RepositoryType.newInstance("TYPE-2").compareTo(RepositoryType.newInstance("TYPE-1")));
    }
    
    public void testThatTheResolveNameReturnsTheFormatedString() throws GreenPepperServerException
    {
        RepositoryType type = RepositoryType.newInstance("TYPE-1");
        type.setDocumentUrlFormat("%s%s");
        Repository repository = Repository.newInstance("UID-1");
        repository.setBaseRepositoryUrl("URL-1");
        repository.setBaseTestUrl("URI-1");

        assertEquals("URL-1DOCUMENT", type.resolveName(new TestMyDocument(repository, "DOCUMENT")));
    }
    
    public void testThatTheResolveURIReturnsTheFormatedString() throws GreenPepperServerException
    {
        RepositoryType type = RepositoryType.newInstance("TYPE-1");
        type.setTestUrlFormat("%s%s");
        Repository repository = Repository.newInstance("UID-1");
        repository.setBaseTestUrl("URI-1");

        assertEquals("URI-1DOCUMENT", type.resolveUri(new TestMyDocument(repository, "DOCUMENT")));
    }

    public void testThatARepositoryTypeIsProperlyMarshalled()
    {  
        String type = "FILE";
    	EnvironmentType JAVA = EnvironmentType.newInstance("JAVA");
        RepositoryType repoType = RepositoryType.newInstance(type);
        repoType.registerClassForEnvironment("REPO-CLASS",JAVA);
        repoType.setDocumentUrlFormat("%s%s");
        repoType.setTestUrlFormat("%s%s");

        Vector<Object> params = new Vector<Object>();
        params.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_NAME_IDX, type);
        Hashtable<String, String> cparams = new Hashtable<String, String>();
        cparams.put("JAVA", "REPO-CLASS");
        params.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_REPOCLASSES_IDX, cparams);
        params.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_NAME_FORMAT_IDX, "%s%s");
        params.add(XmlRpcDataMarshaller.REPOSITORY_TYPE_URI_FORMAT_IDX, "%s%s");
        
        assertEquals(params, repoType.marshallize());
     }
    
    class TestMyDocument extends Document
    {
		private static final long serialVersionUID = 1L;
		
        TestMyDocument(Repository repository, String name)
        {
            super();
            setRepository(repository);
            setName(name);
        }
    }
}
