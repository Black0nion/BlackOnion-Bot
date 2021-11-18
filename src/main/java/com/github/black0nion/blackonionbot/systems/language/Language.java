package com.github.black0nion.blackonionbot.systems.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.Placeholder;

public class Language {
	
	File file;
	String name;
	String languageCode;
	HashMap<String, String> messages;
	
	public Language(final String fileName, final String languageCode) {
		try {
			this.name = fileName;
			this.languageCode = languageCode;
			messages = new HashMap<>();
			final InputStream in = getClass().getResourceAsStream("/translations/" + fileName + ".json"); 
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
	
	@Nonnull
	public String getTranslationNonNull(final String key, final Placeholder... placeholders) {
		String result = getTranslationNonNull(key);
		for (final Placeholder placeholder : placeholders) result = placeholder.process(result);
		return result;
	}
	
	@Nullable
	public String getTranslation(final String key) {
		return messages.get(key);
	}
	
	@Nullable
	public String getTranslation(final String key, final Placeholder... placeholders) {
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