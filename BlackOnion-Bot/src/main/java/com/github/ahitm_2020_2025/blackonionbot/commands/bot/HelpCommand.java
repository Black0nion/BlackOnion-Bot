package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.bot.CommandBase;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.systems.language.LanguageSystem;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		try {
			if (args.length >= 2) {
				//Sum1 entered a category
				List<Category> listOfCategories = Arrays.asList(Category.values());
				ArrayList<String> listOfNames = new ArrayList<>();
				listOfCategories.forEach(entry -> {
					listOfNames.add(entry.name());
				});
				if (listOfNames.contains(args[1].toUpperCase())) {
					EmbedBuilder builder = EmbedUtils.getDefaultSuccessEmbed(author)
							.setTitle("Hilfe | " + args[1].toUpperCase())
							.setColor(Color.getHSBColor(0.8F, 1, 0.5F));
					
					for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
						if (entry.getValue().getVisisbility() == CommandVisibility.SHOWN && (entry.getValue().getCategory() == Category.valueOf(args[1].toUpperCase()) || Category.valueOf(args[1].toUpperCase()) == Category.ALL)) {
							if (entry.getValue().getProgress() == Progress.DONE) {
								builder.addField(BotInformation.prefix + entry.getKey()[0] + (entry.getValue().getSyntax() != null && !entry.getValue().getSyntax().equalsIgnoreCase("") ? " " + entry.getValue().getSyntax() : ""), LanguageSystem.getTranslatedString("help" + entry.getValue().getCommand()[0].toLowerCase(), e.getAuthor(), e.getGuild()), false);
							}
						}
					}
					
					for (Progress pr : Progress.values()) {
						if (pr == Progress.DONE)
							continue;
						for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
							Command command = entry.getValue();
							if (command.getVisisbility() == CommandVisibility.SHOWN && (command.getCategory() == Category.valueOf(args[1].toUpperCase()) || Category.valueOf(args[1].toUpperCase()) == Category.ALL) && command.getProgress() == pr) {
								builder.addField(pr.name().toUpperCase() + ": " + BotInformation.prefix + entry.getKey()[0] + (command.getSyntax() != null && !command.getSyntax().equalsIgnoreCase("") ? " " + command.getSyntax() : ""), LanguageSystem.getTranslatedString("help" + command.getCommand()[0].toLowerCase(), e.getAuthor(), e.getGuild()), false);
							}
						}
					}
					
					channel.sendMessage(builder.build()).queue();
				} else {
					for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
						if (entry.getValue().getVisisbility() == CommandVisibility.SHOWN && new ArrayList<String>(Arrays.asList(entry.getKey())).contains(args[1])) {
							channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).addField(BotInformation.prefix + entry.getKey()[0] + (entry.getValue().getSyntax() != null && !entry.getValue().getSyntax().equalsIgnoreCase("") ? " " + entry.getValue().getSyntax() : ""), LanguageSystem.getTranslatedString("help" + entry.getValue().getCommand()[0].toLowerCase(), e.getAuthor(), e.getGuild()), false).build()).queue();
							return;
						}
					}
					channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Dieser Command wurde nicht gefunden!", "Der Command ``" + args[1] + "`` konnte nicht gefunden werden!", false).build()).queue();
				}
			} else {
				EmbedBuilder builder = new EmbedBuilder()
						.setTitle("Hilfe | Module")
						.setColor(BotInformation.mainColor);
				for (Category c : Category.values()) {
					if (c == Category.ALL)
						continue;
					String commandsInCategory = "";
					for (Map.Entry<String[], Command> entry : CommandBase.commands.entrySet()) {
						if (entry.getValue().getCategory() == c && entry.getValue().getVisisbility() == CommandVisibility.SHOWN)
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
