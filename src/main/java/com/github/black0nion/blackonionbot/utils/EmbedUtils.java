package com.github.black0nion.blackonionbot.utils;

import java.awt.Color;
import java.time.Instant;

import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedUtils {

	public static final Color BLACK_ONION_COLOR = Color.getHSBColor(0.8F, 1, 0.5F);
	private static final Color premiumColor = new Color(245, 189, 2);

	public static TranslatedEmbed getErrorEmbed(final BlackUser author, final BlackGuild guild) {
		final TranslatedEmbed builder = new TranslatedEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static TranslatedEmbed getErrorEmbed() {
		return new TranslatedEmbed().setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
	}

	public static TranslatedEmbed getSuccessEmbed(final BlackUser author, final BlackGuild guild) {
		final TranslatedEmbed builder = new TranslatedEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("success").setColor(BLACK_ONION_COLOR).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static TranslatedEmbed getSuccessEmbed() {
		return new TranslatedEmbed().setTitle("success").setColor(BLACK_ONION_COLOR).setTimestamp(Instant.now());
	}

	public static TranslatedEmbed getLoadingEmbed(final BlackUser author, final BlackGuild guild) {
		final TranslatedEmbed builder = new TranslatedEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("loading").setColor(Color.getHSBColor(0.16F, 1F, 1F)).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder;
	}

	public static MessageEmbed premiumRequired(final BlackUser author, final BlackGuild guild) {
		final TranslatedEmbed builder = new TranslatedEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("error").addField("notpremium", "premiumrequired", false).setColor(premiumColor).setTimestamp(Instant.now());
		if (author != null) {
			builder.setFooter(author.getFullName(), author.getEffectiveAvatarUrl());
		}
		return builder.build();
	}
}