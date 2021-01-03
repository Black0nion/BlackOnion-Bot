package com.github.ahitm_2020_2025.blackonionbot.commands.impl;

import org.menudocs.paste.PasteClient;
import org.menudocs.paste.PasteClientBuilder;

import com.github.ahitm_2020_2025.blackonionbot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PastebinCommand implements Command {
	
	private final PasteClient client = new PasteClientBuilder()
			.setUserAgent("BlackOnion-Bot")
			.build();

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (args.length < 3) {
			channel.sendMessage("Bitte benutze " + BotInformation.prefix + "pastebin " + getSyntax()).queue();
			return;
		}
		
		final String language = args[1];
		final String contentRaw = e.getMessage().getContentRaw();
		final int index = contentRaw.indexOf(language) + language.length();
		final String body = contentRaw.substring(index).trim();
		
		client.createPaste(language, body).async(
				(id) -> client.getPaste(id).async((paste) -> {
					EmbedBuilder builder = new EmbedBuilder()
							.setTitle("Paste erstellt", paste.getPasteUrl())
							.setDescription("```")
							.appendDescription(paste.getLanguage().getId())
							.appendDescription("\n")
							.appendDescription(paste.getBody())
							.appendDescription("```");
					
					channel.sendMessage(builder.build()).queue();
				})
		);
	}

	@Override
	public String getDescription() {
		return "Uploaded einen Text";
	}
	
	@Override
	public String getSyntax() {
		return "<Sprache> <Text>";
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"pastebin"};
	}

}
