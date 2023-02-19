package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSettingBuilder;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ChannelSettingImpl<C extends GuildChannel> extends AbstractSetting<C> implements ChannelSetting<C> {

	private final Supplier<Guild> guildGetter;

	protected ChannelSettingImpl(
		SettingsSaver settingsSaver,
		String name,
		C defaultValue,
		Class<C> type,
		boolean nullable,
		Set<Permission> permissions,
		Set<CustomPermission> customPermissions,
		@Nullable Validator<C>[] validators,
		Supplier<Guild> guildGetter
	) {
		super(settingsSaver, name, defaultValue, type, nullable, permissions, customPermissions, validators);
		this.guildGetter = guildGetter;
	}

	@Override
	protected C parse(@NotNull Object value) throws Exception {
		if (getType().isAssignableFrom(value.getClass())) {
			return getType().cast(value);
		} else if (value instanceof Long l) {
			return guildGetter.get().getChannelById(getType(), l);
		}
		return null;
	}

	private final List<Class<?>> canParse = List.of(Long.class, getType());
	@Override
	public List<Class<?>> canParse() {
		return canParse;
	}

	public static class Builder<C extends GuildChannel> extends AbstractSettingBuilder<C, ChannelSettingImpl<C>, Builder<C>> {
		private final Supplier<Guild> guildGetter;

		public Builder(SettingsSaver settingsSaver, String name, Class<C> type, Supplier<Guild> guildGetter) {
			super(settingsSaver, name, type);
			this.setNullable(true);
			this.guildGetter = guildGetter;
		}

		@Override
		public ChannelSettingImpl<C> build() {
			return new ChannelSettingImpl<>(settingsSaver, name, defaultValue, type, nullable, permissions, customPermissions, validators, guildGetter);
		}
	}
}
