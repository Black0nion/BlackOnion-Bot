package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.StringSetting;

public interface UserSettings extends SettingsContainer {
	StringSetting getJoinMessage();
}
