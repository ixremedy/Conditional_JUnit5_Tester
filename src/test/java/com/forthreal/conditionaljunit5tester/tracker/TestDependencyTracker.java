/*
 * Author: Lev Vanyan
 * Lev.Vanyan@forthreal.com
 */

package com.forthreal.conditionaljunit5tester.tracker;

import com.forthreal.conditionaljunit5tester.classes.TestResult;
import com.forthreal.conditionaljunit5tester.exceptions.DependencyNotFoundException;
import com.forthreal.conditionaljunit5tester.exceptions.TestCaseProcessException;
import com.forthreal.conditionaljunit5tester.exceptions.TestNotFoundException;
import com.forthreal.conditionaljunit5tester.exceptions.TestTreeNotSetException;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.control.Option;
import io.vavr.control.Try;

/* this class is used for tracking of dependent tests,
 * so that if a test failed, the dependent tests that should be
 * run, can be tracked */
public class TestDependencyTracker
{	
	private TestDependencyBuilder.TestDependency testDependencies;
	
	public static class TestDependencyBuilder
	{		
		public class TestDependency
		{
			private LinkedHashMap linkedMap;

			private TestDependency()
			{
				linkedMap = LinkedHashMap.empty();
			}
			
			
		}
		
		private TestDependency dependencies;
		private String methodName;
		private String dependentMethodName;

		public TestDependencyBuilder build()
		{
			dependencies = new TestDependency();
			
			return this;
		}

		public TestDependency get()
		{
			return dependencies;
		}
		
		/* this is a method that follows the builder concept, so that
		 * you can chain the addMethod calls one after another */
		public TestDependencyBuilder addMethod
			(
				String methodName,
				Option<String> opDependentOnMethodName 
			)
			throws DependencyNotFoundException
		{
			Tuple2 newMethod;
			
			/* dependent method not empty */
			if( opDependentOnMethodName.isEmpty() == false )
			{
				/* check that we can find this method, if not - throw */
				if( dependencies.linkedMap.get( opDependentOnMethodName.get() ).isEmpty() )
				{
					throw new DependencyNotFoundException();
				}
				
				newMethod =
					Tuple.of
					  (
					    methodName ,
					    Tuple.of
					      (
					    	opDependentOnMethodName,
					        new TestResult()
					      )
					  ); 
			}
			/* this is a root-level test method, without dependencies
			 * on results of other tests*/
			else
			{
				newMethod =
						Tuple.of
						  (
						    methodName ,
						    Tuple.of
						      (
						    	Option.none(),
						        new TestResult()
						      )
						  ); 
			}
			
			dependencies.linkedMap =
					dependencies.linkedMap.put( newMethod );
			
			return this;
		}
	}

	public TestDependencyTracker(TestDependencyBuilder.TestDependency dependencies)
	{
		this.testDependencies = dependencies;
	}
	
	private Option<Tuple2> checkTestCaseExists(String testName)
			throws TestTreeNotSetException,
			   TestNotFoundException
	{
		if( testDependencies.linkedMap.isEmpty() )
		{
			throw new TestTreeNotSetException();
		}
		
		/* test case name not found */
		if(testDependencies.linkedMap.containsKey( testName ) == false)
		{
			throw new TestNotFoundException();
		}

		return testDependencies.linkedMap.get( testName );
	}
	
	/* set a test to the succeeded state */
	public void  setTestSucceeded(String testName)
		throws TestCaseProcessException
	{
			( (TestResult) checkTestCaseExists( testName ).get()._2 ).
			setTested( true ).
			setResult( true );
	}
	
	/* set a test to the failed state */
	public void setTestFailed(String testName)
		throws TestCaseProcessException
	{
			( (TestResult) checkTestCaseExists( testName ).get()._2 ).
			setTested( true ).
			setResult( false );
	}
	
	/* check if any test that this tested is dependent on - failed */
	public Boolean isTestBranchEnabled(String testName)
	{
		
		/* we are navigating a linked list of values where the linking element
		 * is the name of the parent testCase, which is the 1st element in the
		 * 2nd tuple */
		Boolean result = 
		   (Boolean)
			testDependencies.linkedMap.
			  get( testName ).
			  map( value -> {
				
				Integer iteration = 0;
				
				Boolean canProceed = true;
				Tuple2<Option<String>,TestResult> currResult, nextResult;
				
				nextResult =  (Tuple2<Option<String>,TestResult>) value;
				
				do
				{
					iteration++;
					currResult = nextResult;
					
					System.out.println
					  (
						"currResult (isEmpty=" +
						  currResult._1.isEmpty() +
					    ",canProceed=" +
						  canProceed + 
						"): " +
						currResult.toString() 
					  );
					
					/* if no parent testCases available, no sense to iterate */
					if( currResult._1.isEmpty() == false )
					{
						/* get the parent testCase name */
						nextResult =
								 (Tuple2<Option<String>,TestResult>)
								   testDependencies.linkedMap.get( currResult._1.get() ).get();
						/* if the result of the parent test is failure,
						 * we should allow this test */
						canProceed = nextResult._2.getResult();
						
					}
					
				} while
					( /* iterate while there are positive results in predecessor testCases and
					 	 predecessor testCases exist */
						(
							( (iteration > 1) && (currResult._2.getResult() == true) ) ||
							( iteration == 1 )
						) &&
						( currResult._1.isEmpty() == false ) && /* no parent testCases */
						( canProceed == true ) /* check if that still makes sense to
												* proceed (no parent failed testCases) */
					);
				
				System.out.println(">>> proceed:" + canProceed);

				return canProceed;
			}).
			//orElse(Option.of((Boolean)false)).
			get();
				
		return result;
	}
}
