package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.utils.Placeholder;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Language {

	String name;
	String languageCode;
	HashMap<String, String> messages;

	public Language(final String fileName) {
		try {
			this.name = fileName;
			messages = new HashMap<>();
			final InputStream in = getClass().getResourceAsStream("/translations/" + fileName + ".json");
			assert in != null;
			final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			final JSONObject translations = new JSONObject(String.join("\n", reader.lines().collect(Collectors.joining())));

			if (translations.isEmpty()) {
				System.out.println("fileName" + ".json is empty!");
				return;
			}

			for (final String key : translations.keySet()) {
				if (!key.toLowerCase().equals(key))
					System.out.println(key + " is not entirely in lower case! Please correct in " + fileName + ".json!");
				messages.put(key.toLowerCase(), translations.getString(key));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getName() {
		return name;
	}

	@Nonnull
	public String getTranslationNonNull(final String key) {
		return messages.get(key) != null ? messages.get(key) : key;
	}

	@SuppressWarnings("unused")
	@Nonnull
	public String getTranslationNonNull(String key, final Placeholder... placeholders) {
		if (key == null) key = "";
		String result = getTranslationNonNull(key);
		for (final Placeholder placeholder : placeholders) result = placeholder.process(result);
		return result;
	}

	@Nullable
	public String getTranslation(final String key) {
		if (key == null) return null;
		return messages.get(key);
	}

	@Nullable
	public String getTranslation(final String key, final Placeholder... placeholders) {
		if (key == null) return null;
		String result = getTranslation(key);
		if (result == null) return null;
		for (final Placeholder placeholder : placeholders) result = placeholder.process(result);
		return result;
	}

	@Override
	public String toString() {
		return name + " (" + languageCode + ")";
	}
}