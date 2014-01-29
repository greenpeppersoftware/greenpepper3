package com.greenpepper.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.greenpepper.util.ExceptionImposter;

public final class GreenPepperServer
{
    public static final String VERSION = "${pom.version}";

	private static Date versionDate;

    public static Date versionDate()
    {
		if (versionDate == null)
		{
			// ${timesamp} will be replaced by the maven-resources filtering
			// In dev, this will fail, modify this file without commiting
			try
			{
//				versionDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("17/10/2011 10:49");
				versionDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${timestamp}");
			}
			catch (Exception ex)
			{
				throw ExceptionImposter.imposterize(ex);
			}
		}

		return versionDate;
    }
}
