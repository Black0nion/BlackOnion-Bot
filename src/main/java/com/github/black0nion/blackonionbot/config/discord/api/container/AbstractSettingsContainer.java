package com.github.black0nion.blackonionbot.config.discord.api.container;

import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.LongFunction;

public abstract class AbstractSettingsContainer<E extends ISnowflake> implements SettingsContainer {

	protected final List<Setting<?>> settings = new LinkedList<>();
	protected final long id;
	protected final LongFunction<RestAction<E>> entityGetter;

	protected AbstractSettingsContainer(long id, LongFunction<RestAction<E>> entityGetter) {
		this.id = id;
		this.entityGetter = entityGetter;
	}

	protected void loadSettings(ResultSet resultSet) throws Exception {
		if (!resultSet.isBeforeFirst()) return;
		resultSet.next();

		for (Setting<?> setting : settings) {
			if (setting.getType().equals(String.class))
				setting.setParsedValue(resultSet.getString(setting.getName()));
			else if (setting.getType().equals(Integer.class))
				setting.setParsedValue(resultSet.getInt(setting.getName()));
			else if (setting.getType().equals(Long.class))
				setting.setParsedValue(resultSet.getLong(setting.getName()));
			else if (setting.getType().equals(Boolean.class))
				setting.setParsedValue(resultSet.getBoolean(setting.getName()));
			else if (setting.getType().equals(Double.class))
				setting.setParsedValue(resultSet.getDouble(setting.getName()));
			else if (setting.getType().equals(Float.class))
				setting.setParsedValue(resultSet.getFloat(setting.getName()));
			else if (setting.getType().equals(Short.class))
				setting.setParsedValue(resultSet.getShort(setting.getName()));
			else if (setting.getType().equals(Byte.class))
				setting.setParsedValue(resultSet.getByte(setting.getName()));
			else if (setting.getType().equals(Character.class))
				setting.setParsedValue(resultSet.getString(setting.getName()).charAt(0));
			else
				throw new SQLException("Unknown type: " + setting.getType());
		}
		resultSet.close();
	}

	@Override
	public long getIdentifier() {
		return id;
	}

	public <T extends Setting<?>> T addSetting(T setting) {
		settings.add(setting);
		return setting;
	}

	public RestAction<E> retrieveEntity() {
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
