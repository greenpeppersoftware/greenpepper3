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

import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.greenpepper.runner.SpecificationRunnerMonitor;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.rpc.runner.report.Report;
import com.greenpepper.server.rpc.runner.report.ReportGenerator;
import junit.framework.TestCase;

public abstract class AbstractRunnerTest
		extends TestCase
{

	protected final Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	protected SpecificationRunnerMonitor monitor;
	protected XmlRpcRemoteRunner xmlRpcRemoteRunner;
	protected ReportGenerator reportGenerator;
	protected Report report;

	@Override
	protected void setUp()
			throws Exception
	{
		monitor = context.mock(SpecificationRunnerMonitor.class);
		xmlRpcRemoteRunner = context.mock(XmlRpcRemoteRunner.class);
		reportGenerator = context.mock(ReportGenerator.class);
		report = context.mock(Report.class);
	}

	@Override
	protected void tearDown()
			throws Exception
	{
		context.assertIsSatisfied();
	}

	public Execution createExecution()
	{
		Execution execution = new Execution();
		execution.setSuccess(4);
		execution.setFailures(3);
		execution.setErrors(2);
		execution.setIgnored(1);
		return execution;
	}

	public Throwable createException()
	{
		return new GreenPepperServerException();
	}

}
