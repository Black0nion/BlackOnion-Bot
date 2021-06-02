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
	
	public Language(String fileName, String languageCode) {
		try {
			this.name = fileName;
			this.languageCode = languageCode;
			messages = new HashMap<>();
			InputStream in = getClass().getResourceAsStream("/translations/" + fileName + ".json"); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			JSONObject translations = new JSONObject(String.join("\n", reader.lines().collect(Collectors.joining())));
			
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