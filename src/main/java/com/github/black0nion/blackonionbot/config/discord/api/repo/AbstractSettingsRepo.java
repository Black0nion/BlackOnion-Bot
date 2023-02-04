package com.github.black0nion.blackonionbot.config.discord.api.repo;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingSaveException;
import com.github.black0nion.blackonionbot.config.discord.exception.SettingsLoadingException;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.utils.ThrowableSupplier;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;
import java.util.function.LongFunction;

public abstract class AbstractSettingsRepo<T extends SettingsContainer, E extends ISnowflake> implements SettingsRepo<T> {

	private final String tableName;
	protected final SQLHelperFactory sqlHelperFactory;
	protected final LongFunction<RestAction<E>> entityGetter;

	private final Cache<Long, T> cache = Caffeine.newBuilder()
		.expireAfterAccess(1, TimeUnit.HOURS)
		.build();

	protected AbstractSettingsRepo(String tableName, SQLHelperFactory sqlHelperFactory, LongFunction<RestAction<E>> entityGetter) {
		this.tableName = tableName;
		this.sqlHelperFactory = sqlHelperFactory;
		this.entityGetter = entityGetter;
	}

	@Override
	public T getSettings(long identifier) throws SettingsLoadingException {
		return cache.get(identifier, this::loadSettings);
	}

	protected abstract T loadSettingsImpl(long id, SQLHelperFactory helper, ThrowableSupplier<ResultSet> resultSetSupplier, SQLHelperFactory factory) throws Exception; // NOSONAR will get wrapped in a SettingsLoadingException

	private T loadSettings(long identifier) {
		try (SQLHelper helper = sqlHelperFactory.create("SELECT * FROM " + tableName + " WHERE identifier = ?", identifier)) {
			return loadSettingsImpl(identifier, sqlHelperFactory, () -> helper.create().executeQuery(), sqlHelperFactory);
		} catch (SettingSaveException e) {
			throw e;
		} catch (Exception e) {
			throw new SettingsLoadingException(e);
		}
	}
}
