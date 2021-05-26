/**
 * Author: _SIM_
 * Date: 26.05.2021
 * Copyright 2021 BlackOnion
 * File: CommandEvent.java
 */
package com.github.black0nion.blackonionbot.commands;

import static com.github.black0nion.blackonionbot.utils.EmbedUtils.getErrorEmbed;
import static com.github.black0nion.blackonionbot.utils.EmbedUtils.getLoadingEmbed;
import static com.github.black0nion.blackonionbot.utils.EmbedUtils.getSuccessEmbed;

import java.util.function.Consumer;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 * Date: 26.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: CommandEvent
 */
@SuppressWarnings("unused")
public class CommandEvent {
	
	private Command command;
	private GuildMessageReceivedEvent event;
	private BlackGuild guild;
	private TextChannel channel;
	private BlackMessage message;
	private BlackMember member;
	private BlackUser user;
	private EmbedBuilder successEmbed;
	private EmbedBuilder loadingEmbed;
	private EmbedBuilder errorEmbed;
	private Language language;
	
	@Deprecated
	public CommandEvent(Command cmd, GuildMessageReceivedEvent e) {
		this(cmd, e, BlackGuild.from(e.getGuild()), BlackMessage.from(e.getMessage()), BlackMember.from(e.getMember()), BlackUser.from(e.getAuthor()));
	}

	public CommandEvent(Command cmd, GuildMessageReceivedEvent e, BlackGuild guild, BlackMessage message, BlackMember member, BlackUser user) {
		this.command = cmd;
		this.event = e;
		this.guild = guild;
		this.channel = e.getChannel();
		this.message = message;
		this.member = member;
		this.user = user;
		this.successEmbed = getSuccessEmbed(this.user, this.guild);
		this.loadingEmbed = getLoadingEmbed(this.user, this.guild);
		this.errorEmbed = getErrorEmbed(this.user, this.guild);
		this.language = LanguageSystem.getLanguage(user, guild);
	}
	
	/**
	 * @return the language, user -> guild -> default
	 */
	public Language getLanguage() {
		return language;
	}
	
	public EmbedBuilder success() {
		return this.successEmbed;
	}
	
	public void success(String name, String value) {
		reply(successEmbed.addField(name, value, false));
	}
	
	public void success(String title, String name, String value) {
		reply(successEmbed.setTitle(title).addField(name, value, false));
	}
	
	public void success(String title, String url, String name, String value) {
		reply(successEmbed.setTitle(title, url).addField(name, value, false));
	}
	
	public void success(String title, String name, String value, final Placeholder... placeholders) {
		success(title, name, value, null, placeholders);
	}
	
	public void success(String title, String name, String value, Consumer<? super BlackMessage> success, final Placeholder... placeholders) {
		for (final Placeholder placeholder : placeholders) {
			title = placeholder.process(title);
			name = placeholder.process(name);
			value = placeholder.process(value);
		}
		
		reply(successEmbed.setTitle(title).addField(name, value, false), success);
	}
	
	public void success(String name, String value, final Placeholder... placeholders) {
		for (final Placeholder placeholder : placeholders) {
			name = placeholder.process(name);
			value = placeholder.process(value);
		}
		
		reply(successEmbed.addField(name, value, false), null);
	}
	
	public void success(String name, String value, Consumer<? super BlackMessage> success, final Placeholder... placeholders) {
		for (final Placeholder placeholder : placeholders) {
			name = placeholder.process(name);
			value = placeholder.process(value);
		}
		
		reply(successEmbed.addField(name, value, false), success);
	}
	
	public void loading(String name, String value) {
		reply(loadingEmbed.addField(name, value, false));
	}
	
	public void error(String name, String value) {
		reply(errorEmbed.addField(name, value, false));
	}
	
	public void error(String title, String name, String value) {
		reply(errorEmbed.setTitle(title).addField(name, value, false));
	}
	
	public void error(String title, String name, String value, final Placeholder... placeholders) {
		for (final Placeholder placeholder : placeholders) {
			title = placeholder.process(title);
			name = placeholder.process(name);
			value = placeholder.process(value);
		}
		
		error(title, name, value);
	}
	
	public void error(String name, String value, final Placeholder... placeholders) {
		for (final Placeholder placeholder : placeholders) {
			name = placeholder.process(name);
			value = placeholder.process(value);
		}
		
		error(name, value);
	}
	
	public void exception() {
		error("errorhappened", "somethingwentwrong");
	}
	
	public void reply(EmbedBuilder builder) {
		reply(builder, null, null);
	}
	
	public void reply(EmbedBuilder builder, Consumer<? super BlackMessage> success) {
		reply(builder, success, null);
	}
	
	public void reply(EmbedBuilder builder, Consumer<? super BlackMessage> success, Consumer<? super Throwable> error) {
		message.reply(builder.build()).queue(msg -> success.accept(BlackMessage.from(msg)), error);
	}
	
	public void sendPleaseUse() {
		sendPleaseUse(null, null);
	}
	
	public void sendPleaseUse(Consumer<? super BlackMessage> success) {
		sendPleaseUse(success, null);
	}
	
	public void sendPleaseUse(Consumer<? super BlackMessage> success, Consumer<? super Throwable> error) {
		reply(getWrongArgument(), success, error);
	}

	public static String getPleaseUse(BlackGuild guild, BlackUser author, Command command) {
		return LanguageSystem.getTranslation("pleaseuse", author, guild).replace("%command%", getCommandHelp(guild, author, command));
	}

	public static String getCommandHelp(BlackGuild guild, BlackUser author, Command command) {
		final String syntax = command.getSyntax();
		return "`" + guild.getPrefix() + command.getCommand()[0] + (syntax != null && !syntax.equalsIgnoreCase("") ? " " + syntax : "") + "`";
	}

	public EmbedBuilder getWrongArgument() {
		return errorEmbed.addField("wrongargument", getPleaseUse(this.guild, this.user, this.command), false);
	}
	
	public String getTranslation(String key) {
		return language.getTranslatedString(key);
	}
}