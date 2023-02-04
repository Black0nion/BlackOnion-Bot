package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;

import javax.annotation.Nonnull;
import java.util.List;

public class BooleanSetting extends AbstractSetting<Boolean> {

	public BooleanSetting(SettingsContainer container, String name, Boolean value) {
		super(container, name, value, Boolean.class, false);
	}

	@Override
	protected Boolean parse(@Nonnull Object value) throws Exception {
		if (value instanceof Boolean bool) return bool;

		return switch (((String) value).toLowerCase()) {
			case "true" -> true;
			case "false" -> false;
			default -> throw new IllegalArgumentException("Invalid boolean value: " + value);
		};
	}

	@Override
	public Object toDatabaseValue() {
		return getValue();
	}

	private static final List<Class<?>> CAN_PARSE = List.of(String.class, Boolean.class);

	@Override
	public List<Class<?>> canParse() {
		return CAN_PARSE;
	}
}
