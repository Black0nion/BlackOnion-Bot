package com.github.black0nion.blackonionbot.systems.antiswear;

import static com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType.DELETE;
import static com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType.OFF;
import static com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType.REPLACE;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import club.minnced.discord.webhook.WebhookClient;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.config.Config;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.Permission;

public class AntiSwearSystem {

	public static boolean check(final BlackGuild guild, final BlackMember author, final Message message, final TextChannel channel) {
		if (Config.content_moderator_token == null || Config.content_moderator_token.isEmpty()) {
			return false;
		}
		final String messageContent = message.getContentRaw();
		final BlackUser user = author.getBlackUser();
		if (user.isBot()) return false;
		final AntiSwearType type = guild.getAntiSwearType();
		if (guild.isPremium()) {
			if (type == OFF) return false;
		} else {
			if (type != OFF) {
				guild.setAntiSwearType(OFF);
			}
			return false;
		}

		if (Utils.handleRights(guild, user, channel, Permission.MESSAGE_MANAGE)) return false;

		try {
			if (messageContent.equalsIgnoreCase("")) return false;
			// check for whitelist
			final List<String> whitelist = guild.getAntiSwearWhitelist();
			if (whitelist != null && (whitelist.contains(channel.getAsMention()) || author.getRoles().stream().anyMatch(role -> whitelist.contains(role.getAsMention()))))
				return false;
			// Message messageRaw = event.getMessage();
			HttpResponse<String> response = Bot.getInstance().getHttpClient().send(HttpRequest.newBuilder(URI.create("https://westeurope.api.cognitive.microsoft.com/contentmoderator/moderate/v1.0/ProcessText/Screen?autocorrect=false&classify=True"))
				.POST(HttpRequest.BodyPublishers.ofString(messageContent))
				.header("Content-Type", "text/plain")
				.header("Ocp-Apim-Subscription-Key", Config.content_moderator_token)
				.build(), HttpResponse.BodyHandlers.ofString());

			String responseBody = response.body();
			final JSONObject responseJson = new JSONObject(responseBody);
			// check for profanity
			if (responseJson.has("Terms")) {
				// this will happen if it doesn't contain any profanity
				if (!(responseJson.get("Terms") instanceof JSONArray)) return false;

				StatisticsManager.PROFANITY_FILTERED.labels(guild.getId(), guild.getName(), channel.getId(), channel.getName()).inc();

				try {
					message.delete().queue();

					if (type == DELETE) return true;

					if (type == REPLACE) {

						if (Utils.handleRights(guild, user, channel, Permission.MANAGE_WEBHOOKS)) return true;

						final WebhookMessageBuilder builder = new WebhookMessageBuilder();
						final JSONArray terms = responseJson.getJSONArray("Terms");

						String newMessage = messageContent;

						for (int i = 0; i < terms.length(); i++) {
							final String term = terms.getJSONObject(i).getString("Term");
							newMessage = newMessage.replaceAll("(?i)" + term, "*".repeat(term.length()));
						}

						newMessage = Utils.escapeMarkdown(newMessage);
						builder.setContent(newMessage);
						builder.setUsername(author.getEffectiveName() + "#" + user.getDiscriminator());
						builder.setAvatarUrl(user.getEffectiveAvatarUrl());
						channel.retrieveWebhooks().queue(webhooks -> {
							try {
								WebhookClient client = Utils.makeWebhookClient(Utils.getWebhook(channel, webhooks));
								client.send(builder.build());
								client.close();
							} catch (final Exception ex) {
								ex.printStackTrace();
							}
						});
					} else {
						channel.sendMessageEmbeds(EmbedUtils.getErrorEmbed(user, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return true;
			} else
				throw new RuntimeException("Some error happened while contacting the Microsoft api. Response: \n" + responseBody);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}