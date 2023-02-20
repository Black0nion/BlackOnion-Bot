package com.github.black0nion.blackonionbot.config.discord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import io.javalin.http.BadRequestResponse;
import org.json.JSONObject;

import static java.util.Objects.requireNonNull;

public class SettingsUtils {
	private SettingsUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static Object parsePassedValue(JSONObject body, Setting<Object> setting) throws JsonProcessingException {
		requireNonNull(setting, "setting");

		final String value = "value";
		Object toSet;
		if (setting.canParse(Long.class)) {
			toSet = body.getLong(value);
		} else if (setting.canParse(Boolean.class)) {
			toSet = body.getBoolean(value);
		} else if (setting.canParse(String.class)) {
			toSet = body.getString(value);
		} else if (setting.canParse(Integer.class)) {
			toSet = body.getInt(value);
		} else if (setting.canParse(Double.class)) {
			toSet = body.getDouble(value);
		} else if (setting.canParse(Float.class)) {
			toSet = body.getFloat(value);
		} else {
			throw new BadRequestResponse("No parser found for : " + setting.getType().getSimpleName());
		}

		setting.setParsedValue(toSet);

		return OBJECT_MAPPER.writeValueAsString(setting);
	}
}
