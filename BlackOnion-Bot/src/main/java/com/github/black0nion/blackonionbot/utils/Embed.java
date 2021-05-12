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
		String tempText = LanguageSystem.getTranslation(text, user, guild);
		if (tempText != null)
			text = tempText;
		return super.setFooter(text);
	}
	
	@Override
	public EmbedBuilder setTitle(String title, String url) {
		String tempTitle = LanguageSystem.getTranslation(title, user, guild);
		if (tempTitle != null)
			title = tempTitle;
		return super.setTitle(title, url);
	}
	
	@Override
	public EmbedBuilder addField(String name, String value, boolean inline) {
		String tempName = LanguageSystem.getTranslation(name, user, guild);
		String tempValue = LanguageSystem.getTranslation(value, user, guild);
		if (tempName != null)
			name = tempName;
		if (tempValue != null)
			value = tempValue;
		return super.addField(name, value, inline);
	}
	
	public EmbedBuilder addUntranslatedField(String name, String value, boolean inline) {
		return super.addField(name, value, inline);
	}
}
