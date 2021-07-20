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
	    this.messages = new HashMap<>();
	    final InputStream in = this.getClass().getResourceAsStream("/translations/" + fileName + ".json");
	    final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    final JSONObject translations = new JSONObject(String.join("\n", reader.lines().collect(Collectors.joining())));

	    if (translations.isEmpty()) {
		System.out.println("fileName" + ".json is empty!");
		return;
	    }

	    for (final String key : translations.keySet()) {
		if (!key.toLowerCase().equals(key)) {
		    System.out.println(key + " is not entirely in lower case! Please correct in " + fileName + ".json!");
		}
		this.messages.put(key.toLowerCase(), translations.getString(key));
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public String getLanguageCode() {
	return this.languageCode;
    }

    public String getName() {
	return this.name;
    }

    public String getFullName() {
	return this.getName() + " (" + this.getLanguageCode() + ")";
    }

    @Nonnull
    public String getTranslationNonNull(final String key) {
	return this.messages.get(key) != null ? this.messages.get(key) : key;
    }

    @Nonnull
    public String getTranslationNonNull(final String key, final Placeholder... placeholders) {
	String result = this.getTranslationNonNull(key);
	for (final Placeholder placeholder : placeholders) {
	    result = placeholder.process(result);
	}
	return result;
    }

    @Nullable
    public String getTranslation(final String key) {
	return this.messages.get(key);
    }

    @Nullable
    public String getTranslation(final String key, final Placeholder... placeholders) {
	String result = this.getTranslation(key);
	if (result == null) return null;
	for (final Placeholder placeholder : placeholders) {
	    result = placeholder.process(result);
	}
	return result;
    }

    @Override
    public String toString() {
	return this.name + " (" + this.languageCode + ")";
    }
}