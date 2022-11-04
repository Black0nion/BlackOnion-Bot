package com.github.black0nion.blackonionbot.utils;

public class Placeholder {

	private final String key;
	private final String value;

	/**
	 * The value gets escaped automatically!
	 */
	public Placeholder(final String key, final String value) {
		this.key = "%" + key + "%";
		this.value = value;
	}

	/**
	 * The value gets escaped automatically!
	 */
	public Placeholder(final String key, final Object value) {
		this.key = "%" + key + "%";
		this.value = value.toString();
	}

	public String process(final String input) {
		if (input == null || input.length() == 0) return input;
		if (this.key == null) return input;

		return input.replace(this.key, this.value != null ? Utils.escapeMarkdown(this.value) : "");
	}

	public static String process(String input, final Placeholder... placeholders) {
		if (input == null || input.length() == 0) {
			return input;
		}
		for (final Placeholder placeholder : placeholders) {
			input = placeholder.process(input);
		}
		return input;
	}
}
