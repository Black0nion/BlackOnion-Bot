package com.github.black0nion.blackonionbot.commands.common.utils;

import com.github.black0nion.blackonionbot.commands.common.utils.event.UserRespondUtils;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.slf4j.LoggerFactory;

import static com.github.black0nion.blackonionbot.utils.EmbedUtils.getErrorEmbed;
import static com.github.black0nion.blackonionbot.utils.EmbedUtils.getSuccessEmbed;

public class UserRespondUtilsImpl implements UserRespondUtils {

	private final IReplyCallback callback;
	private final Language language;
	private final TranslatedEmbedBuilder errorEmbed;
	private final TranslatedEmbedBuilder successEmbed;

	public UserRespondUtilsImpl(final IReplyCallback callback, final BlackGuild guild, final BlackUser user) {
		this.callback = callback;
		this.successEmbed = getSuccessEmbed(user, guild);
		this.errorEmbed = getErrorEmbed(user, guild);
		this.language = LanguageSystem.getLanguage(user, guild);
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
