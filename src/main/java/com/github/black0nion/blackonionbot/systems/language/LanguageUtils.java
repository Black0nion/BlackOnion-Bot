package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LanguageUtils {

	private LanguageUtils() {}

	public static Language getLanguage(@Nullable final UserSettings userSettings, @Nullable final GuildSettings guildSettings, @Nonnull Language defaultLanguage) {
		if (userSettings != null) {
			final Language userLang = userSettings.getLanguage().getValue();
			if (userLang != null) return userLang;
		}

		if (guildSettings != null) {
			final Language guildLang = guildSettings.getLanguage().getValue();
			if (guildLang != null) return guildLang;
		}
		return defaultLanguage;
	}
}
