package com.github.black0nion.blackonionbot.systems.language;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;

public class LanguageSystem {
	
	static HashMap<String, Language> languages = new HashMap<>();
	
	static ArrayList<Language> allLanguages = new ArrayList<>();
	
	public static Language german;
	public static Language english;
	
	public static Language defaultLocale;
	
	public static void init() {
		languages.clear();
		english = new Language("English", "EN");
		german = new Language("German", "DE");
		defaultLocale = german;
		allLanguages.add(english);
		allLanguages.add(german);
		languages.put("EN", english);
		languages.put("DE", german);
	}
	
	public static HashMap<String, Language> getLanguages() {
		return languages;
	}
	
	public static Language getLanguage(BlackUser author, BlackGuild guild) {
		try {
			Language userLang = author.getLanguage();
			if (userLang != null) return userLang;
		} catch (Exception ignored) {}
		try {
			Language guildLang = guild.getLanguage();
			if (guildLang != null) return guildLang;
		} catch (Exception ignored) {}
		try {
			return defaultLocale;
		} catch (Exception ignored) {}
		return null;
	}
	
	public static Language getDefaultLanguage() {
		return defaultLocale;
	}
	
	public static void updateUserLocale(BlackUser user, String locale) {
		user.setLanguage(getLanguageFromName(locale));
	}
	
	public static void updateGuildLocale(BlackGuild guild, String locale) {
		guild.setLanguage(getLanguageFromName(locale));
	}
	
	public static Language getLanguageFromName(String name) {
		if (name == null) return null;
		try {
			return languages.get(name.toUpperCase());
		} catch (Exception ignored) {}
		return null;
	}
	
	public static String getTranslation(String key, BlackUser author, BlackGuild guild) {
		try {
			return author.getLanguage().getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return guild.getLanguage().getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getTranslation(String key, BlackUser author) {
		try {
			return author.getLanguage().getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}

	public static String getTranslation(String key, BlackGuild guild) {
		try {
			return guild.getLanguage().getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	/**
	 * The replacement will also get translated!
	 * @param key
	 * @param author
	 * @param guild
	 * @param toReplace
	 * @param replacement
	 * @return
	 */
	public static String getReplacedTranslation(String key, BlackUser author, BlackGuild guild, String toReplace, String replacement) {
		return getTranslation(key, author, guild).replace(toReplace, getTranslation(replacement, author, guild));
	}
	
	public static String getTranslation(String key, Language language) {
		return language.getTranslatedString(key);
	}
}
