package com.github.black0nion.blackonionbot.config.discord.api;

import java.sql.ResultSet;

public interface SettingsFactory<T extends SettingsContainer> {
	T createSettings(long id, ResultSet resultSet);
}
