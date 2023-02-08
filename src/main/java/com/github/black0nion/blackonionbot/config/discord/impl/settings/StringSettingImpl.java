package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSettingBuilder;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringSettingImpl extends AbstractSetting<String> implements StringSetting {

	@SafeVarargs
	public StringSettingImpl(SettingsSaver settingsSaver, String name, String defaultValue, Permission[] permissions, CustomPermission[] customPermissions, @Nullable Validator<String>... validators) {
		super(settingsSaver, name, defaultValue, String.class, false, permissions, customPermissions, validators);
	}

	@SafeVarargs
	public StringSettingImpl(SettingsSaver settingsSaver,
		String name,
		String defaultValue,
		boolean nullable,
		Permission[] permissions,
		CustomPermission[] customPermissions,
		@Nullable Validator<String>... validators
	) {
		super(settingsSaver, name, defaultValue, String.class, nullable, permissions, customPermissions, validators);
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

	public static class Builder extends AbstractSettingBuilder<String, StringSetting, Builder> {

		public Builder(SettingsSaver saver, String name) {
			super(saver, name, String.class);
		}

		public Builder(SettingsSaver saver, String name, String defaultValue) {
			super(saver, name, String.class);
			this.defaultValue = defaultValue;
		}

		@Override
		public StringSetting build() {
			return new StringSettingImpl(settingsSaver, name, defaultValue, nullable, permissions, customPermissions, validators);
		}
	}
}
