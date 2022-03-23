package com.github.black0nion.blackonionbot.utils.config;

import java.util.regex.Pattern;

public class Flags {
	public static final IFlag NonNull = new IFlag() {
		@Override
		public int hashCode() {
			return 20;
		}
	};

	public interface MatchesRegex extends IFlag {
		Pattern regex();
	}
	public static MatchesRegex matchesRegex(String regex) {
		return () -> Pattern.compile(regex);
	}

	public static MatchesRegex matchesRegex(Pattern regex) {
		return () -> regex;
	}

	public interface Range extends IFlag {
		double min();
		double max();
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

	public interface Default<T> extends IFlag {
		T defaultValue();
	}
	public static <T> Default<T> defaultValue(T defaultValue) {
		return () -> defaultValue;
	}
}