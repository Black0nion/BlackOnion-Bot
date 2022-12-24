package com.github.black0nion.blackonionbot.commands.common.utils.event;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.utils.Placeholder;

public interface TranslationUtils {

	Language getLanguage();

	default String getTranslation(final String key) {
		return getLanguage().getTranslationNonNull(key);
	}

	default String getTranslation(final String key, final Placeholder... placeholders) {
		return Placeholder.process(this.getTranslation(key), placeholders);
	}
}
