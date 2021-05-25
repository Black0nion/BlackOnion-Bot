package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CatCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "cat", "uwucat" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		String breed = (args.length >= 2 ? args[1] : null);
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.get("https://api.thecatapi.com/v1/images/search" + (breed != null ? "?breed_ids=" + breed : ""))
			  .header("Content-Type", "application/json")
			  .asString();
			if (response.getBody().equalsIgnoreCase("[]")) {
				// cat breed not found
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("catnotfound", LanguageSystem.getTranslation("catbreednotfound", author, guild).replace("%command%", guild.getPrefix() + CommandBase.commands.get("catbreeds").getCommand()[0]), false).build()).queue();
				return;
			}
			final JSONArray responseAsJSONArray = new JSONArray(response.getBody());
			final JSONObject responseAsJSON = responseAsJSONArray.getJSONObject(0);
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle("UwU").setImage(responseAsJSON.getString("url")).build()).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
			return;
		}
	}

	@Override
	public Category getCategory() {
		return Category.FUN;
	}
	
	@Override
	public String getSyntax() {
		return "[cat breed]";
	}
}