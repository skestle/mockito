/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;
import org.mockito.internal.reporting.PrintSettings;
import org.mockito.internal.verification.DescribeMismatch;

@SuppressWarnings("unchecked")
public class MatchersPrinter {
    
    public String getArgumentsLine(List<Matcher> matchers, PrintSettings printSettings) {
        Description result = new StringDescription();
        result.appendList("(", ", ", ");", applyPrintSettings(matchers, printSettings));
        return result.toString();
    }

    public String getArgumentsBlock(List<Matcher> matchers, PrintSettings printSettings) {
        Description result = new StringDescription();
        result.appendList("(\n    ", ",\n    ", "\n);", applyPrintSettings(matchers, printSettings));
        return result.toString();
    }

    private List<SelfDescribing> applyPrintSettings(List<Matcher> matchers, PrintSettings printSettings) {
        List<SelfDescribing> withPrintSettings = new LinkedList<SelfDescribing>();
        int i = 0;
        for (final Matcher matcher : matchers) {
            if (matcher instanceof ContainsExtraTypeInformation && printSettings.extraTypeInfoFor(i)) {
                withPrintSettings.add(((ContainsExtraTypeInformation) matcher).withExtraTypeInfo());
            } else {
                withPrintSettings.add(matcher);
            }
            i++;
        }
        return withPrintSettings;
    }
    
    
    
    public String getArgumentsLine(List<Matcher> matchers, Object[] mismatchedArgs, PrintSettings printSettings) {
    	if (mismatchedArgs == null) return getArgumentsLine(matchers, printSettings);
        Description result = new StringDescription();
        result.appendList("(", ", ", ");", applyPrintSettings(matchers, mismatchedArgs, printSettings));
        return result.toString();
    }

    public String getArgumentsBlock(List<Matcher> matchers, Object[] mismatchedArgs, PrintSettings printSettings) {
    	if (mismatchedArgs == null) return getArgumentsBlock(matchers, printSettings);
        Description result = new StringDescription();
        result.appendList("(\n    ", ",\n    ", "\n);", applyPrintSettings(matchers, mismatchedArgs, printSettings));
        return result.toString();
    }

    private List<SelfDescribing> applyPrintSettings(List<Matcher> matchers, Object[] mismatchedArgs, PrintSettings printSettings) {
        List<SelfDescribing> withPrintSettings = new LinkedList<SelfDescribing>();
        int i = 0;
        for (Matcher matcher : matchers) {
        	if (i < mismatchedArgs.length) {
        		withPrintSettings.add(new PotentialMismatch(matcher, mismatchedArgs[i], printSettings.extraTypeInfoFor(i)));
        	} else {
        		// if expected argument is missing, the mismatch message is empty
        		// (this is not the same as a null-argument)
        	}
            i++;
        }
    	for (; i < mismatchedArgs.length; i++) {
    		withPrintSettings.add(new UnexpectedArgument(mismatchedArgs[i]));
    	}
        return withPrintSettings;
    }
    
    private static class PotentialMismatch implements SelfDescribing {
    	private final Matcher matcher;
    	private final Object item;
    	private final boolean extraTypeInformation;
    	
		public PotentialMismatch(Matcher matcher, Object arg, boolean extraTypeInformation) {
			this.matcher = matcher;
			this.item = arg;
			this.extraTypeInformation = extraTypeInformation;
		}
		
		private boolean matches() {
			try {
				return matcher.matches(item);
			} catch (ClassCastException e) {
				/** workaround for {@link CustomMatcherDoesYieldCCETest} */
				return false;
			}
		}

		@Override
		public void describeTo(Description description) {
			if (extraTypeInformation && item != null) {
				description.appendText("(");
				description.appendText(item.getClass().getSimpleName());
				description.appendText(") ");
			}
			if (matches()) {
				DescribeMismatch.appendQuoted(item, description);
			} else {
				DescribeMismatch.describeMismatch(matcher, item, description);
			}
		}    	
    }
    
    private static class UnexpectedArgument implements SelfDescribing {
    	private final Object item;
    	
    	public UnexpectedArgument(Object item) {
    		this.item = item;
    	}
    	
    	@Override
		public void describeTo(Description description) {
    		DescribeMismatch.appendQuoted(item, description);
    	}
    }
    
}