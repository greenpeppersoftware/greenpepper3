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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.greenpepper.GreenPepper;
import com.greenpepper.GreenPepperCore;
import com.greenpepper.runner.NullSpecificationRunnerMonitor;
import com.greenpepper.runner.SpecificationRunner;
import com.greenpepper.runner.SpecificationRunnerMonitor;
import com.greenpepper.runner.SpecificationRunnerMonitorProxy;
import com.greenpepper.server.rpc.runner.report.FileReportGenerator;
import com.greenpepper.server.rpc.runner.report.HtmlReport;
import com.greenpepper.server.rpc.runner.report.ReportGenerator;
import com.greenpepper.server.rpc.runner.report.XmlReport;
import com.greenpepper.util.Bean;
import com.greenpepper.util.IOUtil;
import static com.greenpepper.util.URIUtil.decoded;
import static com.greenpepper.util.URIUtil.flatten;
import com.greenpepper.util.cli.ArgumentMissingException;
import com.greenpepper.util.cli.CommandLine;
import com.greenpepper.util.cli.Option;
import com.greenpepper.util.cli.ParseException;

public class CommandLineRemoteRunner
{

	private final CommandLine cli;
	private final Map<String, Object> options;
	private SpecificationRunner runner;
	private SpecificationRunnerMonitor monitor;
	private PrintStream out;

	public CommandLineRemoteRunner()
	{
		this(System.out);
	}

	public CommandLineRemoteRunner(PrintStream out)
	{
		this.cli = new CommandLine();
		this.options = new HashMap<String, Object>();
		this.monitor = new NullSpecificationRunnerMonitor();
		this.out = out;
	}

	public void setMonitor(Object monitor)
	{
		this.monitor = new SpecificationRunnerMonitorProxy(monitor);
	}

	public void run(String... args)
			throws Exception
	{
		defineCommandLine();
		if (!parseCommandLine(args))
		{
			return;
		}
		runSpec();
	}

	private void runSpec()
			throws Exception
	{
		options.putAll(cli.getOptionValues());
		options.put("reportGenerator", reportGenerator());
		options.put("monitor", monitor);
		options.put("xmlRpcRemoteRunner", xmlRpcRemoteRunner());
		new Bean(runner).setProperties(options);
		runner.run(source(), destination());
	}

	private ReportGenerator reportGenerator() throws IOException
	{
		FileReportGenerator generator = new FileReportGenerator( createOuputDirectory() );
		generator.adjustReportFilesExtensions( optionSpecified( "suite" ) || output() == null );
		generator.setReportClass( optionSpecified( "xml" ) ? XmlReport.class : HtmlReport.class );
		return generator;
	}

	private XmlRpcRemoteRunner xmlRpcRemoteRunner()
	{
		return new XmlRpcRemoteRunner(url(), handler());
	}

	private File createOuputDirectory()
			throws IOException
	{
		return IOUtil.createDirectoryTree(outputDirectory());
	}

	private String url()
	{
		return (String)cli.getOptionValue("url");
	}

	private String handler()
	{
		return (String)cli.getOptionValue("handler");
	}

	private String input()
	{
		return cli.getArgument(0) != null ? decoded(cli.getArgument(0)) : null;
	}

	private String output()
	{
		return cli.getArgument(1) != null ? decoded(cli.getArgument(1)) : null;
	}

	public String source()
	{
		return optionSpecified("repository") ? input() : fileName(input());
	}

	private File outputDirectory()
			throws IOException
	{
		if (optionSpecified("suite"))
		{
			return output() != null ? new File(output()) : (File)cli.getOptionValue("output");
		}
		return output() != null ? parentFile(output()) : (File)cli.getOptionValue("output");
	}

	private File parentFile(String pathname)
			throws IOException
	{
		return new File(pathname).getCanonicalFile().getParentFile();
	}

	private String fileName(String pathname)
	{
		return new File(pathname).getName();
	}

	private String destination()
			throws IOException
	{
		if (optionSpecified("suite"))
		{
			return "";
		}
		return output() != null ? fileName(output()) :
			   optionSpecified("repository") ? flatten(input()) : fileName(input());
	}

	private boolean parseCommandLine(String[] args)
			throws ParseException
	{
		cli.parse(args);
		if (optionSpecified("help"))
		{
			return displayUsage();
		}
		if (optionSpecified("version"))
		{
			return displayVersion();
		}
		if (input() == null)
		{
			throw new ArgumentMissingException("input");
		}
		if (url() == null)
		{
			throw new ArgumentMissingException("url");
		}
		if (!optionSpecified("project"))
		{
			throw new ArgumentMissingException("project");
		}
		if (!optionSpecified("systemUnderTest"))
		{
			throw new ArgumentMissingException("system under test");
		}
		if (!optionSpecified("repositoryId"))
		{
			throw new ArgumentMissingException("repository id");
		}

		runner = optionSpecified( "suite" ) ? new RemoteSuiteRunner() : new RemoteDocumentRunner();
		return true;
	}

	private boolean optionSpecified(String name)
	{
		return cli.hasOptionValue(name);
	}

	private boolean displayVersion()
	{
		out.println(String.format("GreenPepper version \"%s\"", GreenPepperCore.VERSION));
		return false;
	}

	private boolean displayUsage()
	{
		out.println(cli.usage());
		return false;
	}

	private void defineCommandLine()
	{
		File workingDirectory = new File(System.getProperty("user.dir"));

		String banner = "greenpepper [options] input [ouput]\n" +
						"Run the input specification and produce a report in output file or in directory specified by -o";
		cli.setBanner(banner);

		cli.defineOption(cli.buildOption("locale", "-l", "--locale LANG", "Set application language (en, fr, ...)")
				.asType(Locale.class).whenPresent(new SetLocale()));
		cli.defineOption(cli.buildOption("url", "-u", "--url URL", "GreenPepper Server Context Path"));
		cli.defineOption(cli.buildOption("handler", "-h", "--handle HANDLE", "GreenPepper Server XML-RPC handler")
				.defaultingTo("greenpepper1"));
		cli.defineOption(cli.buildOption("project", "-p", "--project PROJECT", "Project Name"));
		cli.defineOption(cli.buildOption("systemUnderTest", "-t", "--sut SUT", "System Under Test Name"));
		cli.defineOption(cli.buildOption("repositoryId", "--rep ID", "Repository Id"));
		cli.defineOption(cli.buildOption("output", "-o DIRECTORY",
										 "Produce reports in DIRECTORY (defaults to current directory)")
				.defaultingTo(workingDirectory).asType(File.class));
		cli.defineOption(cli.buildOption("suite", "-s", "--suite",
										 "Run a suite rather than a single test (output must refer to a directory)"));
		cli.defineOption(cli.buildOption("xml", "--xml", "Generate XML report (defaults to plain)"));
		cli.defineOption(cli.buildOption("help", "--help", "Display this help and exit"));
		cli.defineOption(cli.buildOption("version", "--version", "Output version information and exit"));
		cli.defineOption(cli.buildOption("debug", "--debug", "Enable debug mode").whenPresent(new SetDebugMode()));
	}

	public static class SetDebugMode
			implements Option.Stub
	{

		public void call(Option option)
		{
			GreenPepper.setDebugEnabled(true);
		}
	}

	public static class SetLocale
			implements Option.Stub
	{

		public void call(Option option)
		{
			GreenPepper.setLocale((Locale)option.getValue());
		}
	}
}