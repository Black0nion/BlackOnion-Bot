package com.github.ahitm_2020_2025.blackonionbot.commands.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.github.ahitm_2020_2025.blackonionbot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.commands.CommandBase;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisisbility;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
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
				String help = "**Hilfe | " + args[1].toUpperCase() + "**\n";
				String other = "----------------\n";
				for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
					if (entry.getValue().getVisisbility() == CommandVisisbility.SHOWN && (entry.getValue().getCategory() == Category.valueOf(args[1].toUpperCase()) || Category.valueOf(args[1].toUpperCase()) == Category.ALL)) {
						String helpValue = "``" + BotInformation.prefix + entry.getKey()[0] + (entry.getValue().getSyntax() != null ? " " + entry.getValue().getSyntax() : "") + "`` | " + entry.getValue().getDescription() +  "\n";
						if (entry.getValue().getProgress() != Progress.DONE) {
							other += "» **" + entry.getValue().getProgress() + ":** " + helpValue;
						} else {
							help += "» " + helpValue;	
						}
					}
				}
				channel.sendMessage(help + other).queue();
			} else {
				String help = "**Hilfe | Module**\n";
				for (Category c : Category.values()) {
					help += "» " + c.name() + "\n";
				}
				channel.sendMessage(help).queue();
			}
		} catch (Exception ex) {
			if (!(ex instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			} else {
				channel.sendMessage("Diese Kategorie existiert nicht! Benutze " + BotInformation.prefix + "help, um alle Module aufgelistet zu bekommen!").queue();
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
