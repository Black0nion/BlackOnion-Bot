package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.misc.SQLSetup;

import java.lang.reflect.Method;

public class SQLSetupComparator implements java.util.Comparator<Method> {

	public static final SQLSetupComparator INSTANCE = new SQLSetupComparator();

	@Override
	public int compare(Method m1, Method m2) {
		SQLSetup o1 = m1.getAnnotation(SQLSetup.class);
		SQLSetup o2 = m2.getAnnotation(SQLSetup.class);
		// sort the two based on the "after" attribute
		// if o1 is after o2, return 1
		// if o2 is after o1, return -1
		if (o1.after().length == 0 && o2.after().length == 0) {
			return 0;
		} else if (o1.after().length == 0) {
			return -1;
		} else if (o2.after().length == 0) {
			return 1;
		} else {
			for (Class<?> c : o1.after()) {
				if (c.equals(m2.getDeclaringClass())) {
					return 1;
				}
			}
			for (Class<?> c : o2.after()) {
				if (c.equals(m1.getDeclaringClass())) {
					return -1;
				}
			}
			return 0;
		}
	}
}
