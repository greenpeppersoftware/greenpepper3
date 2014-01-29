/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */
package com.greenpepper.server.rpc.runner;

import com.greenpepper.runner.LoggingMonitor;
import com.greenpepper.util.cli.ParseException;

public class Main
{

	public static void main(String[] args)
	{
		CommandLineRemoteRunner runner = new CommandLineRemoteRunner();
		LoggingMonitor monitor = new LoggingMonitor(System.out, System.err);
		runner.setMonitor(monitor);

		try
		{
			runner.run(args);
		}
		catch (ParseException e)
		{
			System.err.println(e.getMessage());
			System.err.println("Try '--help' for more information.");

			if (e.getCause() != null)
			{
				System.err.println("Caused by:");
				e.getCause().printStackTrace(System.err);
			}

			System.exit(1);
		}
		catch (Throwable t)
		{
			monitor.exceptionOccured(t);
			System.exit(1);
		}
	}
}