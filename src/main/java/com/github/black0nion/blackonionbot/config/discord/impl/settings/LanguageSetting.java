package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.utils.Placeholder;

public interface LanguageSetting extends Setting<Language> {
	default String getTranslation(String key) {
		return getValue().getTranslation(key);
	}

	default String getTranslation(String key, Placeholder... args) {
		return getValue().getTranslation(key, args);
	}

	Language getOrDefault();
}
