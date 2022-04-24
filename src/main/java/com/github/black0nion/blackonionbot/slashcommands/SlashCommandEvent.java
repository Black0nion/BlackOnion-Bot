package com.github.black0nion.blackonionbot.slashcommands;

import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.DummyException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.black0nion.blackonionbot.utils.EmbedUtils.*;

@SuppressWarnings("unused")
public class SlashCommandEvent {

	private SlashCommand command;
	private final SlashCommandInteractionEvent event;
	private final JDA jda;
	private final BlackGuild guild;
	private final TextChannel channel;
	private final BlackMember member;
	private final BlackUser user;
	private final TranslatedEmbed successEmbed;
	private final TranslatedEmbed loadingEmbed;
	private final TranslatedEmbed errorEmbed;
	private Language language;

	public SlashCommandEvent(final SlashCommandInteractionEvent e, final BlackGuild guild, final BlackMember member, final BlackUser user) {
		this(null, e, guild, member, user);
	}

	public SlashCommandEvent(final SlashCommand cmd, final SlashCommandInteractionEvent e, final BlackGuild guild, final BlackMember member, final BlackUser user) {
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

	// useful in the LanguageCommand
	public void setLanguage(Language language) {
		this.language = language;
	}

	//region Embeds
	public TranslatedEmbed success() {
		return new TranslatedEmbed(this.successEmbed);
	}

	public void success(final String name, final String value) {
		this.reply(this.success().addField(name, value, false));
	}

	public void success(final String title, final String name, final String value) {
		this.success(title, name, value, new Placeholder[0]);
	}

	public void success(final String title, final String name, final String value, final Consumer<InteractionHook> msg) {
		this.reply(this.success().setTitle(title).addField(name, value, false), msg);
	}

	public void success(final String title, final String url, final String name, final String value) {
		this.reply(this.success().setTitle(title, url).addField(name, value, false));
	}

	public void success(final String name, final String value, final Placeholder... placeholders) {
		this.success(null, name, value, placeholders);
	}

	public void success(String title, String name, String value, final Placeholder... placeholders) {
		this.doReply(this.success(), title, name, value, placeholders);
	}

	public TranslatedEmbed loading() {
		return new TranslatedEmbed(this.loadingEmbed);
	}

	public TranslatedEmbed error() {
		return new TranslatedEmbed(this.errorEmbed);
	}

	public void error(final String name, final String value) {
		this.error(null, name, value);
	}

	public void error(final String title, final String name, final String value) {
		this.error(title, name, value, new Placeholder[0]);
	}

	public void error(String name, String value, final Placeholder... placeholders) {
		this.error(null, name, value, placeholders);
	}

	public void error(String title, String name, String value, final Placeholder... placeholders) {
		this.doReply(this.error(), title, name, value, placeholders);
	}

	private void doReply(TranslatedEmbed embed, String title, String name, String value, final Placeholder... placeholders) {
		if (title != null) title = this.language.getTranslationNonNull(title);
		if (name != null) name = this.language.getTranslationNonNull(name);
		if (value != null) value = this.language.getTranslationNonNull(value);
		if (!(value == null && name == null && title == null)) {
			for (final Placeholder placeholder : placeholders) {
				title = placeholder.process(title);
				name = placeholder.process(name);
				value = placeholder.process(value);
			}
		}

		this.reply(embed.setTitle(title).addField(name, value, false));
	}

	public void exception() {
		this.error("errorhappened", "somethingwentwrong");
	}

	public void exception(@Nullable Throwable t) {
		if (t != null && !(t instanceof DummyException)) LoggerFactory.getLogger(this.getClass()).error("Exception in command", t);
		this.send("errorwithmessage", new Placeholder("msg", t != null ? (t instanceof DummyException ? "" : t.getClass().getSimpleName() + ": ") + t.getMessage() : "null"));
	}

	public void reply(final EmbedBuilder builder) {
		this.reply(builder, null);
	}

	public void reply(final EmbedBuilder builder, final Consumer<InteractionHook> result) {
		this.reply(builder, command.isEphemeral(), result);
	}

	public void reply(final MessageEmbed embed, Consumer<InteractionHook> result) {
		this.reply(embed, command.isEphemeral(), result);
	}

	public void reply(final EmbedBuilder builder, boolean ephemeral, final Consumer<InteractionHook> result) {
		this.reply(builder.build(), ephemeral, result);
	}

	public void reply(final MessageEmbed embed, boolean ephemeral, final Consumer<InteractionHook> result) {
		try {
			this.event.replyEmbeds(embed).setEphemeral(ephemeral).queue(result);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void sendPleaseUse() {
		this.error("wrongargument", this.getPleaseUse());
	}
	//endregion

	public void send(final String message) {
		this.send(message, new Placeholder[0]);
	}

	public void send(final String message, final Placeholder... placeholders) {
		this.send(message, null, placeholders);
	}

	public void send(final String message, final Consumer<InteractionHook> result) {
		this.send(message, result, new Placeholder[0]);
	}

	public void send(final String message, final Consumer<InteractionHook> result, final Placeholder... placeholders) {
		this.event.reply(getTranslation(message, placeholders)).setEphemeral(this.command.isEphemeral()).queue(result);
	}

	public void send(final Message message, final Consumer<InteractionHook> result) {
		this.event.reply(message).setEphemeral(this.command.isEphemeral()).queue(result, System.err::println);
	}

	private String getPleaseUse() {
		return getPleaseUse(this.guild, this.user, this.command);
	}

	public static String getPleaseUse(final BlackGuild guild, final BlackUser author, final SlashCommand command) {
		return LanguageSystem.getTranslation("pleaseuse", author, guild).replace("%command%", "\n" + getCommandHelp(command));
	}

	public static String getCommandHelp(final SlashCommand command) {
		return "/" + command.getData().getName() + " " + command.getData().getOptions().stream().map(data -> data.getName() + " : " + data.getType().name()).collect(Collectors.joining(", "));
	}

	public String getTranslation(final String key) {
		return this.language.getTranslationNonNull(key);
	}

	public String getTranslation(final String key, final Placeholder... placeholders) {
		return Placeholder.process(this.getTranslation(key), placeholders);
	}

	public String getTranslationOrEmpty(final String key) {
		final String translation = this.language.getTranslation(key);
		return translation != null ? translation : this.language.getTranslationNonNull("empty");
	}


	public void handlePerms(Permission... permissions) {
		if (!this.member.hasPermission(permissions)) {
			this.send("missingpermissions", new Placeholder("perms", Utils.getPermissionString(permissions)));
			throw new DummyException();
		}
	}

	//region Getters / Setters
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
	public SlashCommandInteractionEvent getEvent() {
		return this.event;
	}

	public JDA getJDA() {
		return this.jda;
	}

	public void setCommand(final SlashCommand cmd) {
		this.command = cmd;
	}
	//endregion
}