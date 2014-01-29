package com.greenpepper.server;

/**
 * ServerPropertiesManager.
 * For server properties persistence.
 * Copyright (c) 2006 Pyxis technologies inc. All Rights Reserved.
 * @author JCHUET
 */
public interface ServerPropertiesManager
{
    public final static String URL = "GREENPEPPER_URL";
    public final static String HANDLER = "GREENPEPPER_HANDLER";
    public final static String PROJECT = "GREENPEPPER_PROJECT";
    public final static String SEQUENCE = "greenpepper.";
    
    /**
     * Retrieves the property for the specified key and identifier.
     * </p>
     * @param key
     * @param identifier
     * @return the property for the specified key and params.
     */
    public String getProperty(String key, String identifier);
    
    /**
     * Saves the property value for the specified key and identifier.
     * </p>
     * @param key
     * @param value
     * @param identifier
     */
    public void setProperty(String key, String value, String identifier);
}
