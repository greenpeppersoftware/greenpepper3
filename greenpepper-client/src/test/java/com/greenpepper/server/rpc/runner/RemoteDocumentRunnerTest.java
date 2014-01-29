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

import java.util.Locale;

import org.jmock.Expectations;

import com.greenpepper.server.domain.Execution;

public class RemoteDocumentRunnerTest
		extends AbstractRunnerTest
{

	private RemoteDocumentRunner runner;

	@Override
	protected void setUp()
			throws Exception
	{
		super.setUp();

		runner = new RemoteDocumentRunner();
		runner.setLocale(Locale.ENGLISH);
		runner.setMonitor(monitor);
		runner.setProject("project");
		runner.setReportGenerator(reportGenerator);
		runner.setRepositoryId("repositoryId");
		runner.setSystemUnderTest("sut");
		runner.setXmlRpcRemoteRunner(xmlRpcRemoteRunner);
	}

	@Override
	protected void tearDown()
			throws Exception
	{
		super.tearDown();
	}

	public void testRunningSpecificationWithFailure()
			throws Exception
	{
		final Throwable exception = createException();

		context.checking(new Expectations()
		{
			{
				one(monitor).testRunning("repositoryId/a");

				one(xmlRpcRemoteRunner).runSpecification("project", "sut", "repositoryId", "a", false, "en");
				will(throwException(exception));

				one(monitor).exceptionOccured(exception);
			}
		});

		runner.run("a", "b");
	}

	public void testWithReportGenerationFailure()
			throws Exception
	{
		final Throwable exception = createException();
		final Execution execution = createExecution();

		context.checking(new Expectations()
		{
			{
				one(monitor).testRunning("repositoryId/a");

				one(xmlRpcRemoteRunner).runSpecification("project", "sut", "repositoryId", "a", false, "en");
				will(returnValue(execution));

				one(reportGenerator).openReport("repositoryId-a");
				will(returnValue(report));

				one(report).generate(execution);
				will(throwException(exception));

				one(report).renderException(exception);

				one(monitor).exceptionOccured(exception);

				one(reportGenerator).closeReport(report);
			}
		});

		runner.run("a", "b");
	}

	public void testWithClosingReportFailure()
			throws Exception
	{
		final Throwable exception = createException();
		final Execution execution = createExecution();

		context.checking(new Expectations()
		{
			{
				one(monitor).testRunning("repositoryId/a");

				one(xmlRpcRemoteRunner).runSpecification("project", "sut", "repositoryId", "a", false, "en");
				will(returnValue(execution));

				one(reportGenerator).openReport("repositoryId-a");
				will(returnValue(report));

				one(report).generate(execution);

				one(monitor).testDone(4, 3, 2, 1);

				one(reportGenerator).closeReport(report);
				will(throwException(exception));

				one(monitor).exceptionOccured(exception);
			}
		});

		runner.run("a", "b");
	}

	public void testASuccessfullExecution()
			throws Exception
	{
		final Execution execution = createExecution();

		context.checking(new Expectations()
		{
			{
				one(monitor).testRunning("repositoryId/a");

				one(xmlRpcRemoteRunner).runSpecification("project", "sut", "repositoryId", "a", false, "en");
				will(returnValue(execution));

				one(reportGenerator).openReport("repositoryId-a");
				will(returnValue(report));

				one(report).generate(execution);

				one(monitor).testDone(4, 3, 2, 1);

				one(reportGenerator).closeReport(report);
			}
		});

		runner.run("a", "b");
	}
}
