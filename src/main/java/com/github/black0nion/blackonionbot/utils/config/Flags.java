package com.github.black0nion.blackonionbot.utils.config;

import java.util.regex.Pattern;

public class Flags {
	public static final IFlag NonNull = new IFlag() {
		@Override
		public int hashCode() {
			return 20;
		}
	};

	public static abstract class MatchesRegex implements IFlag {
		abstract Pattern regex();
	}
	public static MatchesRegex matchesRegex(String regex) {
		return new MatchesRegex() {
			@Override
			public Pattern regex() {
				return Pattern.compile(regex);
			}
		};
	}

	public static MatchesRegex matchesRegex(Pattern regex) {
		return new MatchesRegex() {
			@Override
			public Pattern regex() {
				return regex;
			}
		};
	}

	public static abstract class Range implements IFlag {
		abstract double min();
		abstract double max();
	}

	public static Range range(double min, double max) {
		return new Range() {
			@Override
			public double min() {
				return min;
			}
			@Override
			public double max() {
				return max;
			}
		};
	}

	public static abstract class Default<T> implements IFlag {
		abstract T defaultValue();
	}
	public static <T> Default<T> defaultValue(T defaultValue) {
		return new Default<>() {
			@Override
			public T defaultValue() {
				return defaultValue;
			}
		};
	}
}