package com.github.black0nion.blackonionbot.commands.misc;

import org.menudocs.paste.PasteClient;
import org.menudocs.paste.PasteClientBuilder;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PastebinCommand implements Command {
	
	private final PasteClient client = new PasteClientBuilder()
			.setUserAgent("BlackOnion-Bot")
			.build();

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		final String language = args[1];
		final String contentRaw = e.getMessage().getContentRaw();
		final int index = contentRaw.indexOf(language) + language.length();
		final String body = contentRaw.substring(index).trim();
		
		client.createPaste(language, body).async(
				(id) -> client.getPaste(id).async((paste) -> {
					EmbedBuilder builder = new EmbedBuilder()
							.setTitle(LanguageSystem.getTranslation("pastecreated", author, guild), paste.getPasteUrl())
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
	public String getSyntax() {
		return "<Sprache> <Text>";
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 3;
	}
	
	@Override
	public Category getCategory() {
		return Category.MISC;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"pastebin"};
	}

}
