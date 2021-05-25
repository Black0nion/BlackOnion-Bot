package com.github.black0nion.blackonionbot.systems.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.utils.CustomManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.GuildManager;

public class LanguageSystem {
	
	static HashMap<String, Language> languages = new HashMap<>();
	static HashMap<Long, Language> userLanguages = new HashMap<>();
	static HashMap<Long, Language> guildLanguages = new HashMap<>();
	
	private static final LoadingCache<BlackGuild, Language> guildsLanguages = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<BlackGuild, Language>() {
                @Override
                public Language load(final BlackGuild guild) {
                    return guild.getLanguage();
                }
            });
	
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
		locale = locale.toUpperCase();
		GuildManager.save(guild, "language", locale);
		guildLanguages.remove(guild);
		guildLanguages.put(guild, getLanguageFromName(locale));
	}
	
	public static Language getLanguageFromName(String name) {
		if (name == null) return null;
		try {
			return languages.get(name.toUpperCase());
		} catch (Exception ignored) {}
		return null;
	}
	
	public static String getTranslation(String key, User author, Guild guild) {
		try {
			return getUserLanguage(author.getId()).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return getGuildLanguage(guild.getId()).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}
	
	public static String getTranslation(String key, User author) {
		try {
			return getUserLanguage(author.getId()).getTranslatedString(key);
		} catch (Exception ignored) {}
		try {
			return defaultLocale.getTranslatedString(key);
		} catch (Exception ignored) {}
		return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
	}

	public static String getTranslation(String key, Guild guild) {
		try {
			return getGuildLanguage(guild.getId()).getTranslatedString(key);
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
	public static String getReplacedTranslation(String key, User author, Guild guild, String toReplace, String replacement) {
		return getTranslation(key, author, guild).replace(toReplace, getTranslation(replacement, author, guild));
	}
	
	public static String getTranslation(String key, Language language) {
		return language.getTranslatedString(key);
	}
}
