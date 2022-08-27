package com.github.black0nion.blackonionbot.tests.junit;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class JUnitDisplayNameGenerator implements DisplayNameGenerator {
	@Override
	public String generateDisplayNameForClass(Class<?> testClass) {
		return testClass.getSimpleName();
	}

	@Override
	public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
		return nestedClass.getSimpleName();
	}

	@Override
	public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
		return DisplayNameGenerator.getDisplayNameGenerator(ReplaceUnderscores.class).generateDisplayNameForMethod(testClass, testMethod);
	}
}