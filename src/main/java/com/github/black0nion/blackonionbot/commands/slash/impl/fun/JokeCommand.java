package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JokeCommand extends SlashCommand {

	public JokeCommand() {
		super("joke", "Sends a random joke");
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		final Language lang = cmde.getLanguage();

		String langParam = null;
		if (Utils.equalsOneIgnoreCase(lang.getLanguageCode(), "de", "en", "cs", "es", "fr", "pt"))
			langParam = "&lang=" + lang.getLanguageCode().toLowerCase();

		Bot.getInstance().getHttpClient().sendAsync(HttpRequest.newBuilder(URI.create("https://v2.jokeapi.dev/joke/Any?blacklistFlags=nsfw,racist,sexist&type=twopart" + (langParam != null ? langParam : ""))).build(), HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body).thenAccept(response -> {
				final JSONObject object = new JSONObject(response);
				cmde.success("Joke", "https://jokeapi.dev", object.getString("setup"), object.getString("delivery"));
			});
	}
}
