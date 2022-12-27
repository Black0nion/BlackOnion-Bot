package com.github.black0nion.blackonionbot.config.common;

import java.util.regex.Pattern;

public class Flags {
	public static final ConfigFlag NonNull = new ConfigFlag() {
		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}

		@Override
		public int hashCode() {
			return 20;
		}
	};

	public interface MatchesRegex extends ConfigFlag {
		Pattern regex();
	}

	public static MatchesRegex matchesRegex(String regex) {
		return () -> Pattern.compile(regex);
	}

	public static MatchesRegex matchesRegex(Pattern regex) {
		return () -> regex;
	}

	@SuppressWarnings("CheckStyle")
	public interface Range extends ConfigFlag {
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

	public interface Default<T> extends ConfigFlag {
		T defaultValue();
	}

	public static <T> Default<T> defaultValue(T defaultValue) {
		return () -> defaultValue;
	}
}
