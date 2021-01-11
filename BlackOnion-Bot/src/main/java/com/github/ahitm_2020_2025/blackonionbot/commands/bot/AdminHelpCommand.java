package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.bot.CommandBase;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.systems.language.LanguageSystem;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AdminHelpCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"adminhelp"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		message.delete().complete();
		EmbedBuilder builder = EmbedUtils.getDefaultSuccessEmbed(author)
				.setTitle("Adminhilfe")
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F));
		
		for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
			if (entry.getValue().getVisisbility() == CommandVisibility.HIDDEN && entry.getValue().getCommand()[0] != getCommand()[0]) {
				builder.addField(BotInformation.prefix + entry.getKey()[0] + (entry.getValue().getSyntax() != null && !entry.getValue().getSyntax().equalsIgnoreCase("") ? " " + entry.getValue().getSyntax() : ""), LanguageSystem.getTranslatedString("help" + entry.getValue().getCommand()[0].toLowerCase(), e.getAuthor().getId(), e.getGuild().getId()), false);
			}
		}
		
		channel.sendMessage(builder.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
	}
	
	@Override
	public boolean requiresBotAdmin() {
		return true;
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}

}
