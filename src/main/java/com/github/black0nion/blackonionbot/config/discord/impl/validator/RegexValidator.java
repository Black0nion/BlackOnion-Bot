package com.github.black0nion.blackonionbot.config.discord.impl.validator;

import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

public class RegexValidator implements Validator<String> {
	private final String regex;

	public RegexValidator(String regex) {
		this.regex = regex;
	}

	public boolean test(String input) {
		return input.matches(regex);
	}
}
