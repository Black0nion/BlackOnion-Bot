package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import com.github.ahitm_2020_2025.blackonionbot.SQL.LiteSQL;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisisbility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownDBCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (member.hasPermission(Permission.ADMINISTRATOR))
			LiteSQL.disconnect();
	}

	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public CommandVisisbility getVisisbility() {
		return CommandVisisbility.HIDDEN;
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"dbshutdown", "shutdowndb", "disconnectdb", "dbdisconnect"};
	}

}
