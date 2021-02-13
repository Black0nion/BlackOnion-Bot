package com.github.black0nion.blackonionbot.commands.misc;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VirusCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "viruscheck", "virus" };
	}
	
	private static final ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		executor.submit(() -> {
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
			}
			System.out.println(new JSONObject(checkAnalyse(new JSONObject(checkUrl(url).getBody()).getJSONObject("data").getString("id")).getBody()).getJSONObject("data").getJSONObject("attributes").getJSONObject("stats").getInt("harmless"));
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
}
