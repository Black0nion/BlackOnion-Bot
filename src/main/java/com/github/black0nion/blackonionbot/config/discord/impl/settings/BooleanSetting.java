package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;

import javax.annotation.Nonnull;
import java.util.List;

public class BooleanSetting extends AbstractSetting<Boolean> {

	public BooleanSetting(SettingsSaver settingsSaver, String name, Boolean value) {
		super(settingsSaver, name, value, Boolean.class, false);
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

	private static final List<Class<?>> CAN_PARSE = List.of(String.class, Boolean.class);

	@Override
	public List<Class<?>> canParse() {
		return CAN_PARSE;
	}
}
