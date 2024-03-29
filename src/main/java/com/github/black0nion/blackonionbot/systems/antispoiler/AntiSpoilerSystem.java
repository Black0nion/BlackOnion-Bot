package com.github.black0nion.blackonionbot.systems.antispoiler;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem.AntiSpoilerType.*;

public class AntiSpoilerSystem extends ListenerAdapter {

	private final LanguageSystem languageSystem;
	private final EmbedUtils embedUtils;
	private final GuildSettingsRepo guildSettingsRepo;
	private final UserSettingsRepo userSettingsRepo;

	public AntiSpoilerSystem(LanguageSystem languageSystem, EmbedUtils embedUtils, GuildSettingsRepo guildSettingsRepo, UserSettingsRepo userSettingsRepo) {
		this.languageSystem = languageSystem;
		this.embedUtils = embedUtils;
		this.guildSettingsRepo = guildSettingsRepo;
		this.userSettingsRepo = userSettingsRepo;
	}

	/**
	 * @return if the message contained a spoiler
	 */
	public boolean removeSpoilers(@Nullable MessageReceivedEvent event, @Nullable MessageUpdateEvent event1) {
		final Message msg = event != null ? event.getMessage() : event1.getMessage();
		final String message = msg.getContentRaw();
		final User author = event != null ? event.getAuthor() : event1.getAuthor();

		if (author.isBot()) return false;

		final TextChannel channel = event != null ? event.getChannel().asTextChannel() : event1.getChannel().asTextChannel();
		final Guild guild = event != null ? event.getGuild() : event1.getGuild();

		final UserSettings userSettings = userSettingsRepo.getSettings(author);

		final GuildSettings guildSettings = guildSettingsRepo.getSettings(guild);
		final AntiSpoilerType type = guildSettings.getAntiSpoiler().getValue();

		return handleSystem(event, guild, msg, message, author, channel, message, type, guildSettings, userSettings);
	}

	public boolean handleSystem(MessageReceivedEvent event, Guild guild, Message msg, String message, User author, TextChannel channel, String newMessage, AntiSpoilerType type, GuildSettings guildSettings, UserSettings userSettings) {
		if (type != OFF) {
			long count = message.chars().filter(c -> c == '|').count();
			if (count < 4) return false;

			if (Utils.handleSelfRights(languageSystem, guild, guildSettings, author, userSettings, channel, null, Permission.MESSAGE_MANAGE))
				return false;

			msg.delete().queue();
			if (type == DELETE) return true;

			while (count >= 4) {
				newMessage = newMessage.replaceFirst("\\|\\|", "");
				newMessage = newMessage.replaceFirst("\\|\\|", "");
				count -= 4;
			}

			final String finalNewMessage = newMessage;

			if (type == REPLACE) {
				try {
					channel.retrieveWebhooks().queue(webhooks -> {
						try {
							WebhookClient client = Utils.makeWebhookClient(Utils.getWebhook(channel, webhooks));
							final WebhookMessageBuilder builder = new WebhookMessageBuilder();
							builder.setUsername(Utils.escapeMarkdown(author.getAsTag()));
							builder.setContent(finalNewMessage);
							builder.setAvatarUrl(author.getAvatarUrl());
							client.send(builder.build());
							client.close();
						} catch (final Exception e) {
							e.printStackTrace();
						}
					});
					return true;
				} catch (final Exception e) {
					e.printStackTrace();
				}
			} else {
				channel.sendMessageEmbeds(embedUtils.getErrorEmbed(author, userSettings, guildSettings).addField("erroroccurred", "somethingwentwrong", false).build()).queue();
			}
		}
		return false;
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		removeSpoilers(event, null);
	}

	@Override
	public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
		removeSpoilers(null, event);
	}

	public enum AntiSpoilerType {
		DELETE,
		REPLACE,
		OFF;

		public static AntiSpoilerType parse(final String input) {
			if (input == null || input.isEmpty()) return null;
			try {
				return valueOf(input.toUpperCase());
			} catch (Exception ignored) {
				return null;
			}
		}
	}
}