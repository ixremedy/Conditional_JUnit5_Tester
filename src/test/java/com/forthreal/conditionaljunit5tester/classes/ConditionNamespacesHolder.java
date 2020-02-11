/*
 * Author: Lev Vanyan
 * Lev.Vanyan@forthreal.com
 */

package com.forthreal.conditionaljunit5tester.classes;

import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

public class ConditionNamespacesHolder
{
	public enum TestResultStatus
	{
        SUCCESSFUL, ABORTED, FAILED, DISABLED;
    }
	
	public static final Namespace ApplicationTestNamespace =
			Namespace.create( "com", "forthreal", "conditionaljunit5tester", "ApplicationTest" ); 

}
