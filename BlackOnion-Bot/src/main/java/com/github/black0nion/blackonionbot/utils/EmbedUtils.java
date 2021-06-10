package com.github.black0nion.blackonionbot.utils;

import java.awt.Color;
import java.time.Instant;

import com.github.black0nion.blackonionbot.blackobjects.BlackEmbed;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedUtils {

    public static final Color blackOnionColor = Color.getHSBColor(0.8F, 1, 0.5F);

    public static BlackEmbed getErrorEmbed(final BlackUser author, final BlackGuild guild) {
	final BlackEmbed builder = new BlackEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
	if (author != null) {
	    builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
	}
	return builder;
    }

    public static BlackEmbed getErrorEmbed() {
	return new BlackEmbed().setTitle("error").setColor(Color.RED).setTimestamp(Instant.now());
    }

    public static BlackEmbed getSuccessEmbed(final BlackUser author, final BlackGuild guild) {
	final BlackEmbed builder = new BlackEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("success").setColor(blackOnionColor).setTimestamp(Instant.now());
	if (author != null) {
	    builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
	}
	return builder;
    }

    public static BlackEmbed getSuccessEmbed() {
	return new BlackEmbed().setTitle("success").setColor(blackOnionColor).setTimestamp(Instant.now());
    }

    public static BlackEmbed getLoadingEmbed(final BlackUser author, final BlackGuild guild) {
	final BlackEmbed builder = new BlackEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("loading").setColor(Color.getHSBColor(0.16F, 1F, 1F)).setTimestamp(Instant.now());
	if (author != null) {
	    builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
	}
	return builder;
    }

    public static BlackEmbed getLoadingEmbed() {
	return new BlackEmbed().setTitle("loading").setColor(Color.getHSBColor(0.16F, 1F, 1F)).setTimestamp(Instant.now());
    }

    private static final Color premiumColor = new Color(245, 189, 2);

    public static MessageEmbed premiumRequired(final BlackUser author, final BlackGuild guild) {
	final BlackEmbed builder = new BlackEmbed(LanguageSystem.getLanguage(author, guild)).setTitle("error").addField("notpremium", "premiumrequired", false).setColor(premiumColor).setTimestamp(Instant.now());
	if (author != null) {
	    builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
	}
	return builder.build();
    }
}