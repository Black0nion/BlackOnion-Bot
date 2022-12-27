package com.github.black0nion.blackonionbot.commands.common.utils.event;

import com.github.black0nion.blackonionbot.utils.CommandReturnException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface UserRespondUtils extends TranslationUtils {

	default TranslatedEmbedBuilder success() {
		return new TranslatedEmbedBuilder(this.getDefaultSuccessEmbed());
	}

	default void success(final String name, final String value) {
		this.reply(this.success().addField(name, value, false));
	}

	default void success(final String title, final String name, final String value) {
		this.success(title, name, value, new Placeholder[0]);
	}

	default void success(final String title, final String name, final String value, final Consumer<InteractionHook> msg) {
		this.reply(this.success().setTitle(title).addField(name, value, false), msg);
	}

	default void success(final String title, final String url, final String name, final String value) {
		this.reply(this.success().setTitle(title, url).addField(name, value, false));
	}

	default void success(final String name, final String value, final Placeholder... placeholders) {
		this.success(null, name, value, placeholders);
	}

	default void success(String title, String name, String value, final Placeholder... placeholders) {
		this.doReply(this.success(), title, name, value, placeholders);
	}

	default TranslatedEmbedBuilder error() {
		return new TranslatedEmbedBuilder(this.getDefaultErrorEmbed());
	}

	default void error(final String name, final String value) {
		this.error(null, name, value);
	}

	default void error(final String title, final String name, final String value) {
		this.error(title, name, value, new Placeholder[0]);
	}

	default void error(String name, String value, final Placeholder... placeholders) {
		this.error(null, name, value, placeholders);
	}

	default void error(String title, String name, String value, final Placeholder... placeholders) {
		this.doReply(this.error(), title, name, value, placeholders);
	}

	private void doReply(TranslatedEmbedBuilder embed, String title, String name, String value, final Placeholder... placeholders) {
		if (title != null) title = getLanguage().getTranslationNonNull(title);
		if (name != null) name = getLanguage().getTranslationNonNull(name);
		if (value != null) value = getLanguage().getTranslationNonNull(value);
		if (!(value == null && name == null && title == null)) {
			for (final Placeholder placeholder : placeholders) {
				title = placeholder.process(title);
				name = placeholder.process(name);
				value = placeholder.process(value);
			}
		}

		this.reply(embed.setTitle(title).addField(name, value, false));
	}

	/**
	 * Sends an error embed with two generic error messages
	 */
	default void exception() {
		this.error("erroroccurred", "somethingwentwrong");
	}

	default void exception(@Nullable Throwable t) {
		long id = System.currentTimeMillis() + (long) (Math.random() * 1000);
		if (t != null && !(t instanceof CommandReturnException)) this.logError(t, id);

		if (this.getEvent() != null && this.getEvent().isAcknowledged()) return;
		this.send("errorwithid", new Placeholder("id", id));
	}

	default void reply(final EmbedBuilder builder) {
		this.reply(builder, null);
	}

	default void reply(final EmbedBuilder builder, final Consumer<InteractionHook> result) {
		this.reply(builder, isEphemeral(), result);
	}

	default void reply(final MessageEmbed embed, Consumer<InteractionHook> result) {
		this.reply(embed, isEphemeral(), result);
	}

	default void reply(final EmbedBuilder builder, boolean ephemeral, final Consumer<InteractionHook> result) {
		this.reply(builder.build(), ephemeral, result);
	}

	default void reply(final MessageEmbed embed, boolean ephemeral, final Consumer<InteractionHook> result) {
		try {
			this.getEvent().replyEmbeds(embed).setEphemeral(ephemeral).queue(result);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	//endregion

	default void send(final Object obj) {
		this.send(obj != null ? obj.toString() : null);
	}

	default void send(final String message) {
		this.send(message, new Placeholder[0]);
	}

	default void send(final String message, final Placeholder... placeholders) {
		this.send(message, null, placeholders);
	}

	default void send(final String message, final Consumer<InteractionHook> result) {
		this.send(message, result, new Placeholder[0]);
	}

	default void send(final String message, final Consumer<InteractionHook> result, final Placeholder... placeholders) {
		this.getEvent().reply(getTranslation(message, placeholders)).setEphemeral(isEphemeral()).queue(result);
	}

	void logError(Throwable t, long id);

	boolean isEphemeral();

	IReplyCallback getEvent();

	TranslatedEmbedBuilder getDefaultSuccessEmbed();

	TranslatedEmbedBuilder getDefaultErrorEmbed();
}
