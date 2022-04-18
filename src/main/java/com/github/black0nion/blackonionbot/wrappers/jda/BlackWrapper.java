package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.systems.settings.Setting;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.internal.utils.Checks;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class BlackWrapper {

	protected abstract Document getIdentifier();

	protected abstract MongoCollection<Document> getCollection();

	@Nullable
	public Document getConfig() {
		return getCollection().find(getIdentifier()).first();
	}

	public <T> void saveList(final String key, final List<T> value) {
		save(new Document(key, value));
	}

	public <T> T get(final String key, final Class<T> clazz) {
		return Optional.ofNullable(getConfig()).map(doc -> doc.get(key, clazz)).orElse(null);
	}

	public <T> void save(final String key, final T value) {
		LoggerFactory.getLogger(this.getClass()).debug("[{}] Saving {} to {}", getIdentifier(), value, key);
		save(new Document(key, value));
	}

	private void save(final Document doc) {
		if (getCollection().find(getIdentifier()).first() == null) {
			final Document newDoc = getIdentifier();
			newDoc.putAll(doc);
			getCollection().insertOne(newDoc);
		} else {
			getCollection().updateOne(getIdentifier(), new Document("$set", doc));
		}
	}

	public void clear(final String... keys) {
		final Document doc = new Document();
		for (final String key : keys) {
			doc.put(key, "");
		}
		clear(doc);
	}

	public void clear(final Document doc) {
		getCollection().updateOne(getIdentifier(), new Document("$unset", doc));
	}

	private final HashMap<String, Setting<?>> settings = new HashMap<>();

	public Collection<Setting<?>> getSettings() {
		return settings.values();
	}

	public <T> Setting<T> getSetting(final String key, final Class<T> clazz) {
		if (!settings.containsKey(key)) {
			throw new NullPointerException("Setting " + key + " does not exist!");
		} else {
			Setting<?> setting = settings.get(key);
			if (setting.getClass() != clazz) {
				throw new ClassCastException("Setting " + key + " is not of type " + clazz.getName());
			}
			//noinspection unchecked
			return (Setting<T>) setting;
		}
	}

	@Nonnull
	public <T> Setting<T> add(final Setting<T> setting) {
		return addSetting(setting.getName(), setting);
	}

	public <T> Setting<T> addSetting(final String key, final Setting<T> setting) {
		settings.put(key, setting);
		return setting.addSettingChangeListener(this::saveSetting);
	}

	public <T> T getSetting(final String key, final Class<T> clazz, final T defaultValue) {
		return Optional.ofNullable(getConfig()).map(doc -> doc.get(key, clazz)).orElse(defaultValue);
	}

	protected void loadSettings() {
		Document config = getConfig();
		if (config == null) {
			settings.values().forEach(Setting::reset);
		} else {
			for (final Setting<?> setting : settings.values()) {
				Document settingDoc = new Document(config);
				// prevent modification of other keys
				settingDoc.keySet().retainAll(Collections.singleton(setting.getName()));
				setting.load(config);
			}
		}
	}

	/*
	┌─────────┬──────────┬─────────┬─────────┐
	│  Value  │ Nullable │ Default │  In DB  │
	├─────────┼──────────┼─────────┼─────────┤
	│ null    │ true     │ null    │ /       │
	│ null    │ true     │ element │ null    │
	│ element │ true     │ null    │ element │
	│ element │ true     │ element │ /       │
	│ element │ false    │ element │ /       │
	└─────────┴──────────┴─────────┴─────────┘
	 */
	/**
	 *<table><tbody><tr><th>Value</th><th>Nullable</th><th>Default</th><th>In DB</th></tr><tr><td>null</td><td>true</td><td>null</td><td>/</td></tr><tr><td>null</td><td>true</td><td>element</td><td>null</td></tr><tr><td>element</td><td>true</td><td>null</td><td>element</td></tr><tr><td>element</td><td>true</td><td>element</td><td>/</td></tr><tr><td>element</td><td>false</td><td>element</td><td>/</td></tr></tbody></table>
	 */
	private void saveSetting(@Nonnull Setting<?> setting) {
		Checks.notNull(setting, "setting");

		Object value = setting.getValue();
		Object defaultValue = setting.getDefaultValue();
		boolean nullable = setting.isNullable();

		if (value == null && nullable && defaultValue == null) {
			clear(setting.getName());
		} else if (value == null && nullable) {
			save(setting.getName(), null);
		} else if (value != null && value.equals(defaultValue)) {
			clear(setting.getName());
		} else {
			save(setting.getName(), value);
		}
	}
}