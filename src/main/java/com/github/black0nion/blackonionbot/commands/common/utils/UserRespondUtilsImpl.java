package com.github.black0nion.blackonionbot.commands.common.utils;

import com.github.black0nion.blackonionbot.commands.common.utils.event.UserRespondUtils;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageUtils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.slf4j.LoggerFactory;

import static com.github.black0nion.blackonionbot.utils.EmbedUtils.getErrorEmbed;
import static com.github.black0nion.blackonionbot.utils.EmbedUtils.getSuccessEmbed;

public class UserRespondUtilsImpl implements UserRespondUtils {

	private final IReplyCallback callback;
	private final Language language;
	private final TranslatedEmbedBuilder errorEmbed;
	private final TranslatedEmbedBuilder successEmbed;

	public UserRespondUtilsImpl(final IReplyCallback callback, final GuildSettings guildSettings, final User user, final UserSettings userSettings, final Language defaultLanguage) {
		this.callback = callback;
		this.successEmbed = getSuccessEmbed(defaultLanguage, user, userSettings, guildSettings);
		this.errorEmbed = getErrorEmbed(defaultLanguage, user, userSettings, guildSettings);
		this.language = LanguageUtils.getLanguage(userSettings, guildSettings, defaultLanguage);
	}

	@Override
	public Language getLanguage() {
		return language;
	}

	@Override
	public void logError(Throwable t, long id) {
		LoggerFactory.getLogger(this.getClass()).error("Exception while trying to reply (id: {})", id, t);
	}

	@Override
	public boolean isEphemeral() {
		return false;
	}

	@Override
	public IReplyCallback getEvent() {
		return callback;
	}

	@Override
	public TranslatedEmbedBuilder getDefaultSuccessEmbed() {
		return successEmbed;
	}

	@Override
	public TranslatedEmbedBuilder getDefaultErrorEmbed() {
		return errorEmbed;
	}
}
