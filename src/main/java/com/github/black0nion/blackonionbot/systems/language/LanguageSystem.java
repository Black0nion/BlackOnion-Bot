package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.exception.MultipleDefaultLanguagesException;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

// TODO: DI
public class LanguageSystem {
	private LanguageSystem() {}

	private static final Logger logger = LoggerFactory.getLogger(LanguageSystem.class);

	private static final HashMap<String, Language> languages = new HashMap<>();
	private static Language defaultLocale;
	private static String languageString;

	@Reloadable("language")
	public static void init() {
		languages.clear();
		AtomicBoolean hasDefault = new AtomicBoolean(false);
		new Reflections("translations", Scanners.Resources).getResources("[A-Z][a-z]+\\.json").stream()
			.peek(lang -> logger.info("Loading language stored in file: '{}'", lang))
			.map(Language::new)
			.peek(lang -> {
				if (lang.isDefault()) {
					if (hasDefault.get()) throw new MultipleDefaultLanguagesException();
					hasDefault.set(true);
				}
			})
			.forEach(lang -> {
				languages.put(lang.getLanguageCode(), lang);
				if (lang.isDefault()) defaultLocale = lang;
				try {
					Bot.getInstance().getSqlHelperFactory().run("INSERT INTO language (code, name) VALUES (?, ?) ON CONFLICT DO NOTHING", lang.getLanguageCode(), lang.getName());
				} catch (SQLException e) {
					logger.error("Error while updating language table", e);
				}
			});
		if (defaultLocale == null) {
			defaultLocale = languages.values().stream().findFirst().orElseThrow(() -> new NullPointerException("No languages found!"));
		}
	}

	@SQLSetup
	public static void setup(SQLHelperFactory sql) throws SQLException {
		sql.run("CREATE TABLE IF NOT EXISTS language (code VARCHAR(2) PRIMARY KEY, name TEXT NOT NULL)");
	}

	public static Map<String, Language> getLanguages() {
		return languages;
	}

	public static Language getLanguage(@Nullable final BlackUser author, @Nullable final BlackGuild guild) {
		if (author != null) {
			final Language userLang = author.getLanguage();
			if (userLang != null) return userLang;
		}
		if (guild != null) {
			final Language guildLang = guild.getLanguage();
			if (guildLang != null) return guildLang;
		}
		return defaultLocale;
	}

	public static Language getDefaultLanguage() {
		return defaultLocale;
	}

	@Nullable
	public static Language getLanguageFromName(final String name) {
		if (name == null || !languages.containsKey(name.toUpperCase())) return null;
		return languages.get(name.toUpperCase());
	}

	public static String getTranslation(final @Nullable String key, final @Nullable BlackUser author, final @Nullable BlackGuild guild) {
		if (author != null && author.getLanguage() != null) return author.getLanguage().getTranslation(key);
		if (guild != null && guild.getLanguage() != null) return guild.getLanguage().getTranslation(key);
		return defaultLocale.getTranslation(key);
	}

	public static String getLanguageString() {
		if (languageString != null) return languageString;
		return languageString = languages.values().stream().map(Language::getFullName).map(l -> "- " + l).collect(Collectors.joining("\n"));
	}
}
