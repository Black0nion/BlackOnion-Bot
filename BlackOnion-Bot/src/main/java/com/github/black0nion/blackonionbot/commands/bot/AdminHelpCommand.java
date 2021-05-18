package com.github.black0nion.blackonionbot.commands.bot;

import java.awt.Color;
import java.time.Duration;
import java.util.Map;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AdminHelpCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"adminhelp"};
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		message.delete().queue();
		EmbedBuilder builder = EmbedUtils.getErrorEmbed(author, guild)
				.setTitle("Adminhilfe")
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F));
		
		for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
			if (entry.getValue().getVisisbility() == CommandVisibility.HIDDEN && entry.getValue().getCommand()[0] != getCommand()[0]) {
				builder.addField(Utils.getCommandHelp(guild, author, entry.getValue()), "help" + entry.getValue().getCommand()[0].toLowerCase(), false);
			}
		}
		
		channel.sendMessage(builder.build()).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
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
