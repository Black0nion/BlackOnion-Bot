package com.github.ahitm_2020_2025.blackonionbot.systems.language;

import java.util.HashMap;

import com.github.ahitm_2020_2025.blackonionbot.utils.CustomManager;

public class LanguageSystem {
	
	public static String defaultLocale = "EN";
	
	static HashMap<String, Language> languages = new HashMap<>();
	static HashMap<String, Language> userLanguages = new HashMap<>();
	static HashMap<String, Language> guildLanguages = new HashMap<>();
	
	private static CustomManager userManager = new CustomManager("userLanguages");
	private static CustomManager guildManager = new CustomManager("guildLanguages");
	
	public static void init() {
		languages.clear();
		languages.put("EN", new Language("English", "EN"));
		languages.put("DE", new Language("German", "DE"));
		
		reloadUserGuildLanguages();
	}
	
	public static void reloadUserGuildLanguages() {
		userLanguages.clear();
		guildLanguages.clear();
		
		for (String user : userManager.getKeys()) {
			userLanguages.put(user, getLanguageFromName(userManager.getString(user)));
		}
		
		for (String guild : guildManager.getKeys()) {
			guildLanguages.put(guild, getLanguageFromName(guildManager.getString(guild)));
		}
	}
	
	public static HashMap<String, Language> getLanguages() {
		return languages;
	}
	
	public static void updateUserLocale(String user, String locale) {
		userManager.save(user, locale);
		reloadUserGuildLanguages();
	}
	
	public static void updateGuildLocale(String guild, String locale) {
		guildManager.save(guild, locale);
		reloadUserGuildLanguages();
	}
	
	public static Language getLanguageFromName(String name) {
		try {
			return languages.get(name);
		} catch (Exception ignored) {}
		return null;
	}
	
	public static String getTranslatedString(String key, String user, String guild) {
		try {
			return userLanguages.get(user).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return guildLanguages.get(guild).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return languages.get(defaultLocale).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + getLanguageFromName(defaultLocale) + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getDefaultTranslatedString(String key) {
		try {
			return languages.get(defaultLocale).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + getLanguageFromName(defaultLocale) + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getLocaleTranslatedString(String key, String locale) {
		try {
			return languages.get(locale).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + getLanguageFromName(defaultLocale) + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getUserTranslatedString(String key, String user) {
		try {
			return userLanguages.get(user).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + getLanguageFromName(defaultLocale) + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getGuildTranslatedString(String key, String guild) {
		try {
			return guildLanguages.get(guild).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + getLanguageFromName(defaultLocale) + ".json!\nPlease report this issue to the admins!";
	}
}
