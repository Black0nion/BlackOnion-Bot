package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.utils.DummyException;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.github.black0nion.blackonionbot.utils.EmbedUtils.*;

public class CommandEvent {

	private TextCommand command;
	private final MessageReceivedEvent event;
	private final JDA jda;
	private final BlackGuild guild;
	private final TextChannel channel;
	private final Message message;
	private final BlackMember member;
	private final BlackUser user;
	private final TranslatedEmbed successEmbed;
	private final TranslatedEmbed loadingEmbed;
	private final TranslatedEmbed errorEmbed;
	private final Language language;

	@Deprecated
	public CommandEvent(final TextCommand cmd, final MessageReceivedEvent e) {
		this(cmd, e, BlackGuild.from(e.getGuild()), e.getMessage(), BlackMember.from(e.getMember()), BlackUser.from(e.getAuthor()));
	}

	public CommandEvent(final MessageReceivedEvent e, final BlackGuild guild, final Message message, final BlackMember member, final BlackUser user) {
		this(null, e, guild, message, member, user);
	}

	public CommandEvent(final TextCommand cmd, final MessageReceivedEvent e, final BlackGuild guild, final Message message, final BlackMember member, final BlackUser user) {
		this.command = cmd;
		this.event = e;
		this.jda = e.getJDA();
		this.guild = guild;
		this.channel = e.getTextChannel();
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

	public TranslatedEmbed success() {
		return new TranslatedEmbed(this.successEmbed);
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

	public TranslatedEmbed loading() {
		return new TranslatedEmbed(this.loadingEmbed);
	}

	public void loading(final Consumer<? super Message> success) {
		this.reply(this.loading(), success);
	}

	public TranslatedEmbed error() {
		return new TranslatedEmbed(this.errorEmbed);
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

	public void exception(@Nullable Throwable t) {
		if (t != null && !(t instanceof DummyException)) LoggerFactory.getLogger(this.getClass()).error("Exception in command", t);
		this.error("errorhappened", t != null ? (t instanceof DummyException ? "" : t.getClass().getSimpleName() + ": ") + t.getMessage() : "null");
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

	public void sendPleaseUse(final Consumer<? super Message> success, final Consumer<? super Throwable> error) {
		this.reply(this.getWrongArgument(), success, error);
	}

	public static String getPleaseUse(final BlackGuild guild, final BlackUser author, final TextCommand command) {
		return LanguageSystem.getTranslation("pleaseuse", author, guild).replace("%command%", getCommandHelp(guild, command));
	}

	public static String getCommandHelp(final BlackGuild guild, final TextCommand command) {
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

	public TextCommand getCommand() {
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

	public JDA getJda() {
		return this.jda;
	}

	public void setCommand(final TextCommand cmd) {
		this.command = cmd;
	}
}