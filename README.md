# Conditional_JUnit5_Tester
(c) Lev Vanyan, Forthreal

A little framework that enables the user to define a list of dependant tests cases that won't be executed if the parent test cases failed.

I am developing this simple framework on a need to make certain JUnit tests dependent on other tests.

I'll keep improving this till that suits my original purpose :)

This is how skipping of tests looks like - [see picture](https://drive.google.com/open?id=1OEDz8DWXDkro9Klq-ppu4GbRk_ndMoQE)


This is how you define a chain of tests. The first one comes with no dependency, the other three depend on the first one

```java
			tracker =
				new TestDependencyTracker
				  (
					new TestDependencyTracker.TestDependencyBuilder().
						  build().
						  addMethod
						    (
						      "ApplicationTest.canCreate()",
						      Option.none()
						    ).
						  addMethod
						    (
						      "ApplicationTest.canConnect()",
						      Option.of("ApplicationTest.canCreate()")
						    ).
						  addMethod
						    (
						      "ApplicationTest.canGetListOfPorts()",
						      Option.of("ApplicationTest.canCreate()")
						    ).
						  addMethod
						    (
							  "ApplicationTest.canGetPortChangeHistory()",
							  Option.of("ApplicationTest.canCreate()")
						    ).
						  get()
				  );

```

This is how the framework decides (based on the previous test results) whether a test would be executed:

```java
	/* the following will be called before each test annotated with @ExtendWith */
	@Override
	public ConditionEvaluationResult
		evaluateExecutionCondition(ExtensionContext context)
	{
		String functionName = getClassFunction( context.getTestMethod().get().toString() ); 
		
		Boolean isEnabled = tracker.isTestBranchEnabled( functionName );

		return (isEnabled == true) ?
					ConditionEvaluationResult.enabled( "Message - enabled") :
					ConditionEvaluationResult.disabled( "Message - skipping");
	}
```

The framework uses JUnit5's TestWatcher class (check out TestWatcherExtension.class) like what follows below:

```java
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
```

