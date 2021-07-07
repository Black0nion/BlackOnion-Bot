package com.github.black0nion.blackonionbot.commands.bot;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ConfigCommand extends Command {

    public ConfigCommand() {
	this.setCommand("config").setSyntax("<list | set | status> [config name (only not required for list)").setRequiredArgumentCount(1).setRequiredPermissions(Permission.ADMINISTRATOR);
	configMethods.clear();
	try {
	    for (final Method method : BlackGuild.class.getDeclaredMethods()) {
		if (method.isAnnotationPresent(Config.class)) {
		    final Config annotation = method.getAnnotation(Config.class);
		    configMethods.put(method, annotation);
		}
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    private static final HashMap<Method, Config> configMethods = new HashMap<>();

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String mode = args[1];
	if (mode.equalsIgnoreCase("list")) {
	    cmde.success("configlist", configMethods.keySet().toString());
	}
    }
}