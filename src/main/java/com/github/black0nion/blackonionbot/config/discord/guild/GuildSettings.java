package com.github.black0nion.blackonionbot.config.discord.guild;

import com.github.black0nion.blackonionbot.commands.common.NamedCommand;
import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.*;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

@SuppressWarnings("CheckStyle")
public interface GuildSettings extends SettingsContainer, DisabledCommandsHelper {

	String TABLE_NAME = "guild_settings";

	@SQLSetup
	static void setup(SQLHelperFactory sqlHelperFactory) throws SQLException {
		try (SQLHelper helper = sqlHelperFactory.create("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
			" (identifier BIGINT PRIMARY KEY," +
			"language VARCHAR(5)," +
			"FOREIGN KEY (language) REFERENCES language (code)," +

			"join_message_activated BOOLEAN," +
			"join_image_activated BOOLEAN," +
			"join_message VARCHAR(500)," +

			"leave_message_activated BOOLEAN," +
			"leave_image_activated BOOLEAN," +
			"leave_message VARCHAR(500)," +

			"guild_type VARCHAR(20)," +
			"anti_spoiler VARCHAR(20)," +
			"disabled_commands VARCHAR(500)," +
			"suggestions_channel BIGINT" +
			")");
			 PreparedStatement ps = helper.create()) {
			ps.executeUpdate();
		}
	}

	LanguageSetting getLanguage();

	BooleanSetting joinMessageActivated();
	BooleanSetting joinImageActivated();
	StringSetting getJoinMessage();

	BooleanSetting leaveMessageActivated();
	BooleanSetting leaveImageActivated();
	StringSetting getLeaveMessage();

	EnumSetting<GuildType> getGuildType();
	EnumSetting<AntiSpoilerSystem.AntiSpoilerType> getAntiSpoiler();
	ListSetting<NamedCommand, Set<NamedCommand>> getDisabledCommands();
}
