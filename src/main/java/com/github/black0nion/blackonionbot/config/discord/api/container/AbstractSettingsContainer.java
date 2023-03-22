package com.github.black0nion.blackonionbot.config.discord.api.container;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSettingBuilder;
import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingSaveException;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.utils.Utils;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.LongFunction;

/**
 * @param <E> The type of the entity, e.g. {@link net.dv8tion.jda.api.entities.User} or {@link net.dv8tion.jda.api.entities.Guild Guild}. This can also be a {@link net.dv8tion.jda.api.requests.RestAction RestAction}
 */
public abstract class AbstractSettingsContainer<E> implements SettingsContainer<E> {

	protected final List<Setting<?>> settings = new LinkedList<>();
	protected final long id;
	protected final LongFunction<E> entityGetter;
	protected final SettingsSaver settingsSaver;
	private boolean firstRun = false;

	protected AbstractSettingsContainer(String tableName, long id, LongFunction<E> entityGetter, SQLHelperFactory sqlHelperFactory) {
		this.id = id;
		this.entityGetter = entityGetter;
		this.settingsSaver = setting -> {
			try {
				if (firstRun) {
					// save all settings to the database
					String[] columns = settings.stream().map(Setting::getName).toArray(String[]::new);
					String placeholders = String.join(",", columns).replaceAll("[^,]+", "?");

					Object[] values = settings.stream().map(Setting::toDatabaseValue).toArray();
					Object[] args = new Object[values.length + 1];
					args[0] = id;
					System.arraycopy(values, 0, args, 1, columns.length);

					sqlHelperFactory.run("INSERT INTO " + tableName + " (identifier," + String.join(",", columns) + ") VALUES (?," + placeholders + ")", args);

					firstRun = false;
				} else {
					sqlHelperFactory.run("UPDATE " + tableName + " SET " + setting.getName() + " = ? WHERE identifier = ?", setting.toDatabaseValue(), id);
				}
			} catch (SQLException e) {
				throw new SettingSaveException(setting, e);
			}
		};
	}

	protected void loadSettings(ResultSet resultSet) throws Exception {
		if (!resultSet.isBeforeFirst()) {
			LoggerFactory.getLogger(getClass()).debug("No settings found in database for {}", this);
			resultSet.close();
			firstRun = true;
			return;
		}
		resultSet.next();

		for (Setting<?> setting : settings) {
			// Check if the column exists
			resultSet.findColumn(setting.getName());

			Object parsed = Utils.parseValue(setting, resultSet);
			if (parsed == null && !setting.isNullable()) throw new ParseException("Setting " + setting.getName() + " cannot be null");
			setting.setParsedValueBypassing(parsed);
		}
		resultSet.close();
	}

	@Override
	public long getIdentifier() {
		return id;
	}

	public <T extends Setting<?>> T addSetting(AbstractSettingBuilder<?, T, ?> settingBuilder) {
		return addSetting(settingBuilder.build());
	}

	public <T extends Setting<?>> T addSetting(T setting) {
		Objects.requireNonNull(setting, "Setting cannot be null");
		settings.add(setting);
		return setting;
	}

	@Override
	public E retrieveEntity() {
		return entityGetter.apply(id);
	}

	@Override
	public List<Setting<?>> getSettings() {
		return settings;
	}

	@Override
	public <T> Setting<T> getSetting(String name) {
		for (Setting<?> setting : settings) {
			if (setting.getName().equals(name)) {
				//noinspection unchecked
				return (Setting<T>) setting;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "AbstractSettingsContainer{" +
			"id=" + id +
			", settings=" + settings +
			'}';
	}
}
