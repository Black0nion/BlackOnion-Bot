package com.github.black0nion.blackonionbot.commands.misc;

import java.util.List;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class VirusCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "viruscheck", "virus" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
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
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", LanguageSystem.getTranslation("pleaseuse", author, guild) + " " + getSyntax(), false).build()).queue();
				return;
			}
			final String finalUrl = url;
			final String urlResult = checkUrl(finalUrl).getBody();
			if (!new JSONObject(urlResult).has("data")) {
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", LanguageSystem.getTranslation("pleaseuse", author, guild) + " " + getSyntax(), false).build()).queue();
				return;
			}
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("fileprocessed", "waitforresult", false).build()).queue(msg -> {
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
