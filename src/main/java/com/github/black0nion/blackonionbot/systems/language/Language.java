package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.internal.utils.Checks;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Language {

	private final String languageCode;
	private final String name;
	private final boolean isDefault;
	private final HashMap<String, String> messages;

	public Language(final String fileName) {
		this(new JSONObject(new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))))
						.lines().collect(Collectors.joining())));
	}

	public Language(final JSONObject translations) {
		try {
			messages = new HashMap<>();
			JSONObject metadata = translations.getJSONObject("metadata");
			this.name = metadata.getString("name");
			Checks.matches(this.name, Pattern.compile("[A-Z][a-z]+"), "Language name");
			this.languageCode = metadata.getString("code");
			Checks.matches(this.languageCode, Pattern.compile("[A-Z]{2}"), "Language code");
			this.isDefault = metadata.has("default") && metadata.getBoolean("default");

			if (translations.isEmpty()) {
				throw new NullPointerException("Translation file is empty");
			}

			for (final String key : translations.keySet()) {
				if (!key.toLowerCase().equals(key))
					System.out.println(
							key + " is not entirely in lower case! Please correct in " + this.languageCode + "!");
				if (key.equalsIgnoreCase("metadata"))
					continue;
				messages.put(key.toLowerCase(), translations.getString(key));
			}
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getName() {
		return name;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public String getFullName() {
		return name + " (" + languageCode + ")";
	}

	@Nonnull
	public String getTranslationNonNull(final String key) {
		return messages.get(key) != null ? messages.get(key) : key;
	}

	@SuppressWarnings("unused")
	@Nonnull
	public String getTranslationNonNull(String key, final Placeholder... placeholders) {
		if (key == null)
			key = "";
		String result = getTranslationNonNull(key);
		for (final Placeholder placeholder : placeholders)
			result = placeholder.process(result);
		return result;
	}

	@Nullable
	public String getTranslation(final String key) {
		if (key == null)
			return null;
		return messages.get(key);
	}

	@Nullable
	public String getTranslation(final String key, final Placeholder... placeholders) {
		if (key == null)
			return null;
		String result = getTranslation(key);
		if (result == null)
			return null;
		for (final Placeholder placeholder : placeholders)
			result = placeholder.process(result);
		return result;
	}

	@Override
	public String toString() {
		return getFullName();
	}
}
