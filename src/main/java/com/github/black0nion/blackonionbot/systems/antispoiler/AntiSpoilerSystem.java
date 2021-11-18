package com.github.black0nion.blackonionbot.systems.antispoiler;

import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType.DELETE;
import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType.OFF;
import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType.REPLACE;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

public class AntiSpoilerSystem {
    /**
     * @param event
     * @return if the message contained a spoiler
     */
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
			    Webhook webhook;

			    if (webhooks.stream().anyMatch(tempWebhook -> {
				if (tempWebhook == null) return false;
				else return (tempWebhook.getOwner().getIdLong() == BotInformation.SELF_USER_ID);
			    })) {
				webhook = webhooks.stream().filter(tempWebhook -> (tempWebhook.getOwner().getIdLong() == BotInformation.SELF_USER_ID)).findFirst().get();
			    } else {
				webhook = channel.createWebhook("BlackOnion-Bot ContentModerator").setAvatar(Icon.from(AntiSpoilerSystem.class.getResourceAsStream("/logo.png"))).submit().join();
			    }

			    final WebhookClientBuilder clientBuilder = new WebhookClientBuilder(webhook.getUrl());
			    clientBuilder.setThreadFactory(job -> {
				final Thread thread = new Thread(job);
				thread.setName("ContentModerator");
				thread.setDaemon(true);
				return thread;
			    });

			    final WebhookClient client = clientBuilder.build();
			    final WebhookMessageBuilder builder = new WebhookMessageBuilder();
			    builder.setUsername(author.getName() + "#" + author.getDiscriminator());
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