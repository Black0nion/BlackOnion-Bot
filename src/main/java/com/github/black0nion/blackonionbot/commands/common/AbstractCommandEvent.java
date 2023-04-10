package com.github.black0nion.blackonionbot.commands.common;

import com.github.black0nion.blackonionbot.commands.common.utils.event.UserRespondUtils;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageUtils;
import com.github.black0nion.blackonionbot.utils.CommandReturnException;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * A generic class that handels the execution of a command.
 * <br>
 * Removes a lot of boilerplate handling slash command events, like responding, passing loads of JDA objects, etc.
 * Can often times be used instead of splitting it up into multiple parameters for methods.
 *
 * @see SlashCommandEvent#send(String)
 * @see SlashCommandEvent#reply(EmbedBuilder)
 * @see SlashCommandEvent#exception()
 * @param <C> the command class that extends this class
 * @param <E> the type of the event that is handled by this class
 */
public abstract class AbstractCommandEvent<C extends AbstractCommand<?, ?>, E extends GenericCommandInteractionEvent> implements UserRespondUtils {

	protected C command;
	protected final E event;
	protected final JDA jda;
	protected final Guild guild;
	protected final Member member;
	protected final User user;
	protected final UserSettings userSettings;
	protected final TranslatedEmbedBuilder successEmbed;
	protected final TranslatedEmbedBuilder errorEmbed;
	protected Language language;

	protected AbstractCommandEvent(final Language defaultLanguage, final C cmd, final E e, final Guild guild, final Member member, final User user, final UserSettings userSettings, final GuildSettings guildSettings) {
		this.command = cmd;
		this.event = e;
		this.jda = e.getJDA();
		this.guild = guild;
		this.member = member;
		this.user = user;
		this.userSettings = userSettings;
		this.successEmbed = EmbedUtils.getSuccessEmbed(defaultLanguage, this.user, userSettings, guildSettings);
		this.errorEmbed = EmbedUtils.getErrorEmbed(defaultLanguage, this.user, userSettings, guildSettings);
		this.language = LanguageUtils.getLanguage(userSettings, guildSettings, defaultLanguage);
	}

	@Override
	public TranslatedEmbedBuilder getDefaultErrorEmbed() {
		return errorEmbed;
	}

	@Override
	public TranslatedEmbedBuilder getDefaultSuccessEmbed() {
		return successEmbed;
	}

	/**
	 * @return the language, userid -> guildid -> default
	 */
	public Language getLanguage() {
		return this.language;
	}

	// useful in the LanguageCommand
	public void setLanguage(Language language) {
		this.language = language;
		this.successEmbed.setLanguage(language);
		this.errorEmbed.setLanguage(language);
	}

	public String getPleaseUse() {
		return language.getTranslation("pleaseuse", new Placeholder("%command%", "\n" + getCommandHelp()));
	}

	public String getCommandHelp() {
		return command.getData().getName();
	}

	public String getTranslationOrEmpty(final String key) {
		final String translation = this.language.getTranslation(key);
		return translation != null ? translation : this.language.getTranslationNonNull("empty");
	}

	public void handlePerms(Permission... permissions) {
		if (!this.member.hasPermission(permissions)) {
			this.send("missingpermissions", new Placeholder("perms", Utils.getPermissionString(permissions)));
			throw new CommandReturnException();
		}
	}

	//region Getters / Setters
	public C getCommand() {
		return this.command;
	}

	public User getUser() {
		return this.user;
	}

	public Guild getGuild() {
		return this.guild;
	}

	public Member getMember() {
		return this.member;
	}

	@Nonnull
	public E getEvent() {
		return this.event;
	}

	public JDA getJDA() {
		return this.jda;
	}

	public void setCommand(final C cmd) {
		this.command = cmd;
	}
	//endregion

	@Override
	public void logError(final Throwable e, final long id) {
		LoggerFactory.getLogger(this.command != null ? this.command.getClass() : this.getClass()).error("Exception in command (id: {})", id, e);
	}

	public void sendPleaseUse() {
		this.error("wrongargument", this.getPleaseUse());
	}

	@Override
	public boolean isEphemeral() {
		return command.isEphemeral();
	}
}
