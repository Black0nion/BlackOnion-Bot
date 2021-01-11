package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.SQL.LiteSQL;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownDBCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		LiteSQL.disconnect();
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).addField("LiteSQL", "Disconnected.", false).build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
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
	public String[] getCommand() {
		return new String[] {"dbshutdown", "shutdowndb", "disconnectdb", "dbdisconnect"};
	}

}
