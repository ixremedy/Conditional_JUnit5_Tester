/*
 * Author: Lev Vanyan
 * Lev.Vanyan@forthreal.com
 */

package com.forthreal.conditionaljunit5tester;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;

import com.forthreal.conditionaljunit5tester.Application;
import com.forthreal.conditionaljunit5tester.classes.TestWatcherExtension;
import com.forthreal.conditionaljunit5tester.classes.TestExecutionCondition;

@ExtendWith(TestWatcherExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationTest
{
	@BeforeAll
	public static void setup()
	{
	}
	
    @Test
    @Order(0)
    @DisplayName("Check if we get a throw")
    @ExtendWith(TestExecutionCondition.class)
    public void canCreate()
    {
    	
    	assertDoesNotThrow( () -> {
			
		} );

    }

    @Test
    @Order(1)
    @DisplayName("Check if we get a negative result")
    @ExtendWith(TestExecutionCondition.class)
    public void canConnect()
    {
    	assertFalse( false );
    }
}
