package com.greenpepper.server.database.component;

import com.greenpepper.server.domain.component.ContentType;

import junit.framework.TestCase;

public class ComponentTypeTest extends TestCase
{
    public void testThatTheInstanciateReturnsTheGoodInstance()
    {
        assertEquals(ContentType.BOTH, ContentType.getInstance("BOTH"));
        assertEquals(ContentType.TEST, ContentType.getInstance("TEST"));
        assertEquals(ContentType.REQUIREMENT, ContentType.getInstance("REQUIREMENT"));
        assertEquals(ContentType.UNKNOWN, ContentType.getInstance("TYPE-NOT-SUPPORTED"));
    }
}
