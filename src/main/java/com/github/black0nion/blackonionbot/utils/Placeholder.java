package com.github.black0nion.blackonionbot.utils;

public class Placeholder {

	private final String placeholder;
	private final String value;

	public Placeholder(final String key, final String value) {
		this.placeholder = "%" + key + "%";
		this.value = value;
	}

	public Placeholder(final String key, final Object value) {
		this.placeholder = "%" + key + "%";
		this.value = value.toString();
	}

	public String process(final String input) {
		return input != null && input.length() > 0 ? input.replace(this.placeholder, this.value) : null;
	}

	public static String process(String input, final Placeholder... placeholders) {
		if (input == null || input.length() == 0) {
			return null;
		}
		for (final Placeholder placeholder : placeholders) {
			input = placeholder.process(input);
		}
		return input;
	}
}
