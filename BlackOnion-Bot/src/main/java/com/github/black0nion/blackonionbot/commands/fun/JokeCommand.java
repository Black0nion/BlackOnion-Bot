package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JokeCommand extends Command {
	
	public JokeCommand() {
		this.setCommand("joke", "jokes");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		try {
			Unirest.setTimeouts(0, 0);
			final Language lang = cmde.getLanguage();
			String langString = null;
			if (Utils.equalsOneIgnoreCase(lang.getLanguageCode(), "de", "en", "cs", "es", "fr", "pt"))
				langString = "&lang=" + lang.getLanguageCode().toLowerCase();
			final HttpResponse<String> response = Unirest.get("https://v2.jokeapi.dev/joke/Any?blacklistFlags=nsfw,racist,sexist&type=twopart" + (langString != null ? langString : "")).asString();
			final JSONObject object = new JSONObject(response.getBody());
			cmde.success("Joke", "https://jokeapi.dev", object.getString("setup"), object.getString("delivery"));
		} catch (final Exception ex) {
			cmde.exception();
			ex.printStackTrace();
		}
	}
}