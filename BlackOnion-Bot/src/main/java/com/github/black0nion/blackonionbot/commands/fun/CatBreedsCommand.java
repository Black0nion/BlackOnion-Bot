package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CatBreedsCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "catbreeds", "catexamples" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		try {
		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.get("https://api.thecatapi.com/v1/breeds?limit=10&page=0")
		  .header("Content-Type", "application/json")
		  .asString();
		final JSONArray responseAsJSONArray = new JSONArray(response.getBody());
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild).setTitle("UwU");
		for (int i = 0; i < responseAsJSONArray.length(); i++) {
			final JSONObject responseAsJSON = responseAsJSONArray.getJSONObject(i);
			builder.addField(responseAsJSON.getString("name"), responseAsJSON.getString("id"), false);
		}
		channel.sendMessage(builder.build()).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
			return;
		}
	}

	@Override
	public Category getCategory() {
		return Category.FUN;
	}
}