/*
 * Author: Lev Vanyan
 * Lev.Vanyan@forthreal.com
 */

package com.forthreal.conditionaljunit5tester.classes;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import com.forthreal.conditionaljunit5tester.tracker.TestDependencyTracker;

import io.vavr.control.Option;

/* this will be used to evaluate the current state of testing of
 * the software and determine if we need to break the testing process */
public class TestExecutionCondition
	implements ExecutionCondition
{
	/* chop out everything that isn't the classname and the function name */
	public static String getClassFunction(String fullFunctionName)
	{
		Integer pos = fullFunctionName.lastIndexOf( '.' );
		
		if(pos > 0)
		{
			pos = fullFunctionName.substring(0, pos).lastIndexOf( '.' );
			
			if( pos > 0 )
			{
				fullFunctionName = fullFunctionName.substring( pos + 1 );
			}
		}
		
		return fullFunctionName;
	}
	
	public static TestDependencyTracker tracker = null;

	static
	{
		try
		{
			tracker =
				new TestDependencyTracker
				  (
					new TestDependencyTracker.TestDependencyBuilder().
						  build().
						  addMethod("ApplicationTest.canCreate()", Option.none()).
						  addMethod
						    (
						      "ApplicationTest.canConnect()",
						      Option.of("ApplicationTest.canCreate()")
						    ).
						  get()
				  );
		}
		catch(Exception exc)
		{
			
		}

	}
	
	/* the following will be called before each test annotated with @ExtendWith */
	@Override
	public ConditionEvaluationResult
		evaluateExecutionCondition(ExtensionContext context)
	{
		String functionName = getClassFunction( context.getTestMethod().get().toString() ); 
		System.out.println(">>>>> executed: " + functionName);
		
		Boolean isEnabled = tracker.isTestBranchEnabled( functionName );

		return (isEnabled == true) ?
					ConditionEvaluationResult.enabled( " 123 ") :
					ConditionEvaluationResult.disabled( " 123 ");
	}

}
