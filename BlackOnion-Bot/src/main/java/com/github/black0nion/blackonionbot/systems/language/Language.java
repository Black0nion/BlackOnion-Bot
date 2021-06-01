package com.github.black0nion.blackonionbot.systems.language;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.google.common.io.Files;

public class Language {
	
	File file;
	String name;
	String languageCode;
	HashMap<String, String> messages;
	
	public Language(String fileName, String languageCode) {
		try {
			this.name = fileName;
			this.languageCode = languageCode;
			this.file = new File("files/translations/" + fileName + ".json");
			messages = new HashMap<>();
			JSONObject translations = new JSONObject(String.join("\n", Files.readLines(file, StandardCharsets.UTF_8)));
			
			if (translations.isEmpty()) {
				System.out.println("fileName" + ".json is empty!");
				return;
			}
			
			for (String key : translations.keySet()) {
				if (!key.toLowerCase().equals(key))
					System.out.println(key + " is not entirely in lower case! Please correct in " + fileName + ".json!");
				messages.put(key.toLowerCase(), translations.getString(key));
			}
		} catch (Exception e) {
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
	public String getTranslationNonNull(String key) {
		return messages.get(key) != null ? messages.get(key) : key;
	}
	
	@Nonnull
	public String getTranslationNonNull(String key, Placeholder... placeholders) {
		String result = getTranslationNonNull(key);
		for (Placeholder placeholder : placeholders) result = placeholder.process(result);
		return result;
	}
	
	@Nullable
	public String getTranslation(String key) {
		return messages.get(key);
	}
	
	@Nullable
	public String getTranslation(String key, Placeholder... placeholders) {
		String result = getTranslation(key);
		if (result == null) return null;
		for (Placeholder placeholder : placeholders) result = placeholder.process(result);
		return result;
	}
}