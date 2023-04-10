package com.github.black0nion.blackonionbot.utils;

import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageUtils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.awt.*;
import java.time.Instant;

public class EmbedUtils {

	private final LanguageSystem languageSystem;

	public EmbedUtils(LanguageSystem languageSystem) {
		this.languageSystem = languageSystem;
	}

	public static final Color BLACK_ONION_COLOR = Color.getHSBColor(0.8F, 1, 0.5F);
	private static final Color PREMIUM_COLOR = new Color(245, 189, 2);

	public TranslatedEmbedBuilder getErrorEmbed() {
		return getErrorEmbed(languageSystem.getDefaultLanguage());
	}

	public TranslatedEmbedBuilder getErrorEmbed(@Nullable final User author, final UserSettings userSettings, final GuildSettings guildSettings) {
		return getErrorEmbed(languageSystem.getDefaultLanguage(), author, userSettings, guildSettings);
	}

	public static TranslatedEmbedBuilder getErrorEmbed(final Language defaultLanguage, @Nullable final User author, @Nullable final UserSettings userSettings, final GuildSettings guildSettings) {
		final TranslatedEmbedBuilder builder = new TranslatedEmbedBuilder(LanguageUtils.getLanguage(userSettings, guildSettings, defaultLanguage)).setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getAsTag(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static TranslatedEmbedBuilder getErrorEmbed(Language language) {
		return new TranslatedEmbedBuilder(language).setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
	}

	public TranslatedEmbedBuilder getSuccessEmbed() {
		return getSuccessEmbed(languageSystem.getDefaultLanguage());
	}

	public TranslatedEmbedBuilder getSuccessEmbed(final User user, final UserSettings userSettings, final GuildSettings guildSettings) {
		return getSuccessEmbed(languageSystem.getDefaultLanguage(), user, userSettings, guildSettings);
	}

	public static TranslatedEmbedBuilder getSuccessEmbed(final Language defaultLanguage, final User user, final UserSettings userSettings, final GuildSettings guildSettings) {
		TranslatedEmbedBuilder builder = getSuccessEmbed(LanguageUtils.getLanguage(userSettings, guildSettings, defaultLanguage));
		if (user != null) {
			builder.setFooter(user.getAsTag(), user.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static TranslatedEmbedBuilder getSuccessEmbed(@Nullable Language language) {
		return new TranslatedEmbedBuilder(language).setColor(BLACK_ONION_COLOR).setTimestamp(Instant.now());
	}

	public TranslatedEmbedBuilder getLoadingEmbed(final User author, final UserSettings userSettings, final GuildSettings guild) {
		final TranslatedEmbedBuilder builder = new TranslatedEmbedBuilder(languageSystem.getLanguage(userSettings, guild)).setTitle("loading").setColor(Color.getHSBColor(0.16F, 1F, 1F)).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getAsTag(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public MessageEmbed premiumRequired(final User author, final UserSettings userSettings, final GuildSettings guildSettings) {
		final TranslatedEmbedBuilder builder = new TranslatedEmbedBuilder(languageSystem.getLanguage(userSettings, guildSettings))
			.setTitle("error")
			.addField("notpremium", "premiumrequired")
			.setColor(PREMIUM_COLOR)
			.setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getAsTag(), author.getEffectiveAvatarUrl());
		}
		return builder.build();
	}
}
