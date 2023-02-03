package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LanguageSetting extends AbstractSetting<Language> {

	@NotNull
	private final LanguageSystem languageSystem;

	@SafeVarargs
	public LanguageSetting(String name, LanguageSystem languageSystem, boolean nullable, @Nullable Validator<Language>... validators) {
		super(name, languageSystem.getDefaultLanguage(), Language.class, nullable, validators);
		this.languageSystem = languageSystem;
	}

	@Override
	protected Language parse(@NotNull Object value) throws Exception {
		if (value instanceof Language language) return language;

		return languageSystem.getLanguageFromCode((String) value);
	}

	private static final List<Class<?>> CAN_PARSE = List.of(String.class, Language.class);

	@Override
	public List<Class<?>> canParse() {
		return CAN_PARSE;
	}
}
