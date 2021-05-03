package com.github.black0nion.blackonionbot.commands.bot;

import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ShutdownDBCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		//TODO: disconnect
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("MongoDB", "Disconnected.", false).build()).submit().join().delete().queueAfter(3,TimeUnit.SECONDS);
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
