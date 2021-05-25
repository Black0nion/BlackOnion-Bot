package com.github.black0nion.blackonionbot.utils;

import java.awt.Color;
import java.time.Instant;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedUtils {
	
	public static EmbedBuilder getErrorEmbed(BlackUser author, BlackGuild guild) {
		EmbedBuilder builder = new Embed(author, guild)
				.setTitle("error")
				.setColor(Color.RED)
				.setTimestamp(Instant.now());
		if (author != null) builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		return builder;
	}
	
	public static EmbedBuilder getErrorEmbed() {
		return new Embed().setTitle("error")
				.setColor(Color.RED)
				.setTimestamp(Instant.now());
	}
	
	public static EmbedBuilder getSuccessEmbed(BlackUser author, BlackGuild guild) {
		EmbedBuilder builder = new Embed(author, guild)
				.setTitle("success")
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F))
				.setTimestamp(Instant.now());
		if (author != null) builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		return builder;
	}
	
	public static EmbedBuilder getSuccessEmbed() {
		return new Embed().setTitle("success")
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F))
				.setTimestamp(Instant.now());
	}
	
	public static EmbedBuilder getLoadingEmbed(BlackUser author, BlackGuild guild) {
		EmbedBuilder builder = new Embed(author, guild)
				.setTitle("loading")
				.setColor(Color.getHSBColor(0.16F, 1F, 1F))
				.setTimestamp(Instant.now());
		if (author != null) builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		return builder;
	}
	
	public static EmbedBuilder getLoadingEmbed() {
		return new Embed().setTitle("loading")
				.setColor(Color.getHSBColor(0.16F, 1F, 1F))
				.setTimestamp(Instant.now());
	}
	
	public static MessageEmbed premiumRequired(BlackUser author, BlackGuild guild) {
		EmbedBuilder builder = new Embed(author, guild)
				.setTitle("error")
				.addField("notpremium", "premiumrequired", false)
				.setColor(Color.RED)
				.setTimestamp(Instant.now());
		if (author != null) builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		return builder.build();
	}
}