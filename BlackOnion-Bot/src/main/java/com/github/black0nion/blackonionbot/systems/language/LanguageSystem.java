package com.github.black0nion.blackonionbot.systems.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.misc.Reloadable;

public class LanguageSystem {

    static HashMap<String, Language> languages = new HashMap<>();

    static ArrayList<Language> allLanguages = new ArrayList<>();

    public static Language german;
    public static Language english;

    public static Language defaultLocale;

    @Reloadable("language")
    public static void init() {
	languages.clear();
	english = new Language("English", "EN");
	german = new Language("German", "DE");
	defaultLocale = german;
	allLanguages.add(english);
	allLanguages.add(german);
	languages.put("EN", english);
	languages.put("DE", german);

	for (final Map.Entry<String, Language> entry : languages.entrySet()) {
	    validLanguages += entry.getValue().getName() + " (" + entry.getKey() + ")\n";
	}
	validLanguages += "```";
    }

    public static String validLanguages = "\n```";

    public static HashMap<String, Language> getLanguages() {
	return languages;
    }

    public static Language getLanguage(final BlackUser author, final BlackGuild guild) {
	try {
	    final Language userLang = author.getLanguage();
	    if (userLang != null) return userLang;
	} catch (final Exception ignored) {
	}
	try {
	    final Language guildLang = guild.getLanguage();
	    if (guildLang != null) return guildLang;
	} catch (final Exception ignored) {
	}
	try {
	    return defaultLocale;
	} catch (final Exception ignored) {
	}
	return null;
    }

    public static Language getDefaultLanguage() {
	return defaultLocale;
    }

    public static Language getLanguageFromName(final String name) {
	if (name == null || !languages.containsKey(name.toUpperCase())) return null;
	return languages.get(name.toUpperCase());
    }

    public static String getTranslation(final String key, final BlackUser author, final BlackGuild guild) {
	try {
	    return author.getLanguage().getTranslationNonNull(key);
	} catch (final Exception ignored) {
	}
	try {
	    return guild.getLanguage().getTranslationNonNull(key);
	} catch (final Exception ignored) {
	}
	try {
	    return defaultLocale.getTranslationNonNull(key);
	} catch (final Exception ignored) {
	}
	return "ERROR! Key " + key + "doesn't exist in " + defaultLocale.getName() + ".json!\nPlease report this issue to the admins!";
    }

    /**
     * The replacement will also get translated!
     *
     * @param key
     * @param author
     * @param guild
     * @param toReplace
     * @param replacement
     * @return
     */
    public static String getReplacedTranslation(final String key, final BlackUser author, final BlackGuild guild, final String toReplace, final String replacement) {
	return getTranslation(key, author, guild).replace(toReplace, getTranslation(replacement, author, guild));
    }

    public static enum Languages {

	GERMAN(german), ENGLISH(english);

	private final Language lang;

	private Languages(final Language lang) {
	    this.lang = lang;
	}

	/**
	 * @return the lang
	 */
	public Language getLang() {
	    return this.lang;
	}
    }
}