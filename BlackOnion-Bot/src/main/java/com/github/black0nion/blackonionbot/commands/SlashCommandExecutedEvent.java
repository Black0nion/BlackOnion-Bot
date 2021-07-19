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

import java.util.stream.Collectors;

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
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * @author _SIM_ Date: 26.05.2021 Copyright 2021 BlackOnion Class Name:
 *         CommandEvent
 */
public class SlashCommandExecutedEvent {

    private SlashCommand command;
    private final net.dv8tion.jda.api.events.interaction.SlashCommandEvent event;
    private final JDA jda;
    private final BlackGuild guild;
    private final TextChannel channel;
    private final BlackMember member;
    private final BlackUser user;
    private final BlackEmbed successEmbed;
    private final BlackEmbed loadingEmbed;
    private final BlackEmbed errorEmbed;
    private final Language language;

    public SlashCommandExecutedEvent(final net.dv8tion.jda.api.events.interaction.SlashCommandEvent e, final BlackGuild guild, final BlackMember member, final BlackUser user) {
	this(null, e, guild, member, user);
    }

    public SlashCommandExecutedEvent(final SlashCommand cmd, final net.dv8tion.jda.api.events.interaction.SlashCommandEvent e, final BlackGuild guild, final BlackMember member, final BlackUser user) {
	this.command = cmd;
	this.event = e;
	this.jda = e.getJDA();
	this.guild = guild;
	this.channel = e.getTextChannel();
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

    public void successPrivate(final String name, final String value) {
	this.replyPrivate(this.success().addField(name, value, false));
    }

    public void success(final String title, final String name, final String value) {
	this.reply(this.success().setTitle(title).addField(name, value, false));
    }

    public void success(final String title, final String url, final String name, final String value) {
	this.reply(this.success().setTitle(title, url).addField(name, value, false));
    }

    public void success(String title, String name, String value, final Placeholder... placeholders) {
	title = this.language.getTranslationNonNull(title);
	name = this.language.getTranslationNonNull(name);
	value = this.language.getTranslationNonNull(value);
	for (final Placeholder placeholder : placeholders) {
	    title = placeholder.process(title);
	    name = placeholder.process(name);
	    value = placeholder.process(value);
	}

	this.reply(this.success().setTitle(title).addField(name, value, false));
    }

    public void success(String name, String value, final Placeholder... placeholders) {
	name = this.language.getTranslationNonNull(name);
	value = this.language.getTranslationNonNull(value);
	for (final Placeholder placeholder : placeholders) {
	    name = placeholder.process(name);
	    value = placeholder.process(value);
	}

	this.reply(this.success().addField(name, value, false));
    }

    public BlackEmbed loading() {
	return new BlackEmbed(this.loadingEmbed);
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

    public void privateException() {
	this.reply(this.error().addField("errorhappened", "somethingwentwrong", false));
    }

    public void reply(final EmbedBuilder builder) {
	this.event.replyEmbeds(builder.build()).queue();
    }

    public void replyPrivate(final EmbedBuilder builder) {
	this.event.replyEmbeds(builder.build()).setEphemeral(true).queue();
    }

    public void sendPleaseUse() {
	this.reply(this.getWrongArgument());
    }

    public void sendPleaseUsePrivate() {
	this.replyPrivate(this.getWrongArgument());
    }

    public static String getPleaseUse(final BlackGuild guild, final BlackUser author, final SlashCommand command) {
	return LanguageSystem.getTranslation("pleaseuse", author, guild).replace("%command%", getCommandHelp(guild, author, command));
    }

    public static String getCommandHelp(final BlackGuild guild, final BlackUser author, final SlashCommand command) {
	return command.getData().getOptions().stream().map(data -> data.getName() + " - " + data.getDescription() + " : " + data.getType().name()).collect(Collectors.joining("\n"));
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

    public TextChannel getChannel() {
	return this.channel;
    }

    public SlashCommand getCommand() {
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
    public net.dv8tion.jda.api.events.interaction.SlashCommandEvent getEvent() {
	return this.event;
    }

    public JDA getJda() {
	return this.jda;
    }

    public void setCommand(final SlashCommand cmd) {
	this.command = cmd;
    }
}