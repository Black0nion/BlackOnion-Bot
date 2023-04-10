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
import java.util.Set;

public class EnumSettingImpl<E extends Enum<E>> extends AbstractSetting<E> implements EnumSetting<E> {

	@SafeVarargs
	public EnumSettingImpl(SettingsSaver settingsSaver,
		String name,
		E defaultValue,
		Class<E> type,
		boolean nullable,
		Set<Permission> permissions,
		Set<CustomPermission> customPermissions,
		@Nullable Validator<E>... validators)
	{
		super(settingsSaver, name, defaultValue, type, nullable, permissions, customPermissions, validators);
	}

	@Override
	protected E parse(@NotNull Object value) throws Exception {
		// check if value is already the right enum
		if (value instanceof Enum e && e.getClass() == getType()) {
			//noinspection unchecked
			return (E) value;
		}

		if (value instanceof String str) {
			return Enum.valueOf(getType(), str);
		}
		return null;
	}

	@Override
	public Object toDatabaseValue() {
		return getValue() == null ? null : getValue().name();
	}

	private final List<Class<?>> canParse = List.of(String.class, getType());

	@Override
	public List<Class<?>> canParse() {
		return canParse;
	}

	public static class Builder<E extends Enum<E>> extends AbstractSettingBuilder<E, EnumSetting<E>, Builder<E>> {

		public Builder(SettingsSaver saver, String name, Class<E> type) {
			super(saver, name, type);
		}

		@Override
		public EnumSetting<E> build() {
			return new EnumSettingImpl<>(settingsSaver, name, defaultValue, type, nullable, permissions, customPermissions, validators);
		}
	}
}
