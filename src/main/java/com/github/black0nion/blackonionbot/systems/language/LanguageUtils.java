package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LanguageUtils {

	private LanguageUtils() {}

	public static Language getLanguage(@Nullable final BlackUser author, @Nullable final BlackGuild guild, @Nonnull Language defaultLanguage) {
		if (author != null) {
			final Language userLang = author.getLanguage();
			if (userLang != null) return userLang;
		}
		if (guild != null) {
			final Language guildLang = guild.getLanguage();
			if (guildLang != null) return guildLang;
		}
		return defaultLanguage;
	}
}
