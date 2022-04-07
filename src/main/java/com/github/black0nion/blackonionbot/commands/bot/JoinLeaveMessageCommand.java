package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class JoinLeaveMessageCommand extends SlashCommand {

	public JoinLeaveMessageCommand() {
		this.setCommand("joinleavemessage", "jlm")
			.setRequiredPermissions(Permission.ADMINISTRATOR)
			.setRequiredArgumentCount(2)
			.setSyntax("<join | leave> <message>");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
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