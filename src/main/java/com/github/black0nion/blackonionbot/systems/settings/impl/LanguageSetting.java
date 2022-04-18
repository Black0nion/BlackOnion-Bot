package com.github.black0nion.blackonionbot.systems.settings.impl;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.settings.ConsumerCancellable;
import com.github.black0nion.blackonionbot.systems.settings.Setting;

import java.util.function.Consumer;

public class LanguageSetting extends Setting<Language> {

	public LanguageSetting(String name, String descriptionKey, Language defaultValue, Consumer<Language> onChanged, ConsumerCancellable<Language> preChanged, boolean nullable) {
		super(name, descriptionKey, defaultValue, onChanged, preChanged, nullable);
	}

	@Override
	protected Language parse(Object value) throws IllegalArgumentException {
		if (value instanceof Language lang) return lang;
		if (value instanceof String str) return LanguageSystem.getLanguageFromName(str);
		throw new IllegalArgumentException("Invalid language: " + value);
	}

	public static class Builder extends SettingBuilder<Builder, Language, LanguageSetting> {

		public Builder() {
			this(null);
		}

		public Builder(String name) {
			this.name = name;
			this.nullable = true;
		}

		@Override
		protected LanguageSetting buildImpl() {
			return new LanguageSetting(name, descriptionKey, defaultValue, onChanged, preChanged, nullable);
		}
	}
}