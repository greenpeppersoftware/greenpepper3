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

import java.io.Writer;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.text.StringContains.containsString;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.greenpepper.server.domain.Execution;
import junit.framework.TestCase;

public class HtmlReportTest
		extends TestCase
{

	private final Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private Writer writer;
	private HtmlReport htmlReport;

	@Override
	protected void setUp()
			throws Exception
	{
		writer = context.mock(Writer.class);
		htmlReport = HtmlReport.newInstance("test");
	}

	@Override
	protected void tearDown()
			throws Exception
	{
		context.assertIsSatisfied();
	}

	public void testThatNothingIsPrintedWhenNoExecution()
			throws Exception
	{
		htmlReport.printTo(writer);
	}

	public void testThatExceptionIsPrintedCorrectly()
			throws Exception
	{
		htmlReport.renderException(new NullPointerException("testThatExceptionIsPrintedCorrectly"));

		context.checking(new Expectations()
		{
			{
				one(writer).write(with(allOf(containsString("testThatExceptionIsPrintedCorrectly"),
											 containsString("com.greenpepper.server.rpc.runner.report"))));
				one(writer).flush();
			}
		});

		htmlReport.printTo(writer);
	}

	public void testThatResultIsPrintedCorrectly()
			throws Exception
	{
		Execution execution = new Execution();
		execution.setResults("<html>test</html>");

		htmlReport.generate(execution);

		context.checking(new Expectations()
		{
			{
				one(writer).write(with(allOf(containsString("body, p, td, table, tr, .bodytext, .stepfield {"),
											 containsString("<title>test</title>"),
											 containsString("<div id=\"Content\""))));
				one(writer).flush();
			}
		});

		htmlReport.printTo(writer);
	}
}
