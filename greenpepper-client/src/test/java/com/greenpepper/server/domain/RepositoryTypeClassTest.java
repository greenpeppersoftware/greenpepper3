package com.greenpepper.server.domain;

import junit.framework.TestCase;

public class RepositoryTypeClassTest extends TestCase
{
    public void testTheBasicBehaviorOfTheEquals()
    {
    	RepositoryType type = RepositoryType.newInstance("JIRA");
    	EnvironmentType env = EnvironmentType.newInstance("JAVA");
        assertFalse(RepositoryTypeClass.newInstance(type, env, "PROJECT-1").equals(null));
        assertFalse(RepositoryTypeClass.newInstance(type, env, "PROJECT-1").equals(new Integer(0)));
        assertFalse(RepositoryTypeClass.newInstance(type, env, "PROJECT-1").equals(Project.newInstance("PROJECT-2")));
        assertTrue(RepositoryTypeClass.newInstance(type, env, "PROJECT-1").equals(RepositoryTypeClass.newInstance(type, env, "PROJECT-1")));
    }
}
