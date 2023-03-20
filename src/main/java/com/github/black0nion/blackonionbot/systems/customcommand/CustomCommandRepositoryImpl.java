package com.github.black0nion.blackonionbot.systems.customcommand;

import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;

import java.util.Map;

// TODO: implement
public class CustomCommandRepositoryImpl implements CustomCommandRepository {
	private final SQLHelperFactory sqlHelperFactory;

	public CustomCommandRepositoryImpl(SQLHelperFactory sqlHelperFactory) {
		this.sqlHelperFactory = sqlHelperFactory;
	}

	@Override
	public Map<String, CustomCommand> getCustomCommands(long guildId) {
		return null;
	}
}
