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

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.blackobjects.BlackEmbed;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_ Date: 26.05.2021 Copyright 2021 BlackOnion Class Name:
 *         CommandEvent
 */
public class CommandEvent {

    private Command command;
    private final GuildMessageReceivedEvent event;
    private final JDA jda;
    private final BlackGuild guild;
    private final TextChannel channel;
    private final Message message;
    private final BlackMember member;
    private final BlackUser user;
    private final BlackEmbed successEmbed;
    private final BlackEmbed loadingEmbed;
    private final BlackEmbed errorEmbed;
    private final Language language;

    @Deprecated
    public CommandEvent(final Command cmd, final GuildMessageReceivedEvent e) {
	this(cmd, e, BlackGuild.from(e.getGuild()), e.getMessage(), BlackMember.from(e.getMember()), BlackUser.from(e.getAuthor()));
    }

    public CommandEvent(final GuildMessageReceivedEvent e, final BlackGuild guild, final Message message, final BlackMember member, final BlackUser user) {
	this(null, e, guild, message, member, user);
    }

    public CommandEvent(final Command cmd, final GuildMessageReceivedEvent e, final BlackGuild guild, final Message message, final BlackMember member, final BlackUser user) {
	this.command = cmd;
	this.event = e;
	this.jda = e.getJDA();
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
	return this.language;
    }

    public BlackEmbed success() {
	return new BlackEmbed(this.successEmbed);
    }

    public void success(final String name, final String value) {
	this.reply(this.success().addField(name, value, false));
    }

    public void success(final String title, final String name, final String value) {
	this.reply(this.success().setTitle(title).addField(name, value, false));
    }

    public void success(final String title, final String url, final String name, final String value) {
	this.reply(this.success().setTitle(title, url).addField(name, value, false));
    }

    public void success(final String title, final String name, final String value, final Placeholder... placeholders) {
	this.success(title, name, value, null, placeholders);
    }

    public void success(String title, String name, String value, final Consumer<? super Message> success, final Placeholder... placeholders) {
	title = this.language.getTranslationNonNull(title);
	name = this.language.getTranslationNonNull(name);
	value = this.language.getTranslationNonNull(value);
	for (final Placeholder placeholder : placeholders) {
	    title = placeholder.process(title);
	    name = placeholder.process(name);
	    value = placeholder.process(value);
	}

	this.reply(this.success().setTitle(title).addField(name, value, false), success);
    }

    public void success(String name, String value, final Placeholder... placeholders) {
	name = this.language.getTranslationNonNull(name);
	value = this.language.getTranslationNonNull(value);
	for (final Placeholder placeholder : placeholders) {
	    name = placeholder.process(name);
	    value = placeholder.process(value);
	}

	this.reply(this.success().addField(name, value, false), null);
    }

    public void success(String name, String value, final Consumer<? super Message> success, final Placeholder... placeholders) {
	name = this.language.getTranslationNonNull(name);
	value = this.language.getTranslationNonNull(value);
	for (final Placeholder placeholder : placeholders) {
	    name = placeholder.process(name);
	    value = placeholder.process(value);
	}

	this.reply(this.success().addField(name, value, false), success);
    }

    public BlackEmbed loading() {
	return new BlackEmbed(this.loadingEmbed);
    }

    public void loading(final Consumer<? super Message> success) {
	this.reply(this.loading(), success);
    }

    public void loading(final String name, final String value) {
	this.reply(this.loading().addField(name, value, false));
    }

    public void loading(final String name, final String value, final Consumer<? super Message> success) {
	this.reply(this.loading().addField(name, value, false), success);
    }

    public BlackEmbed error() {
	return new BlackEmbed(this.errorEmbed);
    }

    public void error(final String name, final String value) {
	this.reply(this.error().addField(name, value, false));
    }

    public void error(final String title, final String name, final String value) {
	this.reply(this.error().setTitle(title).addField(name, value, false));
    }

    public void error(final String name, final String value, final Consumer<? super Message> success) {
	this.reply(this.error().addField(name, value, false), success);
    }

    public void error(String title, String name, String value, final Placeholder... placeholders) {
	title = this.language.getTranslationNonNull(title);
	name = this.language.getTranslationNonNull(name);
	value = this.language.getTranslationNonNull(value);
	for (final Placeholder placeholder : placeholders) {
	    title = placeholder.process(title);
	    name = placeholder.process(name);
	    value = placeholder.process(value);
	}

	this.error(title, name, value);
    }

    public void error(String name, String value, final Placeholder... placeholders) {
	name = this.language.getTranslationNonNull(name);
	value = this.language.getTranslationNonNull(value);
	for (final Placeholder placeholder : placeholders) {
	    name = placeholder.process(name);
	    value = placeholder.process(value);
	}

	this.error(name, value);
    }

    public void exception() {
	this.error("errorhappened", "somethingwentwrong");
    }

    public void selfDestructingException() {
	this.error("errorhappened", "somethingwentwrong", msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }

    public void reply(final EmbedBuilder builder) {
	this.reply(builder, null, null);
    }

    public void reply(final EmbedBuilder builder, final Consumer<? super Message> success) {
	this.reply(builder, success, null);
    }

    public void reply(final EmbedBuilder builder, final Consumer<? super Message> success, final Consumer<? super Throwable> error) {
	this.message.replyEmbeds(builder.build()).queue(msg -> {
	    if (success != null) {
		success.accept(msg);
	    }
	}, error);
    }

    public void sendPleaseUse() {
	this.sendPleaseUse(null, null);
    }

    public void sendPleaseUse(final Consumer<? super Message> success) {
	this.sendPleaseUse(success, null);
    }

    public void sendPleaseUse(final Consumer<? super Message> success, final Consumer<? super Throwable> error) {
	this.reply(this.getWrongArgument(), success, error);
    }

    public static String getPleaseUse(final BlackGuild guild, final BlackUser author, final Command command) {
	return LanguageSystem.getTranslation("pleaseuse", author, guild).replace("%command%", getCommandHelp(guild, author, command));
    }

    public static String getCommandHelp(final BlackGuild guild, final BlackUser author, final Command command) {
	final String syntax = command.getSyntax();
	return guild.getPrefix() + command.getCommand()[0] + (syntax != null && !syntax.equalsIgnoreCase("") ? " " + syntax : "");
    }

    public EmbedBuilder getWrongArgument() {
	return this.error().addField("wrongargument", getPleaseUse(this.guild, this.user, this.command), false);
    }

    public String getTranslation(final String key) {
	return this.language.getTranslationNonNull(key);
    }

    public String getTranslation(final String key, final Placeholder... placeholders) {
	String result = this.getTranslation(key);
	for (final Placeholder placeholder : placeholders) {
	    result = placeholder.process(result);
	}
	return result;
    }

    public String getTranslationOrEmpty(final String key) {
	final String translation = this.language.getTranslation(key);
	return translation != null ? translation : this.language.getTranslationNonNull("empty");
    }

    public Message getMessage() {
	return this.message;
    }

    public TextChannel getChannel() {
	return this.channel;
    }

    public Command getCommand() {
	return this.command;
    }

    public BlackUser getUser() {
	return this.user;
    }

    public BlackGuild getGuild() {
	return this.guild;
    }

    public BlackMember getMember() {
	return this.member;
    }

    @Nullable
    public GuildMessageReceivedEvent getEvent() {
	return this.event;
    }

    public JDA getJda() {
	return this.jda;
    }

    public void setCommand(final Command cmd) {
	this.command = cmd;
    }
}