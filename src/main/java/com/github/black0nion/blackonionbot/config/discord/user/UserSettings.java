package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.LanguageSetting;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.ListSetting;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public interface UserSettings extends SettingsContainer {

	String TABLE_NAME = "user_settings";

	@SQLSetup
	static void setup(SQLHelperFactory sqlHelperFactory) throws SQLException {
		try (SQLHelper helper = sqlHelperFactory.create("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (identifier BIGINT PRIMARY KEY, " +
				"language VARCHAR(5), " +
				"FOREIGN KEY (language) REFERENCES language (code), " +
				"permissions VARCHAR(500)" +
				")");
			 PreparedStatement ps = helper.create()) {
			ps.executeUpdate();
		}
	}


	@Nonnull
	LanguageSetting getLanguage();

	@Nonnull
	ListSetting<CustomPermission, Set<CustomPermission>> getPermissions();
}
