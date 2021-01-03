package com.github.ahitm_2020_2025.blackonionbot.commands.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.github.ahitm_2020_2025.blackonionbot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.commands.CommandBase;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisisbility;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		try {
			if (args.length >= 2 && new ArrayList<Category>(Arrays.asList(Category.values())).contains(Category.valueOf(args[1].toUpperCase()))) {
				EmbedBuilder builder = new EmbedBuilder()
						.setTitle("Hilfe | " + args[1].toUpperCase())
						.setColor(Color.getHSBColor(0.8F, 1, 0.5F));
				
				for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
					if (entry.getValue().getVisisbility() == CommandVisisbility.SHOWN && (entry.getValue().getCategory() == Category.valueOf(args[1].toUpperCase()) || Category.valueOf(args[1].toUpperCase()) == Category.ALL)) {
						if (entry.getValue().getProgress() == Progress.DONE) {
							builder.addField(BotInformation.prefix + entry.getKey()[0] + (entry.getValue().getSyntax() != null && !entry.getValue().getSyntax().equalsIgnoreCase("") ? " " + entry.getValue().getSyntax() : ""), entry.getValue().getDescription(), false);
						}
					}
				}
				
				for (Progress pr : Progress.values()) {
					if (pr == Progress.DONE)
						continue;
					for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
						Command command = entry.getValue();
						if (command.getVisisbility() == CommandVisisbility.SHOWN && (command.getCategory() == Category.valueOf(args[1].toUpperCase()) || Category.valueOf(args[1].toUpperCase()) == Category.ALL) && command.getProgress() == pr) {
							builder.addField(pr.name().toUpperCase() + ": " + BotInformation.prefix + entry.getKey()[0] + (command.getSyntax() != null && !command.getSyntax().equalsIgnoreCase("") ? " " + command.getSyntax() : ""), command.getDescription(), false);
						}
					}
				}
				
				channel.sendMessage(builder.build()).queue();
			} else {
				EmbedBuilder builder = new EmbedBuilder()
						.setTitle("Hilfe | Module")
						.setColor(BotInformation.mainColor);
				for (Category c : Category.values()) {
					if (c == Category.ALL)
						continue;
					String commandsInCategory = "";
					for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
						if (entry.getValue().getCategory() == c)
							commandsInCategory += ", " + entry.getValue().getCommand()[0];
					}
					builder.addField(c.name(), commandsInCategory.substring(1), false);
				}
				channel.sendMessage(builder.build()).queue();
			}
		} catch (Exception ex) {
			if (!(ex instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			} else {
				channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Diese Kategorie existiert nicht!", 
						"Benutze " + BotInformation.prefix + "help, um alle Module aufgelistet zu bekommen!", false).build()).queue();
			}
		}
	}

	@Override
	public String getDescription() {
		return "Zeigt die Hilfe an";
	}

	@Override
	public String getSyntax() {
		return null;
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"help"};
	}

}
