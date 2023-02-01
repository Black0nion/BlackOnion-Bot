package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.common.parse.ParseFactory;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

public class BooleanSetting extends AbstractSetting<Boolean> {
	@SafeVarargs
	public BooleanSetting(String name, Boolean value, Validator<Boolean>... validators) {
		super(name, value, BooleanParser.INSTANCE, validators);
	}

	public static final class BooleanParser implements ParseFactory<Boolean> {

		public static final BooleanParser INSTANCE = new BooleanParser();

		@Override
		public Boolean parseImpl(String input) {
			return switch (input.toLowerCase()) {
				case "true", "yes", "y", "1" -> true;
				case "false", "no", "n", "0" -> false;
				default -> throw new IllegalArgumentException("Invalid boolean value: " + input);
			};
		}

		@Override
		public boolean allowsNull() {
			return false;
		}
	}
}
