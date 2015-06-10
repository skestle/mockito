package org.mockito.internal.verification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.internal.matchers.ArrayEquals;
import org.mockito.internal.matchers.Equals;

public class DescribeMismatch {

	private static Method mDescribeMismatch(Class<?> clazz) throws NoSuchMethodException, SecurityException {
		return clazz.getMethod("describeMismatch", Object.class, Description.class);
	}
	
	/**
	 * Invokes {@code describeMismatch(Object, Description)} if exists.
	 * @param matcher
	 * @param item
	 * @param description
	 */
	public static void describeMismatch(Matcher matcher, Object item, Description description) {
		try {
			Method m = mDescribeMismatch(matcher.getClass());
			m.invoke(matcher, item, description);
			return;
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) throw (RuntimeException) cause;
			if (cause instanceof Error) throw (Error) cause;
			throw new RuntimeException(cause);
		} catch (Exception e) {
			appendQuoted(item, description);
		}
	}
	
	public static void appendQuoted(Object item, Description description) {
		// use quoting as defined by existing matchers
		if (item != null && item.getClass().isArray()) {
			new ArrayEquals(item).describeMismatch(item, description);
		} else {
			new Equals(item).describeMismatch(item, description);
		}
	}
	
}
