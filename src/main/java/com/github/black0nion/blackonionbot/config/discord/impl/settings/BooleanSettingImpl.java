package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSettingBuilder;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class BooleanSettingImpl extends AbstractSetting<Boolean> implements BooleanSetting {

	public BooleanSettingImpl(
		SettingsSaver settingsSaver,
		String name,
		Boolean value,
		Set<Permission> permissions,
		Set<CustomPermission> customPermissions
	) {
		super(settingsSaver, name, value, Boolean.class, false, permissions, customPermissions);
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

	public static class Builder extends AbstractSettingBuilder<Boolean, BooleanSetting, Builder> {

		public Builder(SettingsSaver saver, String name) {
			super(saver, name, Boolean.class);
		}

		public Builder(SettingsSaver saver, String name, boolean defaultValue) {
			super(saver, name, Boolean.class);
			this.defaultValue = defaultValue;
		}

		@Override
		public BooleanSetting build() {
			return new BooleanSettingImpl(settingsSaver, name, defaultValue, permissions, customPermissions);
		}
	}
}
