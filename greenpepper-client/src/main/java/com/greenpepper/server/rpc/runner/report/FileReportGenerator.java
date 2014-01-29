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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.greenpepper.util.Factory;
import com.greenpepper.util.IOUtil;

public class FileReportGenerator
		implements ReportGenerator
{

	private final File reportsDirectory;

	private Class<? extends Report> reportClass;
	private boolean automaticExtension;

	public FileReportGenerator(File outputDir)
	{
		this.reportsDirectory = outputDir;
		this.reportClass = HtmlReport.class;
	}

	public void setReportClass(Class<? extends Report> reportClass)
	{
		this.reportClass = reportClass;
	}

	public void adjustReportFilesExtensions(boolean enable)
	{
		this.automaticExtension = enable;
	}

	public Report openReport(String name)
	{
		Factory<Report> factory = new Factory<Report>(reportClass);
		return factory.newInstance(name);
	}

	public void closeReport(Report report)
			throws IOException
	{
		FileWriter out = null;
		try
		{
			File reportFile = new File(reportsDirectory, outputNameOf(report));
			IOUtil.createDirectoryTree(reportFile.getParentFile());
			out = new FileWriter(reportFile);
			report.printTo(out);
			out.flush();
		}
		finally
		{
			IOUtil.closeQuietly(out);
		}
	}

	private String outputNameOf(Report report)
	{
		String name = report.getName();
		if (automaticExtension && !name.endsWith(extensionOf(report)))
		{
			name += extensionOf(report);
		}
		return name;
	}

	private String extensionOf(Report report)
	{
		return "." + report.getType();
	}
}