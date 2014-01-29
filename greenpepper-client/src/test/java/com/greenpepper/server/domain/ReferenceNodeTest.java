package com.greenpepper.server.domain;

import java.util.Hashtable;
import java.util.Vector;

import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;

import junit.framework.TestCase;

public class ReferenceNodeTest extends TestCase 
{
	ReferenceNode node = new ReferenceNode("TITLE", "REPO-UID", "SUT-NAME", "SECTIONS");

	public void testReferenceNodeIsProperlyMarshalled()
	{
		Vector<Object> expectedVector = referenceVector();
		node.setIsExecutable(true);
		
		assertEquals(expectedVector, node.marshallize());
	}
	
	public void testThatReferenceNodeCannotHaveChildren()
	{
		try 
		{
			node.addChildren(new DocumentNode("TITLE-CHILDREN"));
			fail("Reference not should not accept child.");
		} catch (RuntimeException e) 
		{
			assertTrue(true);
		}
	}
	
	public void testTheEqualBehaviour()
	{
		assertFalse(node.equals(null));
		assertFalse(node.equals(new DocumentNode("TITLE")));
		assertFalse(node.equals(new ReferenceNode("TITLE-DIFFERENT", "REPO-UID", "SUT-NAME", "SECTIONS")));
		assertFalse(node.equals(new ReferenceNode("TITLE", "REPO-UID-DIFFERENT", "SUT-NAME", "SECTIONS")));
		assertFalse(node.equals(new ReferenceNode("TITLE", "REPO-UID", "SUT-NAME-DIFFERENTE", "SECTIONS")));
		assertFalse(node.equals(new ReferenceNode("TITLE", "REPO-UID", "SUT-NAME-DIFFERENTE", "SECTIONS-DIFFERENTE")));

		assertEquals(node, new ReferenceNode("TITLE", "REPO-UID", "SUT-NAME", "SECTIONS"));	
	}
	
	public void testReferenceNodeIsProperlyUnmarshallized()
	{
		DocumentNode hierarchy = XmlRpcDataMarshaller.toDocumentNode(createExpectedVector());
		ReferenceNode node = (ReferenceNode)hierarchy.getChildren().iterator().next();
		
		assertEquals(node.getTitle(), "TITLE");
		assertEquals(node.isExecutable(), true);
		assertEquals(node.canBeImplemented(), false);
		assertEquals(node.getChildren().size(), 0);
		assertEquals(node.getRepositoryUID(), "REPO-UID");
		assertEquals(node.getSutName(), "SUT-NAME");
		assertEquals(node.getSection(), "SECTIONS");
	}
	
	private Vector<Object> referenceVector()
	{
		Vector<Object> referenceVector = new Vector<Object>();
		referenceVector.add("TITLE");
		referenceVector.add(true);  // executable
		referenceVector.add(false);	// can be implemented
		referenceVector.add(new Hashtable<String, Object>());	// children
		referenceVector.add("REPO-UID");
		referenceVector.add("SUT-NAME");
		referenceVector.add("SECTIONS");
		return referenceVector;
	}

	@SuppressWarnings("unchecked")
	private Vector<Object> createExpectedVector() 
	{
		Vector<Object> expectedVector = new Vector<Object>();		
		expectedVector.add("MAIN NODE");
		expectedVector.add(true);
		expectedVector.add(false);
		Hashtable children  = new Hashtable();
		children.put("TITLE", referenceVector());
		expectedVector.add(children);
		return expectedVector;
	}
}