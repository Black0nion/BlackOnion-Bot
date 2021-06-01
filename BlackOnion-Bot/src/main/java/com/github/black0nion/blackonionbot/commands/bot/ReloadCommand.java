package com.github.black0nion.blackonionbot.commands.bot;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ReloadCommand extends Command {
	
	private static HashMap<String, Method> reloadableMethods = null;
	
	public ReloadCommand() {
		this.setCommand("reload", "rl")
			.botAdminRequired()
			.setHidden();
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(e.getChannel(), Permission.MESSAGE_MANAGE)) return;
		message.delete().queue();
		
		if (true) {
			reloadableMethods = new HashMap<>();
			final Reflections reflections = new Reflections(Logger.class.getPackage().getName());
			System.out.println("hi");
			System.out.println(reflections.getAllTypes());
			System.out.println("hii");
			final List<Class<?>> annotated = reflections.getAllTypes().stream().map(className -> {
				try {
					return Class.forName(className);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				return null;
			}).collect(Collectors.toList());
	
			for (Class<?> clazz : annotated) {
				System.out.println(clazz);
				try {
					Class<?> objectClass = Class.forName(clazz.getName());
					
					for (final Method method : objectClass.getDeclaredMethods()) {
						if (method.isAnnotationPresent(Reloadable.class)) {
							final Reloadable annotation = method.getAnnotation(Reloadable.class);
							reloadableMethods.put(annotation.value(), method);
						}
					}
					
					// this is how you invoke methods:
					// Method method = objectClass.getMethod("setPrefix", String.class);
					// method.invoke(valueObject, objectToSendIn);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		//e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID, "k").queue();
		// TODO: delete after x seconds
		if (args.length >= 2) {
			boolean invalidConfigs = false;
			for (String argument : Utils.removeFirstArg(args)) {
				if (reloadableMethods.containsKey(argument)) {
					Method method = reloadableMethods.get(argument);
					try {
						method.invoke(method.getClass());
						channel.sendMessage(cmde.success().addField(cmde.getTranslation("configreload", new Placeholder("config", argument.toUpperCase())), "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
					} catch (Exception ex) {
						cmde.exception();
					}
				} else {
					invalidConfigs = true;
				}
			}
			if (invalidConfigs)
				channel.sendMessage(cmde.success().addField("invalidconfig", cmde.getTranslation("availableconfigs", new Placeholder("configs", reloadableMethods.toString())), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} else {
			channel.sendMessage(cmde.success().addField("reloading", "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			reload();
			channel.sendMessage(cmde.success().addField("configsreload", "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		}
	}
	
	public static void reload() {
		for (Method m : reloadableMethods.values()) {
			try {
				m.invoke(m.getClass());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}