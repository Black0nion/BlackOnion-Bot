package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class TronaldDumpCommand extends SlashCommand {

    public TronaldDumpCommand() {
	this.setData(new CommandData("tronalddump", "Get a random quote of our favorite president of america, Tronald Dump!"));
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    Unirest.setTimeouts(0, 0);
	    final HttpResponse<String> response = Unirest.get("https://tronalddump.io/random/quote").header("Accept", "application/json").asString();
	    final JSONObject object = new JSONObject(response.getBody());
	    cmde.reply(cmde.success().setThumbnail("https://www.tronalddump.io/img/tronalddump_850x850.png").setTitle("TronaldDump", "https://tronalddump.io").addField(object.getString("value"), "bytronalddump", false));
	} catch (final Exception ex) {
	    ex.printStackTrace();
	    cmde.exception();
	}
    }
}