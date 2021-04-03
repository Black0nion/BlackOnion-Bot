package com.github.black0nion.blackonionbot.systems;

import java.util.List;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AntiSpoilerSystem {
	/**
	 * @param event
	 * @return if the message contained a spoiler
	 */
	public static boolean removeSpoilers(GuildMessageReceivedEvent event) {
		final Guild guild = event.getGuild();
		final Message msg = event.getMessage();
		final String message = msg.getContentRaw();
		final User user = event.getAuthor();
		String newMessage = message;
		boolean deletespoiler = GuildManager.getBoolean(guild, "deletespoiler", false);
		boolean antispoiler = GuildManager.getBoolean(guild, "antispoiler", false);
		
		if (antispoiler || deletespoiler) {
			long count = message.chars().filter(c -> c == '|').count();
			if (count < 4) return false;
			
			msg.delete().queue();
			if (deletespoiler) return true;
			
			while (count >= 4) {
				newMessage = newMessage.replaceFirst("\\|\\|", "");
				newMessage = newMessage.replaceFirst("\\|\\|", "");
				count -= 4;
			}
			
			try {
				final List<Webhook> webhooks = event.getChannel().retrieveWebhooks().submit().join();
				
				Webhook webhook;
				
				if (webhooks.stream().anyMatch(tempWebhook -> {if (tempWebhook == null) return false; else return (tempWebhook.getOwner().getIdLong() == BotInformation.botId);})) {
					webhook = webhooks.stream().filter(tempWebhook -> {return tempWebhook.getOwner().getIdLong() == BotInformation.botId;}).findFirst().get();
				} else {
					webhook = event.getChannel().createWebhook("BlackOnion-Bot ContentModerator").setAvatar(Icon.from(ContentModeratorSystem.file)).submit().join();
				}
				
				WebhookClientBuilder clientBuilder = new WebhookClientBuilder(webhook.getUrl());
				clientBuilder.setThreadFactory((job) -> {
					Thread thread = new Thread(job);
					thread.setName("ContentModerator");
					thread.setDaemon(true);
					return thread;
				});
				
				WebhookClient client = clientBuilder.build();
				WebhookMessageBuilder builder = new WebhookMessageBuilder();
				builder.setUsername(user.getName() + "#" + user.getDiscriminator());
				builder.setContent(newMessage);
				builder.setAvatarUrl(user.getAvatarUrl());
				client.send(builder.build());
				client.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
