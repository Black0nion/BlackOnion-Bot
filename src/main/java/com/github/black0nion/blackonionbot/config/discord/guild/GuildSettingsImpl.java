package com.github.black0nion.blackonionbot.config.discord.guild;

import com.github.black0nion.blackonionbot.bot.CommandRegistry;
import com.github.black0nion.blackonionbot.commands.common.NamedCommand;
import com.github.black0nion.blackonionbot.config.discord.api.container.AbstractSettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.*;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.LongFunction;

public class GuildSettingsImpl extends AbstractSettingsContainer<Guild> implements GuildSettings {

	private final LanguageSetting language;


	private final BooleanSetting joinMessageActivated = addSetting(
		new BooleanSettingImpl.Builder(settingsSaver, "join_message_activated", false));
	private final BooleanSetting joinImageActivated = addSetting(
		new BooleanSettingImpl.Builder(settingsSaver, "join_image_activated", false));
	private final StringSetting joinMessage = addSetting(
		new StringSettingImpl.Builder(settingsSaver, "join_message", "Welcome %user% to %guild%!"));


	private final BooleanSetting leaveMessageActivated = addSetting(
		new BooleanSettingImpl.Builder(settingsSaver, "leave_message_activated", false));
	private final BooleanSetting leaveImageActivated = addSetting(
		new BooleanSettingImpl.Builder(settingsSaver, "leave_image_activated", false));
	private final StringSetting leaveMessage = addSetting(
		new StringSettingImpl.Builder(settingsSaver, "leave_message", "Goodbye %user%!"));


	private final EnumSetting<GuildType> guildType = addSetting(
		new EnumSettingImpl.Builder<>(settingsSaver, "guild_type", GuildType.class)
			.defaultValue(GuildType.NORMAL)
			.customPermissions(CustomPermission.ADMIN));
	private final EnumSetting<AntiSpoilerSystem.AntiSpoilerType> antiSpoiler = addSetting(
		new EnumSettingImpl.Builder<>(settingsSaver, "anti_spoiler", AntiSpoilerSystem.AntiSpoilerType.class)
			.defaultValue(AntiSpoilerSystem.AntiSpoilerType.OFF)
			.permissions(Permission.ADMINISTRATOR));
	private final ListSetting<NamedCommand, Set<NamedCommand>> disabledCommands;

	public GuildSettingsImpl(long id, LongFunction<Guild> guildGetter, ResultSet resultSet, SQLHelperFactory helperFactory, LanguageSystem languageSystem, CommandRegistry commandRegistry) throws Exception {
		super(TABLE_NAME, id, guildGetter, helperFactory);

		language = addSetting(new LanguageSettingImpl.Builder(settingsSaver, "language", languageSystem));
		disabledCommands = addSetting(
			new ListSettingImpl.Builder<NamedCommand, Set<NamedCommand>>(settingsSaver, "disabled_commands", Set.class, commandRegistry::getCommand, NamedCommand::getName)
				.defaultValue(new HashSet<>())
				.permissions(Permission.ADMINISTRATOR)
		);

		this.loadSettings(resultSet);
	}

	@NotNull
	@Override
	public LanguageSetting getLanguage() {
		return language;
	}

	@Override
	public BooleanSetting joinMessageActivated() {
		return joinMessageActivated;
	}

	@Override
	public BooleanSetting joinImageActivated() {
		return joinImageActivated;
	}

	@Override
	public StringSetting getJoinMessage() {
		return joinMessage;
	}

	@Override
	public BooleanSetting leaveMessageActivated() {
		return leaveMessageActivated;
	}

	@Override
	public BooleanSetting leaveImageActivated() {
		return leaveImageActivated;
	}

	@Override
	public StringSetting getLeaveMessage() {
		return leaveMessage;
	}

	@Override
	public EnumSetting<GuildType> getGuildType() {
		return guildType;
	}

	@Override
	public EnumSetting<AntiSpoilerSystem.AntiSpoilerType> getAntiSpoiler() {
		return antiSpoiler;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ListSetting<NamedCommand, Set<NamedCommand>> getDisabledCommands() {
		return disabledCommands;
	}
}
