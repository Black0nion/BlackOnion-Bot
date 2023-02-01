package com.github.black0nion.blackonionbot.config.discord.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.black0nion.blackonionbot.config.discord.api.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.api.SettingsFactory;
import com.github.black0nion.blackonionbot.config.discord.api.SettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.exception.SettingsLoadingException;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class SettingsRepoImpl<T extends SettingsContainer> implements SettingsRepo<T> {

	private final String tableName;
	private final SQLHelperFactory sqlHelperFactory;
	private final SettingsFactory<T> settingsFactory;

	private final Cache<Long, T> cache = Caffeine.newBuilder()
		.expireAfterAccess(1, TimeUnit.HOURS)
		.build();

	public SettingsRepoImpl(String tableName, SQLHelperFactory sqlHelperFactory, SettingsFactory<T> settingsFactory) {
		this.tableName = tableName;
		this.sqlHelperFactory = sqlHelperFactory;
		this.settingsFactory = settingsFactory;
	}

	@Override
	public T getSettings(long identifier) {
		return cache.get(identifier, this::loadSettings);
	}

	private T loadSettings(long identifier) {
		try (SQLHelper helper = sqlHelperFactory.create("SELECT * FROM " + tableName + " WHERE identifier = ?", identifier)) {
			return settingsFactory.createSettings(identifier, helper.create() // NOSONAR closed by SQLHelper
				.executeQuery());
		} catch (SQLException e) {
			throw new SettingsLoadingException(e);
		}
	}
}
