package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.LanguageSetting;

import javax.annotation.Nonnull;

public interface UserSettings extends SettingsContainer {
	@Nonnull
	LanguageSetting getLanguage();
}
