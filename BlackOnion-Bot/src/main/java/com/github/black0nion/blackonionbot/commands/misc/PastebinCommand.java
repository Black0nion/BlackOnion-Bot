package com.github.black0nion.blackonionbot.commands.misc;

import org.menudocs.paste.PasteClient;
import org.menudocs.paste.PasteClientBuilder;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PastebinCommand extends Command {
	
	public PastebinCommand() {
		this.setCommand("pastebin")
			.setSyntax("<language> <text>")
			.setRequiredArgumentCount(3);
	}
	
	private static final PasteClient client = new PasteClientBuilder()
			.setUserAgent("BlackOnion-Bot")
			.build();

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final String language = args[1];
		final String contentRaw = e.getMessage().getContentRaw();
		final int index = contentRaw.indexOf(language) + language.length();
		final String body = contentRaw.substring(index).trim();
		
		client.createPaste(language, body).async(
				(id) -> client.getPaste(id).async((paste) -> {
					cmde.reply(cmde.success()
							.setTitle("pastecreated", paste.getPasteUrl())
							.setDescription("```")
							.appendDescription(paste.getLanguage().getId())
							.appendDescription("\n")
							.appendDescription(paste.getBody())
							.appendDescription("```"));
				})
		);
	}
}