package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TronaldDumpCommand extends SlashCommand {

	public TronaldDumpCommand() {
		super("tronalddump", "Shows you a random quote from tronald dump");
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		Bot.getInstance().getHttpClient().sendAsync(HttpRequest.newBuilder(URI.create("https://tronalddump.io/random/quote")).header("Accept", "application/json").build(), HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body).thenAccept(response ->
				cmde.reply(cmde.success()
					.setThumbnail("https://www.tronalddump.io/img/tronalddump_850x850.png")
					.setTitle("TronaldDump", "https://tronalddump.io")
					.addField(new JSONObject(response).getString("value"), "bytronalddump", false)
				)
			);
	}
}
