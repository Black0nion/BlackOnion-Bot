package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringSetting extends AbstractSetting<String> {

	@SafeVarargs
	public StringSetting(SettingsSaver settingsSaver, String name, String defaultValue, @Nullable Validator<String>... validators) {
		super(settingsSaver, name, defaultValue, String.class, false, validators);
	}

	@SafeVarargs
	public StringSetting(SettingsSaver settingsSaver, String name, String defaultValue, boolean nullable, @Nullable Validator<String>... validators) {
		super(settingsSaver, name, defaultValue, String.class, nullable, validators);
	}

	@Override
	protected String parse(@NotNull Object value) throws Exception {
		return (String) value;
	}

	private static final List<Class<?>> CAN_PARSE = List.of(String.class);

	@Override
	public List<Class<?>> canParse() {
		return CAN_PARSE;
	}
}
