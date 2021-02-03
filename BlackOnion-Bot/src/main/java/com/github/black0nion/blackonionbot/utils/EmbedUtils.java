package com.github.black0nion.blackonionbot.utils;

import java.awt.Color;
import java.time.Instant;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class EmbedUtils {
	
	public static EmbedBuilder getDefaultErrorEmbed() {
		return getDefaultErrorEmbed(null);
	}
	
	public static EmbedBuilder getDefaultErrorEmbed(User author) {
			Language authorLanguage = (author != null ? LanguageSystem.getUserLanguage(author.getId()) : null);
			return getDefaultErrorEmbed(author, (authorLanguage != null ? authorLanguage : LanguageSystem.getDefaultLanguage()));
	}
	
	public static EmbedBuilder getDefaultErrorEmbed(User author, Guild guild) {
		Language authorLanguage = LanguageSystem.getUserLanguage(author.getId());
		Language guildLanguage = LanguageSystem.getGuildLanguage(guild.getId());
		return getDefaultErrorEmbed(author, authorLanguage != null ? authorLanguage : (guildLanguage != null ? guildLanguage : LanguageSystem.getDefaultLanguage()));
	}
	
	public static EmbedBuilder getDefaultErrorEmbed(User author, Language lang) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(lang.getTranslatedString("error"))
				.setColor(Color.RED)
				.setTimestamp(Instant.now());
		if (author != null)
			builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		
		return builder;
	}
	
	public static EmbedBuilder getDefaultSuccessEmbed() {
		return getDefaultSuccessEmbed(null);
	}
	
	public static EmbedBuilder getDefaultSuccessEmbed(User author) {
		Language authorLanguage = (author != null ? LanguageSystem.getUserLanguage(author.getId()) : null);
		return getDefaultSuccessEmbed(author, (authorLanguage != null ? authorLanguage : LanguageSystem.getDefaultLanguage()));
	}
	
	public static EmbedBuilder getDefaultSuccessEmbed(User author, Guild guild) {
		Language authorLanguage = LanguageSystem.getUserLanguage(author.getId());
		Language guildLanguage = LanguageSystem.getGuildLanguage(guild.getId());
		return getDefaultSuccessEmbed(author, authorLanguage != null ? authorLanguage : (guildLanguage != null ? guildLanguage : LanguageSystem.getDefaultLanguage()));
	}
	
	public static EmbedBuilder getDefaultSuccessEmbed(User author, Language lang) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(lang.getTranslatedString("success"))
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F))
				.setTimestamp(Instant.now());
		if (author != null)
			builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		
		return builder;
	}
	
	public static EmbedBuilder getDefaultTranslatedErrorEmbed(User author, Guild guild) {
		return new Embed(author, guild)
				.setTitle("error")
				.setColor(Color.RED)
				.setTimestamp(Instant.now());
	}
	
	public static EmbedBuilder getDefaultTranslatedSuccessEmbed(User author, Guild guild) {
		return new Embed(author, guild)
				.setTitle("success")
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F))
				.setTimestamp(Instant.now());
	}
}
