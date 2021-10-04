package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CatCommand extends SlashCommand {

    public CatCommand() {
	this.setData(new CommandData("cat", "Shows you a cute cat ^-^").addOption(OptionType.STRING, "breed", "The breed of the cat", false));
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String breed = !e.getOptionsByType(OptionType.STRING).isEmpty() ? e.getOptionsByType(OptionType.STRING).get(0).getAsString() : null;
	try {
	    Unirest.setTimeouts(0, 0);
	    final HttpResponse<String> response = Unirest.get("https://api.thecatapi.com/v1/images/search" + (breed != null ? "?breed_ids=" + breed : "")).header("Content-Type", "application/json").asString();
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