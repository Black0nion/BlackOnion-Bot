package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import com.github.black0nion.blackonionbot.utils.Placeholder;

public interface StringSetting extends Setting<String> {
	default String getValue(Placeholder... placeholders) {
		return Placeholder.process(getValue(), placeholders);
	}
}
