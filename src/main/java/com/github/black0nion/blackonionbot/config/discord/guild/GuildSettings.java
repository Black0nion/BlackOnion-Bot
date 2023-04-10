package com.github.black0nion.blackonionbot.config.discord.guild;

import com.github.black0nion.blackonionbot.commands.common.Command;
import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.*;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

@SuppressWarnings("CheckStyle")
public interface GuildSettings extends SettingsContainer<Guild>, DisabledCommandsHelper {

	String TABLE_NAME = "guild_settings";

	@SQLSetup
	static void setup(SQLHelperFactory sqlHelperFactory) throws SQLException {
		try (SQLHelper helper = sqlHelperFactory.create("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
			" (identifier BIGINT PRIMARY KEY," +
			"language VARCHAR(5)," +
			"FOREIGN KEY (language) REFERENCES language (code)," +

			"join_message_activated BOOLEAN," +
			"join_channel BIGINT," +
			"join_image_activated BOOLEAN," +
			"join_message VARCHAR(500)," +

			"leave_message_activated BOOLEAN," +
			"leave_channel BIGINT," +
			"leave_image_activated BOOLEAN," +
			"leave_message VARCHAR(500)," +

			"guild_type VARCHAR(20)," +
			"anti_spoiler VARCHAR(20)," +
			"disabled_commands VARCHAR(500)," +
			"suggestions_channel BIGINT," +

			"auto_roles VARCHAR(500)" +
			")");
			 PreparedStatement ps = helper.create()) {
			ps.executeUpdate();
		}
	}

	LanguageSetting getLanguage();

	BooleanSetting joinMessageActivated();
	ChannelSetting<TextChannel> getJoinChannel();
	BooleanSetting joinImageActivated();
	StringSetting getJoinMessage();

	BooleanSetting leaveMessageActivated();
	ChannelSetting<TextChannel> getLeaveChannel();
	BooleanSetting leaveImageActivated();
	StringSetting getLeaveMessage();

	EnumSetting<GuildType> getGuildType();
	EnumSetting<AntiSpoilerSystem.AntiSpoilerType> getAntiSpoiler();
	ListSetting<Command, Set<Command>> getDisabledCommands();

	ListSetting<Long, Set<Long>> getAutoRoles();

	ChannelSetting<TextChannel> getSuggestionsChannel();
}