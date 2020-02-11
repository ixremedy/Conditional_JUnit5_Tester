/*
 * Author: Lev Vanyan
 * Lev.Vanyan@forthreal.com
 */

package com.forthreal.conditionaljunit5tester.classes;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import com.forthreal.conditionaljunit5tester.exceptions.TestCaseProcessException;

public class TestWatcherExtension
	implements TestWatcher
{
	@Override
	public void testFailed(ExtensionContext context, Throwable cause)
	{
		String testName =
			TestExecutionCondition.
				getClassFunction( context.getRequiredTestMethod().toString() );

		try
		{
			TestExecutionCondition.tracker.setTestFailed( testName );
		}
		catch( TestCaseProcessException exc )
		{
			System.err.println("Test case wasn't found: " + exc.getLocalizedMessage() );
		}
	}
	
	@Override
	public void testSuccessful(ExtensionContext context)
	{
		String testName =
				TestExecutionCondition.
					getClassFunction( context.getRequiredTestMethod().toString() );
		try
		{
			TestExecutionCondition.tracker.setTestSucceeded( testName );
		}
		catch( TestCaseProcessException exc )
		{
			System.err.println("Test case wasn't found: " + exc.getLocalizedMessage() );
		}
	}

}
