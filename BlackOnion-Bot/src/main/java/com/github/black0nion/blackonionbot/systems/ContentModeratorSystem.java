package com.github.black0nion.blackonionbot.systems;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ContentModeratorSystem extends ListenerAdapter {
	
	private static final File file = new File("resources/logo.png");
	
	@SuppressWarnings("unused")
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		final Guild guild = event.getGuild();
		if (event.getAuthor().isBot()) return;
		if (GuildManager.isPremium(guild)) {
			if (!GuildManager.getBoolean(guild, "antiSwear"))
				return;
		} else {
			if (GuildManager.getBoolean(guild, "antiSwear"))
				GuildManager.save(guild, "antiSwear", false);
			return;
		}
		try {
			HttpResponse<String> response = Unirest.get("https://www.purgomalum.com/service/plain?text=" + URLEncoder.encode(event.getMessage().getContentDisplay(), "UTF-8")).asString();
			
			String message = response.getBody().replace("Ã¤", "ä").replace("Ã„", "Ä").replace("Ã¶", "ö").replace("Ã–", "Ö").replace("Ã¼", "ü").replace("Ãœ", "Ü");
			
			if (!message.equals(event.getMessage().getContentDisplay())) {
				if (true) {
					//event.getMessage().delete().queue();
					WebhookMessageBuilder builder = new WebhookMessageBuilder();
					message = Utils.removeMarkdown(message);
					builder.setContent(message);
					builder.setUsername(event.getMember().getEffectiveName());
					builder.setAvatarUrl(event.getAuthor().getEffectiveAvatarUrl());
					final List<Webhook> webhooks = event.getChannel().retrieveWebhooks().submit().join();
					
					Webhook webhook;
					
					if (webhooks.stream().anyMatch(tempWebhook -> {if (tempWebhook == null) return false; else return (tempWebhook.getOwner().getIdLong() == BotInformation.botId);})) {
						webhook = webhooks.stream().filter(tempWebhook -> {return tempWebhook.getOwner().getIdLong() == BotInformation.botId;}).findFirst().get();
					} else {
						webhook = event.getChannel().createWebhook("BlackOnion-Bot ContentModerator").setAvatar(Icon.from(file)).submit().join();
					}
					
					WebhookClientBuilder clientBuilder = new WebhookClientBuilder(webhook.getUrl());
					clientBuilder.setThreadFactory((job) -> {
						Thread thread = new Thread(job);
						thread.setName("ContentModerator");
						thread.setDaemon(true);
						return thread;
					});
					
					WebhookClient client = clientBuilder.build();
					client.send(builder.build());
					client.close();
				} else 
					event.getMessage().delete().queue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
