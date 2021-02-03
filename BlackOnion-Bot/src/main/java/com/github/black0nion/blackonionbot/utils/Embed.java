package com.github.black0nion.blackonionbot.utils;

import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Embed extends EmbedBuilder {
	
	User user;
	Guild guild;
	
	public Embed(User user, Guild guild) {
		this.user = user;
		this.guild = guild;
	}
	
	@Override
	public EmbedBuilder setFooter(String text) {
		text = LanguageSystem.getTranslatedString(text, user, guild);
		return super.setFooter(text);
	}
	
	@Override
	public EmbedBuilder setTitle(String title, String url) {
		title = LanguageSystem.getTranslatedString(title, user, guild);
		return super.setTitle(title, url);
	}
	
	@Override
	public EmbedBuilder addField(String name, String value, boolean inline) {
		name = LanguageSystem.getTranslatedString(name, user, guild);
		value = LanguageSystem.getTranslatedString(value, user, guild);
		return super.addField(name, value, inline);
	}
}
