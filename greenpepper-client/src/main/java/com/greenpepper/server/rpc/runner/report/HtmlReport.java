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
package com.greenpepper.server.rpc.runner.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.rpc.runner.XmlRpcRemoteRunner;
import com.greenpepper.util.ExceptionImposter;
import com.greenpepper.util.ExceptionUtils;

public class HtmlReport
		implements Report
{

	private final String name;
	private Execution execution;
	private Throwable exception;

	public static HtmlReport newInstance(String name)
	{
		return new HtmlReport(name);
	}

	public HtmlReport(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return "html";
	}

	public void printTo(Writer writer)
			throws IOException
	{
		if (exception != null)
		{
			writer.write(ExceptionUtils.stackTrace(exception, "\n"));
			writer.flush();
			return;
		}

		if (execution != null)
		{
			writer.write(toHtml(execution, true));
			writer.flush();
		}
	}

	public void renderException(Throwable t)
	{
		this.exception = t;
	}

	public void generate(Execution execution)
	{
		this.execution = execution;
	}

	private String toHtml(Execution execution, boolean includeStyle)
			throws IOException
	{
		StringBuilder html = new StringBuilder();

		String results = execution.getResults();

		if (includeStyle)
		{
			html.append("<html>\n")
					.append("  <head>\n")
					.append("  <title>").append(getName()).append("</title>\n")
					.append("<style>\n")
					.append(getStyleContent())
					.append("\n</style>\n")
					.append("</head>\n")
					.append("<body>\n")
					.append("<div id=\"Content\" style=\"text-align:left; padding: 5px;\">")
					.append(results.replace("<html>", "").replace("</html>", ""))
					.append("</div>\n")
					.append("</body>\n")
					.append("</html>");
		}
		else
		{
			html.append(results);
		}

		return html.toString();
	}

	private String getStyleContent()
	{
		try
		{
			InputStream is = XmlRpcRemoteRunner.class.getResource("style.css").openStream();
			byte[] bytes = new byte[is.available()];
			if (is.read(bytes) > 0)
			{
				return new String(bytes);
			}
			else
			{
				throw new Exception("Cannot read style.css resource from jar");
			}
		}
		catch (Exception ex)
		{
			throw ExceptionImposter.imposterize(ex);
		}
	}
}