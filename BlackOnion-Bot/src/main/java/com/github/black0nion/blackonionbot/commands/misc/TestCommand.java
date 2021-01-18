package com.github.black0nion.blackonionbot.commands.misc;

import java.awt.Color;
import java.io.File;

import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.enums.DrawType;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.systems.JoinSystem;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TestCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"test"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		File file;
		try {
			file = JoinSystem.generateImage(Color.black, member, guild, DrawType.JOIN);
			guild.getTextChannelById("800032895803719751").sendFile(file, "welcome.png").queue();
			GuildManager.createGuildOptions(guild.getId());
			System.out.println(GuildManager.getGuildSettings(guild.getId()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.MESSAGE_MANAGE};
	}

}
