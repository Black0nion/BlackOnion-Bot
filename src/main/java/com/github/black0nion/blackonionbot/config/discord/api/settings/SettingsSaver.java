package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.utils.ThrowableConsumer;

public interface SettingsSaver extends ThrowableConsumer<Setting<?>, SettingSaveException> {
}
