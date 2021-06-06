package com.github.black0nion.blackonionbot.commands.bot;

import java.awt.Color;
import java.time.Duration;
import java.util.Map;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AdminHelpCommand extends Command {
	
	public AdminHelpCommand() {
		this.setCommand("adminhelp")
			.botAdminRequired()
			.setHidden()
			.setCategory(Category.BOT);
	}

	@Override
	public String[] getCommand() {
		return new String[] { "adminhelp" };
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		message.delete().queue();
		final EmbedBuilder builder = EmbedUtils.getErrorEmbed(author, guild)
				.setTitle("Adminhilfe")
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F));
		
		for (final Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet())
			if (entry.getValue().getVisibility() == CommandVisibility.HIDDEN && entry.getValue().getCommand()[0] != getCommand()[0])
				builder.addField(CommandEvent.getCommandHelp(guild, author, entry.getValue()), "help" + entry.getValue().getCommand()[0].toLowerCase(), false);
		
		channel.sendMessage(builder.build()).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
	}
}