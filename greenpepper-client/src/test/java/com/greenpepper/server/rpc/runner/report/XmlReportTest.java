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

public class XmlReportTest
		extends TestCase
{

	private static final String simpleResultString =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<documents>\n" +
			"    <document>\n" +
			"        <statistics>\n" +
			"            <success>0</success>\n" +
			"            <failure>0</failure>\n" +
			"            <error>0</error>\n" +
			"            <ignored>0</ignored>\n" +
			"        </statistics>\n" +
			"        <results><![CDATA[<html>test</html>]]></results>\n" +
			"    </document>\n" +
			"</documents>";

	private final Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private Writer writer;
	private XmlReport xmlReport;

	@Override
	protected void setUp()
			throws Exception
	{
		writer = context.mock(Writer.class);
		xmlReport = XmlReport.newInstance("test");
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
		xmlReport.printTo(writer);
	}

	public void testThatExceptionIsPrintedCorrectly()
			throws Exception
	{
		xmlReport.renderException(new NullPointerException("testThatExceptionIsPrintedCorrectly"));

		context.checking(new Expectations()
		{
			{
				one(writer).write(with(containsString("<global-exception><![CDATA[java.lang.NullPointerException: testThatExceptionIsPrintedCorrectly")));
				one(writer).flush();
			}
		});

		xmlReport.printTo(writer);
	}

	public void testThatResultIsPrintedCorrectly()
			throws Exception
	{
		Execution execution = new Execution();
		execution.setResults("<html>test</html>");

		xmlReport.generate(execution);

		context.checking(new Expectations()
		{
			{
				one(writer).write(with(allOf(containsString("<success>0</success>"),
											 containsString("<failure>0</failure>"),
											 containsString("<error>0</error>"),
											 containsString("<results><![CDATA[<html>test</html>]]></results>"))));
				one(writer).flush();
			}
		});

		xmlReport.printTo(writer);
	}
}