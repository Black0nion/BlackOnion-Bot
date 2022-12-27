package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.misc.exception.LanguageCreationException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.internal.utils.Checks;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Language {
	private static final Logger logger = LoggerFactory.getLogger(Language.class);
	private final String languageCode;
	private final String name;
	private final boolean isDefault;
	private final HashMap<String, String> messages;

	public Language(final String fileName) {
		this(
			new JSONObject(
				new BufferedReader(
					new InputStreamReader(
						Objects.requireNonNull(
							Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)
						)
					)
				)
				.lines()
				.collect(Collectors.joining())
			)
		);
	}

	public Language(final JSONObject translations) {
		String langCode = null;
		String langName = null;

		try {
			if (translations == null) throw new IllegalArgumentException("Translations must not be null");
			messages = new HashMap<>();
			JSONObject metadata = translations.getJSONObject("metadata");
			langName = metadata.getString("name");
			Checks.matches(langName, Pattern.compile("[A-Z][a-z]+"), "Language name");
			langCode = metadata.getString("code");
			Checks.matches(langCode, Pattern.compile("[A-Z]{2}"), "Language code");
			this.isDefault = metadata.has("default") && metadata.getBoolean("default");

			if (translations.isEmpty()) {
				throw new IllegalArgumentException("Translation file is empty");
			}

			for (final String key : translations.keySet()) {
				if (!key.toLowerCase().equals(key))
					logger.error("'{}' is not entirely in lower case! Please correct in '{}' !", key, langCode);
				if (key.equalsIgnoreCase("metadata")) continue;

				messages.put(key.toLowerCase(), translations.getString(key));
			}
		} catch (final Exception e) {
			throw new LanguageCreationException(langCode, langName, e);
		}
		this.languageCode = langCode;
		this.name = langName;
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
		return Placeholder.process(result, placeholders);
	}

	@Override
	public String toString() {
		return getFullName();
	}
}
