package com.greenpepper.server.database.configuration;

import java.net.URL;

import junit.framework.TestCase;

import com.greenpepper.server.configuration.ServerConfiguration;

public class ServerConfigurationTest extends TestCase
{
    private ServerConfiguration config;
    
    public void setUp() throws Exception
    {
        URL url = ServerConfigurationTest.class.getResource("configuration-test.xml");
        config = ServerConfiguration.load(url);
    }
    
    public void testThatPropertiesAreProperlyLoaded()
    {
        assertEquals("value1", config.getProperties().getProperty("property1"));
        assertEquals("value2", config.getProperties().getProperty("property2"));
        assertEquals("some/url/with/slashes", config.getProperties().getProperty("property3"));
    }
}
