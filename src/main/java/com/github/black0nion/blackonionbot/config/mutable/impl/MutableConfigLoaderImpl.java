package com.github.black0nion.blackonionbot.config.mutable.impl;

import com.github.black0nion.blackonionbot.config.common.ConfigFlag;
import com.github.black0nion.blackonionbot.config.common.Flags;
import com.github.black0nion.blackonionbot.config.common.exception.ConfigLoadingException;
import com.github.black0nion.blackonionbot.config.common.exception.ConfigSavingException;
import com.github.black0nion.blackonionbot.config.mutable.api.MutableConfigLoader;
import com.github.black0nion.blackonionbot.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.black0nion.blackonionbot.config.immutable.impl.ConfigLoaderImpl.getFlag;

public class MutableConfigLoaderImpl implements MutableConfigLoader {

	private static final Path CONFIG_FILE_PATH = Path.of("files/config.json");
	public static final MutableConfigLoaderImpl INSTANCE = new MutableConfigLoaderImpl();

	private final JSONObject json;

	private MutableConfigLoaderImpl() {
		try {
			List<String> lines = Files.exists(CONFIG_FILE_PATH) ? Files.readAllLines(CONFIG_FILE_PATH) : null;
			if (lines == null || lines.isEmpty()) {
				json = new JSONObject();
				return;
			}
			json = new JSONObject(String.join("", lines));
		} catch (IOException | JSONException e) {
			throw new ConfigLoadingException(e);
		}
	}

	@Override
	public void set(String name, Object value) {
		json.put(name, value);
		try {
			Files.writeString(CONFIG_FILE_PATH, json.toString(4));
		} catch (IOException e) {
			throw new ConfigSavingException(e);
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String name, Class<T> clazz, ConfigFlag... flagsArr) {
		List<ConfigFlag> flags = flagsArr == null ? List.of() : List.of(flagsArr);
		if (!json.has(name)) {
			if (flags.contains(Flags.NonNull)) {
				throw new ConfigLoadingException(new IllegalArgumentException("Missing required config value: " + name));
			}
			Flags.Default<T> defaultFlag = getFlag(flags, Flags.Default.class);
			return defaultFlag != null ? defaultFlag.defaultValue() : null;
		}
		T value = parse(name, clazz);

		if (value == null) return null;

		for (ConfigFlag f : flags) {
			if (f instanceof Flags.MatchesRegex flag && !flag.regex().matcher((CharSequence) value).matches()) {
				throw new ConfigLoadingException(new IllegalArgumentException("Config value " + name + " does not match regex " + flag.regex()));
			} else if (f instanceof Flags.Range flag
				&& value instanceof Number num
				&& (num.doubleValue() < flag.min() || num.doubleValue() > flag.max())) {
				throw new ConfigLoadingException(new IllegalArgumentException("Config value " + name + " is out of range " + flag.min() + " to " + flag.max()));
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	private <T> T parse(String name, Class<T> clazz) {
		if (clazz == String.class) return (T) json.getString(name);
		if (clazz == Integer.class) return (T) Integer.valueOf(json.getInt(name));
		if (clazz == Long.class) return (T) Long.valueOf(json.getLong(name));
		if (clazz == Boolean.class) return (T) Boolean.valueOf(json.getBoolean(name));
		if (clazz == Double.class) return (T) Double.valueOf(json.getDouble(name));
		if (clazz == Float.class) return (T) Float.valueOf(json.getFloat(name));
		if (clazz == Character.class) return (T) Character.valueOf(json.getString(name).charAt(0));
		if (clazz == List.class) return Utils.jsonArrayToList(json.getJSONArray(name));
		if (clazz == JSONObject.class) return (T) json.getJSONObject(name);
		if (clazz == JSONArray.class) return (T) json.getJSONArray(name);
		if (clazz.isEnum()) //noinspection rawtypes
			return (T) Enum.valueOf((Class<Enum>) clazz, json.getString(name)); // NOSONAR
		throw new ConfigLoadingException("Unsupported type: " + clazz.getName());
	}
}
