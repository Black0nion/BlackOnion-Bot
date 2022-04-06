package com.github.black0nion.blackonionbot.systems.antispoiler;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType.*;

public class AntiSpoilerSystem {
	/**
	 * @return if the message contained a spoiler
	 */
	@SuppressWarnings("ConstantConditions")
	public static boolean removeSpoilers(final CommandEvent event) {
		final BlackGuild guild = event.getGuild();
		final Message msg = event.getMessage();
		final String message = msg.getContentRaw();
		final BlackUser author = event.getUser();
		final TextChannel channel = event.getChannel();
		String newMessage = message;
		final AntiSpoilerType type = guild.getAntiSpoilerType();

		if (type != OFF) {
			long count = message.chars().filter(c -> c == '|').count();
			if (count < 4)
				return false;

			if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_MANAGE))
				return false;

			msg.delete().queue();
			if (type == DELETE)
				return true;

			if (Utils.handleRights(guild, author, channel, Permission.MANAGE_WEBHOOKS))
				return true;

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
				channel.sendMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild)
						.addField("errorhappened", "somethingwentwrong", false).build()).queue();
			}
		}
		return false;
	}
}
