package com.github.ahitm_2020_2025.blackonionbot.utils;

import java.awt.Color;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class EmbedUtils {
	
	public static EmbedBuilder getDefaultErrorEmbed() {
		return getDefaultErrorEmbed(null);
	}
	
	public static EmbedBuilder getDefaultErrorEmbed(User author) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Error")
				.setColor(Color.RED)
				.setTimestamp(Instant.now());
		if (author != null)
			builder.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		
		return builder;
	}
}
