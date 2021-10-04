package com.github.black0nion.blackonionbot.commands.misc;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class PasteCommand extends SlashCommand {

    public PasteCommand() {
	this.setData(new CommandData("paste", "Uploads a text to a pasteserver")
		.addOption(OptionType.STRING, "text", "The text to upload", true)
		.addOption(OptionType.STRING, "language", "The programming language the paste is in", false));
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String finalLanguage = e.getOption("language") != null ? e.getOption("language").getAsString() : null;
	final String finalBody = e.getOption("text").getAsString();

	e.replyEmbeds(cmde.loading().build()).flatMap(InteractionHook::retrieveOriginal).queue(msg -> {
	    try {
		Unirest.setTimeouts(0, 0);
		final HttpRequestWithBody headers = Unirest.post("https://paste.sv-studios.net/documents").header("Content-Type", "text/plain");

		if (finalLanguage != null) {
		    headers.header("language", finalLanguage);
		}

		final HttpResponse<String> response = headers.body(finalBody).asString();

		final JSONObject obj = new JSONObject(response.getBody());

		final EmbedBuilder builder = cmde.success().setTitle("pastecreated", "https://paste.sv-studios.net/" + obj.getString("key")).setDescription("```" + (finalLanguage != null ? finalLanguage : "")).appendDescription("\n").appendDescription(finalBody).appendDescription("```");

		msg.editMessageEmbeds(builder.build()).queue();

		e.getHook().sendMessage(cmde.getTranslation("yourcode").replace("%code%", obj.getString("deleteSecret"))).setEphemeral(true).queue();
	    } catch (final Exception ex) {
		cmde.exception();
	    }
	});
    }
}