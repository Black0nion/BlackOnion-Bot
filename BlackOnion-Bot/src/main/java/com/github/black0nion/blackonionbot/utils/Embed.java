package com.github.black0nion.blackonionbot.utils;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.EmbedBuilder;

public class Embed extends EmbedBuilder {
	
	private BlackUser user;
	private BlackGuild guild;
	
	public Embed(BlackUser user, BlackGuild guild) {
		this.user = user;
		this.guild = guild;
	}
	
	public Embed(EmbedBuilder builder) {
		super(builder);
	}
	
	public Embed(Embed embed) {
		super(embed);
		this.user = embed.user;
		this.guild = embed.guild;
	}
	
	public Embed(BlackUser user, BlackGuild guild, EmbedBuilder builder) {
		super(builder);
		this.user = user;
		this.guild = guild;
	}
	
	public Embed() {
		this.user = null;
		this.guild = null;
	}
	
	@Override
	public Embed setFooter(String text) {
		String tempText = LanguageSystem.getTranslation(text, user, guild);
		if (tempText != null)
			text = tempText;
		super.setFooter(text);
		return this;
	}
	
	@Override
	public Embed setTitle(String title) {
		String tempTitle = LanguageSystem.getTranslation(title, user, guild);
		if (tempTitle != null)
			title = tempTitle;
		super.setTitle(title);
		return this;
	}
	
	@Override
	public Embed setTitle(String title, String url) {
		String tempTitle = LanguageSystem.getTranslation(title, user, guild);
		if (tempTitle != null)
			title = tempTitle;
		super.setTitle(title, url);
		return this;
	}
	
	@Override
	public Embed addField(String name, String value, boolean inline) {
		String tempName = LanguageSystem.getTranslation(name, user, guild);
		String tempValue = LanguageSystem.getTranslation(value, user, guild);
		if (tempName != null)
			name = tempName;
		if (tempValue != null)
			value = tempValue;
		super.addField(name, value, inline);
		return this;
	}
	
	public Embed addUntranslatedField(String name, String value, boolean inline) {
		super.addField(name, value, inline);
		return this;
	}
}
