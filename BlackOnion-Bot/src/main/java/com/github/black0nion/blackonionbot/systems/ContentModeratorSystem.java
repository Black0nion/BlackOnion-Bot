package com.github.black0nion.blackonionbot.systems;

import java.io.File;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.bot.Bot;
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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;

public class ContentModeratorSystem {
	
	public static int profanityFilteredLastTenSecs = 0;
	
	public static final File file = new File("resources/logo.png");
	
	/**
	 * @param event
	 * @return true if the message contained unwanted content
	 */
	public static boolean checkMessageForProfanity(GuildMessageReceivedEvent event) {
		return check(event.getGuild(), event.getMember(), event.getMessage(), event.getChannel());
	}
	
	public static boolean checkMessageForProfanity(GuildMessageUpdateEvent event) {
		return check(event.getGuild(), event.getMember(), event.getMessage(), event.getChannel());
	}
	
	private static boolean check(Guild guild, Member author, Message message, TextChannel channel) {
		final String messageContent = message.getContentRaw();
		final User user = author.getUser();
		if (user.isBot()) return false;
		if (GuildManager.isPremium(guild)) {
			if (!GuildManager.getBoolean(guild, "antiSwear"))
				return false;
		} else {
			if (GuildManager.getBoolean(guild, "antiSwear"))
				GuildManager.save(guild, "antiSwear", false);
			return false;
		}
		try {
			if (messageContent.equalsIgnoreCase("")) return false;
			// check for whitelist
			final List<String> whitelist = GuildManager.getList(guild, "whitelist", String.class);
			if (whitelist != null && (whitelist.contains(channel.getAsMention()) || author.getRoles().stream().anyMatch(role -> whitelist.contains(role.getAsMention())))) return false;
			//Message messageRaw = event.getMessage();
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.post("https://westeurope.api.cognitive.microsoft.com/contentmoderator/moderate/v1.0/ProcessText/Screen?autocorrect=false&classify=True")
			  .header("Content-Type", "text/plain")
			  .header("Ocp-Apim-Subscription-Key", Bot.getCredentialsManager().getString("content_moderator_key"))
			  .body(messageContent)
			  .asString();

			JSONObject responseJson = new JSONObject(response.getBody());
			// check for profanity
			if (responseJson.has("Terms")) {
				// this will happen if it doesn't contain any profanity
				if (!(responseJson.get("Terms") instanceof JSONArray)) return false;
				Bot.executor.submit(() -> {
					profanityFilteredLastTenSecs++;
					try {
						message.delete().queue();
						WebhookMessageBuilder builder = new WebhookMessageBuilder();
						final JSONArray terms = responseJson.getJSONArray("Terms");
						
						String newMessage = messageContent;
						
						for (int i = 0; i < terms.length(); i++) {
							final String term = terms.getJSONObject(i).getString("Term");
							newMessage = newMessage.replaceAll("(?i)" + term, term.replaceAll(".", "*"));
						}
						
						newMessage = Utils.removeMarkdown(newMessage);
						builder.setContent(newMessage);
						builder.setUsername(author.getEffectiveName() + "#" + user.getDiscriminator());
						builder.setAvatarUrl(user.getEffectiveAvatarUrl());
						channel.retrieveWebhooks().queue(webhooks -> {
							try {
								Webhook webhook;
								
								if (webhooks.stream().anyMatch(tempWebhook -> {if (tempWebhook == null) return false; else return (tempWebhook.getOwner().getIdLong() == BotInformation.botId);})) {
									webhook = webhooks.stream().filter(tempWebhook -> {return tempWebhook.getOwner().getIdLong() == BotInformation.botId;}).findFirst().get();
								} else {
									webhook = channel.createWebhook("BlackOnion-Bot ContentModerator").setAvatar(Icon.from(file)).submit().join();
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
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				return true;
			} else throw new RuntimeException("Some error happened while contacting the Microsoft API. Response: \n" + response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
}
