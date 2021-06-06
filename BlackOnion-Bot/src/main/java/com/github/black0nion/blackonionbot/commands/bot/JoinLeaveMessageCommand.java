package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinLeaveMessageCommand extends Command {
	
	public JoinLeaveMessageCommand() {
		this.setCommand("joinleavemessage", "jlm")
			.setRequiredPermissions(Permission.ADMINISTRATOR)
			.setRequiredArgumentCount(2)
			.setSyntax("<join | leave> <message>");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final String newMessage = String.join(" ", Utils.subArray(args, 2));
		if (args[1].equalsIgnoreCase("join")) {
			guild.setJoinMessage(newMessage);
			cmde.success("setjoinmessage", "joinmessagesestto", new Placeholder("msg", "`" + newMessage + "`"));
		} else if (args[1].equalsIgnoreCase("leave")) {
			guild.setLeaveMessage(newMessage);
			cmde.success("setleavemessage", "leavemessagesestto", new Placeholder("msg", "`" + newMessage + "`"));
		} else
			cmde.sendPleaseUse();
	}
}