/*
 * Author: Lev Vanyan
 * Lev.Vanyan@forthreal.com
 */

package com.forthreal.conditionaljunit5tester.classes;

public class TestResult
{
	private Boolean tested = false;
	private Boolean result = false;

	public void reset()
	{
		tested = false;
		result = false;
	}
	
	/* result management */
	public Boolean getResult()
	{
		return result;
	}

	/* result TestResult to be able to chain this call */
	public TestResult setResult(Boolean result)
	{
		this.result = result;
		
		return this;
	}
	
	/* test field management */
	public Boolean getTested()
	{
		return tested;
	}
	
	/* result TestResult to be able to chain this call */
	public TestResult setTested(Boolean tested)
	{
		this.tested = tested;
		
		return this;
	}
}
