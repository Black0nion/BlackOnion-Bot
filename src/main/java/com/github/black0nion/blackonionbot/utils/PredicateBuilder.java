package com.github.black0nion.blackonionbot.utils;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PredicateBuilder implements Predicate<String> {
	private int minLength = 0;
	private int maxLength = Integer.MAX_VALUE;
	private Pattern pattern;

	public PredicateBuilder() {}

	public PredicateBuilder(int minLength, int maxLength, Pattern pattern) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.pattern = pattern;
	}

	public PredicateBuilder minLength(int minLength) {
		this.minLength = minLength;
		return this;
	}

	public PredicateBuilder maxLength(int maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public PredicateBuilder pattern(Pattern pattern) {
		this.pattern = pattern;
		return this;
	}

	public static Predicate<String> ofPattern(String pattern) {
		return ofPattern(Pattern.compile(pattern));
	}

	public static Predicate<String> ofPattern(Pattern pattern) {
		return s -> pattern.matcher(s).matches();
	}

	public Predicate<String> build() {
		return (String s) -> {
			if (s.length() < minLength) return false;
			if (s.length() > maxLength) return false;
			if (pattern != null) return pattern.matcher(s).matches();
			return true;
		};
	}

	@Override
	public boolean test(String s) {
		return build().test(s);
	}
}