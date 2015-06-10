/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.reporting.PrintSettings;
import org.mockito.internal.reporting.SmartPrinter;
import org.mockito.invocation.Invocation;
import org.mockitoutil.TestBase;

@SuppressWarnings("unchecked")
public class MatchersPrinterTest extends TestBase {

    MatchersPrinter printer = new MatchersPrinter();

    @Test
    public void shouldGetArgumentsLine() {
        String line = printer.getArgumentsLine((List) Arrays.asList(new Equals(1), new Equals(2)), new PrintSettings());
        assertEquals("(1, 2);", line);
    }

    @Test
    public void shouldGetArgumentsBlock() {
        String line = printer.getArgumentsBlock((List) Arrays.asList(new Equals(1), new Equals(2)), new PrintSettings());
        assertEquals("(\n    1,\n    2\n);", line);
    }

    @Test
    public void shouldDescribeTypeInfoOnlyMarkedMatchers() {
        //when
        String line = printer.getArgumentsLine((List) Arrays.asList(new Equals(1L), new Equals(2)), PrintSettings.verboseMatchers(1));
        //then
        assertEquals("(1, (Integer) 2);", line);
    }

    @Test
    public void shouldGetVerboseArgumentsInBlock() {
        //when
        String line = printer.getArgumentsBlock((List) Arrays.asList(new Equals(1L), new Equals(2)), PrintSettings.verboseMatchers(0, 1));
        //then
        assertEquals("(\n    (Long) 1,\n    (Integer) 2\n);", line);
    }

    @Test
    public void shouldGetVerboseArgumentsEvenIfSomeMatchersAreNotVerbose() {
        //when
        String line = printer.getArgumentsLine((List) Arrays.asList(new Equals(1L), NotNull.NOT_NULL), PrintSettings.verboseMatchers(0));
        //then
        assertEquals("((Long) 1, notNull());", line);
    }
    
    @Test
    public void shouldPrintSimpleExpectedValue() {
    	// setup
    	String line = printer.getArgumentsLine((List) Arrays.asList(new Equals(11)), new Object[]{12}, new PrintSettings());
        //then
        assertEquals("(12);", line);
    }

    @Test
    public void shouldPrintMismatchDescription() {
    	// setup
    	String line = printer.getArgumentsLine((List) Arrays.asList(IS_PRIME), new Object[]{12}, new PrintSettings());
        //then
        assertContains("multiple of 2, 3, 4", line);
    }
    
    private static final PrimeMatcher IS_PRIME = new PrimeMatcher();
    
    public static class PrimeMatcher extends BaseMatcher<Integer> {

		@Override
		public void describeTo(Description description) {
			description.appendText("prime");
			BaseMatcher<Integer> m = null;
		}

		@Override
		public boolean matches(Object item) {
			if (!(item instanceof Integer)) return false;
			final int value = (Integer) item;
			for (int i = 2; i < value; i++) {
				if (value % i == 0) return false;
			}
			return true;
		}
		
		public void describeMismatch(Object item, Description description) {
			if (!(item instanceof Integer)) {
				description.appendValue(item);
				return;
			}
			final int value = (Integer) item;
			description.appendText(String.valueOf(item));
			description.appendText("(multiple of ");
			int count = 0;
			for (int i = 2; i < value; i++) {
				if (value % i == 0) {
					if (count == 3) {
						description.appendText(", ...");
						break;
					}
					if (count > 0) {
						description.appendText(", ");
					}
					description.appendText(String.valueOf(i));
					count++;
				}
			}
			description.appendText(")");
		}
    }
}