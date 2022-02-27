package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class TronaldDumpCommand extends Command {

	public TronaldDumpCommand() {
		this.setCommand("tronalddump", "td", "donaldtrump");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
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