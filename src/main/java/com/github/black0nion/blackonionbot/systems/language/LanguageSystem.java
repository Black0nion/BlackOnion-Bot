package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;

import javax.annotation.Nullable;
import java.util.Map;

public interface LanguageSystem {
	Map<String, Language> getLanguages();

	default Language getLanguage(@Nullable UserSettings userSettings, @Nullable GuildSettings guildSettings) {
		return LanguageUtils.getLanguage(userSettings, guildSettings, getDefaultLanguage());
	}

	Language getDefaultLanguage();

	@Nullable
	Language getLanguageFromCode(String name);

	String getTranslation(@Nullable String key, @Nullable UserSettings userSettings, @Nullable GuildSettings guildSettings);

	String getLanguageString();
}
