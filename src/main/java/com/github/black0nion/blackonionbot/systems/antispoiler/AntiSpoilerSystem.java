package com.github.black0nion.blackonionbot.systems.antispoiler;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType.*;

public class AntiSpoilerSystem {
	/**
	 * @return if the message contained a spoiler
	 */
	@SuppressWarnings("ConstantConditions")
	public static boolean removeSpoilers(final @NotNull SlashCommandEvent event, @NotNull SlashCommandInteractionEvent interactionEvent) {
		final BlackGuild guild = event.getGuild();
		final BlackUser author = event.getUser();
		final Message msg = interactionEvent.getTextChannel().retrieveMessageById(interactionEvent.getTextChannel().getLatestMessageIdLong()).complete();
		final String message = msg.getContentRaw();
		final TextChannel channel = event.getChannel();
		final AntiSpoilerType type = guild.getAntiSpoilerType();
		String newMessage = message;

		if (type != OFF) {
			long count = message.chars().filter(c -> c == '|').count();
			if (count < 4) return false;

			if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_MANAGE)) return false;

			msg.delete().queue();
			if (type == DELETE) return true;

			if (Utils.handleRights(guild, author, channel, Permission.MANAGE_WEBHOOKS)) return true;

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
							builder.setUsername(author.getEscapedEffectiveName());
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
				channel.sendMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
			}
		}
		return false;
	}
}