package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CatCommand extends Command {
	
	public CatCommand() {
		this.setCommand("cat", "uwucat")
			.setSyntax("[cat breed]");
	}

	@Override
	public String[] getCommand() {
		return new String[] { "cat", "uwucat" };
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final String breed = (args.length >= 2 ? args[1] : null);
		try {
			Unirest.setTimeouts(0, 0);
			final HttpResponse<String> response = Unirest.get("https://api.thecatapi.com/v1/images/search" + (breed != null ? "?breed_ids=" + breed : ""))
			  .header("Content-Type", "application/json")
			  .asString();
			if (response.getBody().equalsIgnoreCase("[]")) {
				// cat breed not found
				cmde.error("catnotfound", "catbreednotfound", new Placeholder("command", guild.getPrefix() + CommandBase.commands.get("catbreeds").getCommand()[0]));
				return;
			}
			final JSONArray responseAsJSONArray = new JSONArray(response.getBody());
			final JSONObject responseAsJSON = responseAsJSONArray.getJSONObject(0);
			cmde.reply(cmde.success().setTitle("UwU").setImage(responseAsJSON.getString("url")));
		} catch (final Exception ex) {
			ex.printStackTrace();
			cmde.exception();
			return;
		}
	}
}