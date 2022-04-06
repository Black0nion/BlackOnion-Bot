package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class LanguageSystem {

	private static final HashMap<String, Language> languages = new HashMap<>();
	private static Language defaultLocale;
	private static String languageString;

	@Reloadable("language")
	public static void init() {
		languages.clear();
		AtomicBoolean hasDefault = new AtomicBoolean(false);
		new Reflections("translations", Scanners.Resources).getResources("[A-Z][a-z]+\\.json").stream()
				.peek(lang -> System.out.println("Loading language stored in file: " + lang)).map(Language::new)
				.peek(lang -> {
					if (lang.isDefault()) {
						if (hasDefault.get())
							throw new RuntimeException("There can only be one default language!");
						hasDefault.set(true);
					}
				}).forEach(lang -> {
					languages.put(lang.getLanguageCode(), lang);
					if (lang.isDefault())
						defaultLocale = lang;
				});
		if (defaultLocale == null) {
			defaultLocale = languages.values().stream().findFirst()
					.orElseThrow(() -> new NullPointerException("No languages found!"));
		}
	}

	public static HashMap<String, Language> getLanguages() {
		return languages;
	}

	public static Language getLanguage(final BlackUser author, final BlackGuild guild) {
		try {
			final Language userLang = author.getLanguage();
			if (userLang != null)
				return userLang;
		} catch (final Exception ignored) {
		}
		try {
			final Language guildLang = guild.getLanguage();
			if (guildLang != null)
				return guildLang;
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

	@Nullable
	public static Language getLanguageFromName(final String name) {
		if (name == null || !languages.containsKey(name.toUpperCase()))
			return null;
		return languages.get(name.toUpperCase());
	}

	public static String getTranslation(final @Nullable String key, final @Nullable BlackUser author,
			final @Nullable BlackGuild guild) {
		if (author != null && author.getLanguage() != null)
			return author.getLanguage().getTranslation(key);
		if (guild != null && guild.getLanguage() != null)
			return guild.getLanguage().getTranslation(key);
		return defaultLocale.getTranslation(key);
	}

	public static String getLanguageString() {
		if (languageString != null)
			return languageString;
		return languageString = languages.values().stream().map(Language::getFullName).map(l -> "- " + l)
				.collect(Collectors.joining("\n"));
	}
}
