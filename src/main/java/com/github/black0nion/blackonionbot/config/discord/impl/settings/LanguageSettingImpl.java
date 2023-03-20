package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSettingBuilder;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class LanguageSettingImpl extends AbstractSetting<Language> implements LanguageSetting {

	@NotNull
	private final LanguageSystem languageSystem;

	public LanguageSettingImpl(
		SettingsSaver saver,
		String name,
		LanguageSystem languageSystem,
		Set<Permission> permissions,
		Set<CustomPermission> customPermissions,
		@Nullable Validator<Language>[] validators
	) {
		super(saver, name, languageSystem.getDefaultLanguage(), Language.class, true, permissions, customPermissions, validators);
		this.languageSystem = languageSystem;
	}

	@Override
	protected Language parse(@NotNull Object value) throws Exception {
		if (value instanceof Language language) return language;

		return languageSystem.getLanguageFromCode((String) value);
	}

	@Override
	public Object toDatabaseValue() {
		return getValue() == null ? null : this.getValue().getLanguageCode();
	}

	@Override
	public Language getOrDefault() {
		return getValue() == null ? languageSystem.getDefaultLanguage() : getValue();
	}

	private static final List<Class<?>> CAN_PARSE = List.of(String.class, Language.class);

	@Override
	public List<Class<?>> canParse() {
		return CAN_PARSE;
	}

	public static class Builder extends AbstractSettingBuilder<Language, LanguageSetting, Builder> {

		private final LanguageSystem languageSystem;

		public Builder(SettingsSaver saver, String name, LanguageSystem languageSystem) {
			super(saver, name, Language.class);
			this.languageSystem = languageSystem;
		}

		@Override
		public LanguageSetting build() {
			return new LanguageSettingImpl(settingsSaver, name, languageSystem, permissions, customPermissions, validators);
		}
	}
}
