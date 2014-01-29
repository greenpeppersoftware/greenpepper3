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

import com.greenpepper.server.domain.DocumentNode;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.SystemUnderTest;

public class RemoteSuiteRunnerTest
		extends AbstractRunnerTest
{

	private RemoteSuiteRunner runner;

	@Override
	protected void setUp()
			throws Exception
	{
		super.setUp();

		runner = new RemoteSuiteRunner();
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

	public void testWithEmptySpecificationResultList()
			throws Exception
	{
		context.checking(new Expectations()
		{
			{
				one(xmlRpcRemoteRunner).getSpecificationHierarchy(with(any(Repository.class)), with(any(SystemUnderTest.class)));
				will(returnValue(new DocumentNode("test")));

				one(monitor).testRunning("repositoryId/a");
				one(monitor).testDone(0, 0, 0, 0);
			}
		});

		runner.run("a", "b");
	}

	public void testWithSpecificationHierarchyFailure()
			throws Exception
	{
		final Throwable exception = createException();

		context.checking(new Expectations()
		{
			{
				one(xmlRpcRemoteRunner).getSpecificationHierarchy(with(any(Repository.class)), with(any(SystemUnderTest.class)));
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
		final DocumentNode documentNode = createDocumentNode();

		context.checking(new Expectations()
		{
			{
				one(xmlRpcRemoteRunner).getSpecificationHierarchy(with(any(Repository.class)), with(any(SystemUnderTest.class)));
				will(returnValue(documentNode));

				one(monitor).testRunning("repositoryId/A");

				one(xmlRpcRemoteRunner).runSpecification("project", "sut", "repositoryId", "A", false, "en");
				will(returnValue(execution));

				one(reportGenerator).openReport("repositoryId-A");
				will(returnValue(report));

				one(report).generate(execution);

				one(monitor).testDone(4, 3, 2, 1);

				one(reportGenerator).closeReport(report);
			}
		});

		runner.run("a", "b");
	}

	private DocumentNode createDocumentNode()
	{
		final DocumentNode documentNode = new DocumentNode("test");

		DocumentNode childNodeA = new DocumentNode("A");
		childNodeA.setIsExecutable(true);

		documentNode.addChildren(childNodeA);
		return documentNode;
	}
}