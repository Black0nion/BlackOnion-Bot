package com.github.black0nion.blackonionbot.systems.language;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildSettings;
import com.github.black0nion.blackonionbot.utils.CustomManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class LanguageSystem {
	
	static HashMap<String, Language> languages = new HashMap<>();
	static HashMap<String, Language> userLanguages = new HashMap<>();
	static HashMap<String, Language> guildLanguages = new HashMap<>();
	
	static ArrayList<Language> allLanguages = new ArrayList<>();
	
	private static CustomManager userManager = new CustomManager("userLanguages");
	
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
		
		reloadUserGuildLanguages();
	}
	
	public static void reloadUserGuildLanguages() {
		userLanguages.clear();
		guildLanguages.clear();
		
		for (String user : userManager.getKeys()) {
			userLanguages.put(user, getLanguageFromName(userManager.getString(user)));
		}
		
		GuildManager.init();
		
		for (GuildSettings guildSettings : GuildManager.getAllGuildOptions()) {
			guildLanguages.put(guildSettings.getGuildId(), guildSettings.getLanguage());
		}
	}
	
	public static HashMap<String, Language> getLanguages() {
		return languages;
	}
	
	public static Language getDefaultLanguage() {
		return defaultLocale;
	}
	
	public static Language getUserLanguage(String user) {
		try {
			return userLanguages.get(user);
		} catch (Exception ignored) {return null;}
	}
	
	public static Language getGuildLanguage(String guild) {
		try {
			return guildLanguages.get(guild);
		} catch (Exception ignored) {return null;}
	}
	
	public static void updateUserLocale(String user, String locale) {
		userManager.save(user, locale);
		reloadUserGuildLanguages();
	}
	
	public static void updateGuildLocale(String guild, String locale) {
		GuildManager.getGuildSettings(guild).setLanguage(getLanguageFromName(locale));
		reloadUserGuildLanguages();
	}
	
	public static Language getLanguageFromName(String name) {
		try {
			return languages.get(name.toUpperCase());
		} catch (Exception ignored) {}
		return null;
	}
	
	public static String getTranslatedString(String key, User author, Guild guild) {
		try {
			return userLanguages.get(author.getId()).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return guildLanguages.get(guild.getId()).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getTranslatedString(String key, User author) {
		try {
			return userLanguages.get(author.getId()).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getDefaultTranslatedString(String key) {
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getLocaleTranslatedString(String key, String locale) {
		try {
			return languages.get(locale).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getUserTranslatedString(String key, String user) {
		try {
			return userLanguages.get(user).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getGuildTranslatedString(String key, String guild) {
		try {
			return guildLanguages.get(guild).getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
}
