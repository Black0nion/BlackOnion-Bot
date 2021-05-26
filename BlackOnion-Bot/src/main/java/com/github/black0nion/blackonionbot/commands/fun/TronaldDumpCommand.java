package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TronaldDumpCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "tronalddump", "td", "donaldtrump" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.get("https://tronalddump.io/random/quote").header("Accept", "application/json").asString();
			JSONObject object = new JSONObject(response.getBody());
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).setThumbnail("https://www.tronalddump.io/img/tronalddump_850x850.png").setTitle("TronaldDump", "https://tronalddump.io").addField(object.getString("value"), "bytronalddump", false).build()).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}
}