package com.github.black0nion.blackonionbot.commands.misc;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TestCommand extends Command {
	
	public TestCommand() {
		this.setCommand("test", "tet")
			.setRequiredArgumentCount(2);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final Method method = Dashboard.setters.get(args[1]);
		Object[] obj = Arrays.asList(Utils.removeFirstArg(Utils.removeFirstArg(args))).stream().map(map -> (Object) map).toArray();
		System.out.println(Dashboard.saveValue(guild, method, obj));
	}
}