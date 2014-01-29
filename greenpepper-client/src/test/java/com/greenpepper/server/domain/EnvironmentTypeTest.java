package com.greenpepper.server.domain;

import java.util.Vector;

import com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller;

public class EnvironmentTypeTest extends junit.framework.TestCase
{
    public void testTheBasicBehaviorOfTheEquals()
    {
        assertFalse(EnvironmentType.newInstance("TYPE-1").equals(null));
        assertFalse(EnvironmentType.newInstance("TYPE-1").equals(new Integer(0)));
        assertFalse(EnvironmentType.newInstance("TYPE-1").equals(EnvironmentType.newInstance("TYPE-2")));
        assertTrue(EnvironmentType.newInstance("TYPE-1").equals(EnvironmentType.newInstance("TYPE-1")));
    }

    public void testThatTheEnvironmentTypeIsAlphaComparable()
    {
        assertEquals(0, EnvironmentType.newInstance("TYPE-1").compareTo(EnvironmentType.newInstance("TYPE-1")));
        assertEquals(-1, EnvironmentType.newInstance("TYPE-1").compareTo(EnvironmentType.newInstance("TYPE-2")));
        assertEquals(1, EnvironmentType.newInstance("TYPE-2").compareTo(EnvironmentType.newInstance("TYPE-1")));
    }
    
    public void testThatAnEnvironmentTypeIsProperlyMarshalled()
    {
    	EnvironmentType type = EnvironmentType.newInstance("TYPE");
        Vector<Object> params = new Vector<Object>();
        params.add(XmlRpcDataMarshaller.ENVTYPE_NAME_IDX, "TYPE");
        assertEquals(params, type.marshallize());
    }
    
    public void testTheHashCodeIsTheNameHasCode()
    {
    	assertEquals("TYPE-1".hashCode(), EnvironmentType.newInstance("TYPE-1").hashCode());
    }
}
