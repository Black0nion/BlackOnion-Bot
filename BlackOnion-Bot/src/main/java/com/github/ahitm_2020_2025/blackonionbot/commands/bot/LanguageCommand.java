package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.util.Map;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.systems.language.Language;
import com.github.ahitm_2020_2025.blackonionbot.systems.language.LanguageSystem;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LanguageCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"language", "lang", "locale"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (LanguageSystem.getLanguageFromName(args[1].toUpperCase()) != null) {
			LanguageSystem.updateUserLocale(author.getId(), args[1]);
			channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).addField("Erfolgreich Sprache geupdated!", "Neue Sprache: " + LanguageSystem.getLanguageFromName(args[1]).getName() + " (" + args[1] + ")", false).build()).queue();
		} else {
			//NICHT ändern, ist ein genereller Command -> soll Englisch bleiben
			String validLanguages = "\n";
			for (Map.Entry<String, Language> entry : LanguageSystem.getLanguages().entrySet()) {
				validLanguages += entry.getValue().getName() + " (" + entry.getKey() + ")\n";
			}
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Language doesn't exist!", "Valid languages: " + validLanguages, false).build()).queue();
		}
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}

}
