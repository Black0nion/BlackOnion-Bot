package com.github.black0nion.blackonionbot.commands.misc;

import java.util.List;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class VirusCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "viruscheck", "virus" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		Bot.executor.submit(() -> {
			final List<Attachment> attachments = message.getAttachments();
			
			String url = "";
			if (attachments.size() != 0) {
				if (attachments.get(0).getSize() >= 32000000) {
					url = new JSONObject(getUploadUrl()).getString("data");
				} else {
					url = attachments.get(0).getUrl();
				}
			} else if (args.length >= 2) {
				url = args[1];
			} else {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
				return;
			}
			final String finalUrl = url;
			final String urlResult = checkUrl(finalUrl).getBody();
			if (!new JSONObject(urlResult).has("data")) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
				return;
			}
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("fileprocessed", "waitforresult", false).build()).queue(msg -> {
				JSONObject analyse = null;
				do  { 
					try { Thread.sleep(5000); } catch (InterruptedException ex) { ex.printStackTrace(); }
					analyse = new JSONObject(checkAnalyse(new JSONObject(urlResult).getJSONObject("data").getString("id")).getBody()); 
				} while (!analyse.has("data"));
				final JSONObject result = analyse.getJSONObject("data").getJSONObject("attributes").getJSONObject("stats");
				msg.editMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("Result: " + (result.getInt("malicious") == 0 ? "No Virus." : "Virus!"), "Malicious: " + result.getInt("malicious") + "\nUndetected: " + result.getInt("undetected") + "\nSuspicious: " + result.getInt("suspicious") + "\nHarmless: " + result.getInt("harmless"), false).build()).queue();
				return;
			});
		});
	}
	
	public static HttpResponse<String> checkUrl(String url) {
		try {
			final String key = Bot.getCredentialsManager().getString("virustotal_key");
			if (key == null)
				return null;
			Unirest.setTimeouts(0, 0);
			return Unirest.post("https://www.virustotal.com/api/v3/urls")
			  .header("x-apikey", key)
			  .field("url", url)
			  .asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static HttpResponse<String> checkAnalyse(String analyseId) {
		try {
			final String key = Bot.getCredentialsManager().getString("virustotal_key");
			if (key == null)
				return null;
			Unirest.setTimeouts(0, 0);
			return Unirest.get("https://www.virustotal.com/api/v3/analyses/" + analyseId)
			  .header("x-apikey", key)
			  .asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static HttpResponse<String> getUploadUrl() {
		try {
			final String key = Bot.getCredentialsManager().getString("virustotal_key");
			if (key == null)
				return null;
			Unirest.setTimeouts(0, 0);
			return Unirest.get("https://www.virustotal.com/api/v3/files/upload_url")
			  .header("x-apikey", key)
			  .asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getSyntax() {
		return "<url / attachement>";
	}
}