package com.greenpepper.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Util class for internalisation.
 * JIRA dependend.
 * <p/>
 * Copyright (c) 2005 Pyxis technologies inc. All Rights Reserved.
 * @author jchuet
 */
public final class I18nUtil
{
    private I18nUtil() {}

    /**
     * Custom I18n. Based on WebWork i18n.
     * @param key
     * @return the i18nze message. If none found key is returned.
     */
    public static String getText(String key, ResourceBundle bundle)
    {
        try
        {
            return  bundle.getString(key);
        }
        catch (MissingResourceException ex)
        {
            return key;
        }
    }

	public static String getText(String key, ResourceBundle bundle, Object... arguments)
	{
		try
		{
			String value = bundle.getString(key);

			return MessageFormat.format(value, arguments);
		}
		catch (MissingResourceException ex)
		{
			return key;
		}
	}

    public static ResourceBundle getResourceBundle(String bundleName, Locale locale)
    {
        return ResourceBundle.getBundle(bundleName, locale);
    }
}
