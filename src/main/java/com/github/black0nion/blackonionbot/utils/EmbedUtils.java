package com.github.black0nion.blackonionbot.utils;

import java.awt.Color;
import java.time.Instant;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nullable;

public class EmbedUtils {

	private EmbedUtils() {}

	public static final Color BLACK_ONION_COLOR = Color.getHSBColor(0.8F, 1, 0.5F);
	private static final Color PREMIUM_COLOR = new Color(245, 189, 2);

	public static TranslatedEmbedBuilder getErrorEmbed(final BlackUser author, final BlackGuild guild) {
		final TranslatedEmbedBuilder builder = new TranslatedEmbedBuilder(LanguageSystem.getLanguage(author, guild)).setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static TranslatedEmbedBuilder getErrorEmbed() {
		return new TranslatedEmbedBuilder().setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
	}

	public static TranslatedEmbedBuilder getSuccessEmbed() {
		return getSuccessEmbed(null);
	}

	public static TranslatedEmbedBuilder getSuccessEmbed(final BlackUser author, final BlackGuild guild) {
		TranslatedEmbedBuilder builder = getSuccessEmbed(LanguageSystem.getLanguage(author, guild));
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static TranslatedEmbedBuilder getSuccessEmbed(@Nullable Language language) {
		return new TranslatedEmbedBuilder(language).setColor(BLACK_ONION_COLOR).setTimestamp(Instant.now());
	}

	public static TranslatedEmbedBuilder getLoadingEmbed(final BlackUser author, final BlackGuild guild) {
		final TranslatedEmbedBuilder builder = new TranslatedEmbedBuilder(LanguageSystem.getLanguage(author, guild)).setTitle("loading").setColor(Color.getHSBColor(0.16F, 1F, 1F)).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static MessageEmbed premiumRequired(final BlackUser author, final BlackGuild guild) {
		final TranslatedEmbedBuilder builder = new TranslatedEmbedBuilder(LanguageSystem.getLanguage(author, guild))
			.setTitle("error")
			.addField("notpremium", "premiumrequired")
			.setColor(PREMIUM_COLOR)
			.setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder.build();
	}
}
