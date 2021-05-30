package com.github.black0nion.blackonionbot.commands.misc;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PasteCommand extends Command {
	
	public PasteCommand() {
		this.setCommand("paste", "uploadtext")
			.setSyntax("<language> <text>")
			.setRequiredArgumentCount(2);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final String language = args[1];
		final String contentRaw = e.getMessage().getContentRaw();
		final int index = contentRaw.indexOf(language) + language.length();
		final String bodyLong = contentRaw.substring(index).trim();
		// todo pls help alvin
		
		cmde.loading(msg -> {				
			try {
				Unirest.setTimeouts(0, 0);
				HttpResponse<String> response = Unirest.post("https://paste.sv-studios.net/documents")
						.header("Content-Type", "text/plain")
						.header("language", language)
						.body(bodyLong)
						.asString();
				
				JSONObject obj = new JSONObject(response.getBody());
				
				final EmbedBuilder builder = cmde.success()
						.setTitle("pastecreated", "https://paste.sv-studios.net/" + obj.getString("key"))
						.setDescription("```")
						.appendDescription(language)
						.appendDescription("\n")
						.appendDescription(bodyLong)
						.appendDescription("```");
				
				msg.editMessage(builder.build()).queue();
				
				author.openPrivateChannel().queue(ch -> {
					ch.sendMessage(builder.appendDescription("\n" + cmde.getTranslation("yourcode").replace("%code%", obj.getString("deleteSecret"))).build()).queue();
				});
			} catch (Exception ex) {
				cmde.exception();
			}
		});
	}
}