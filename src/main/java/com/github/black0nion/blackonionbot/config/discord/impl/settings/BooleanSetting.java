package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.common.parse.ParseFactory;
import com.github.black0nion.blackonionbot.config.common.parse.ParseFactoryImpl;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

public class BooleanSetting extends AbstractSetting<Boolean> {

	public static final ParseFactory<String, Boolean> PARSER = new ParseFactoryImpl<>(Boolean.class, input -> switch (input.toLowerCase()) {
		case "true", "yes", "y", "1" -> true;
		case "false", "no", "n", "0" -> false;
		default -> throw new IllegalArgumentException("Invalid boolean value: " + input);
	});

	@SafeVarargs
	public BooleanSetting(String name, Boolean value, Validator<Boolean>... validators) {
		super(name, value, parsers(PARSER), validators); // NOSONAR what do you want from me
	}
}
