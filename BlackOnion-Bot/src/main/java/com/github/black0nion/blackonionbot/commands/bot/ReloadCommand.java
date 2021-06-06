package com.github.black0nion.blackonionbot.commands.bot;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(e.getChannel(), Permission.MESSAGE_MANAGE)) return;
		message.delete().queue();
		
		if (reloadableMethods == null) {
			reloadableMethods = new HashMap<>();
			final Reflections reflections = new Reflections(Logger.class.getPackage().getName(), new MethodAnnotationsScanner());
			try {
				reflections.getMethodsAnnotatedWith(Reloadable.class).forEach((method) -> {
					reloadableMethods.put(method.getAnnotation(Reloadable.class).value(), method);
				});
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
		
		//e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID, "k").queue();
		// TODO: delete after x seconds
		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("ls")) {
				String reloadableMethodsString = "```";
				for (final Map.Entry<String, Method> entry : reloadableMethods.entrySet()) {
					final Method meth = entry.getValue();
					reloadableMethodsString += "\n" + entry.getKey() + ": " + meth.getDeclaringClass().getSimpleName() + "." + meth.getName();
				}
				channel.sendMessage(cmde.success().addField("reloadables", reloadableMethodsString + "```", false).build()).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
				return;
			}
			boolean invalidConfigs = false;
			for (final String argument : Utils.removeFirstArg(args))
				if (reloadableMethods.containsKey(argument)) {
					final Method method = reloadableMethods.get(argument);
					try {
						method.invoke(method.getClass());
						channel.sendMessage(cmde.success().addField(cmde.getTranslation("configreload", new Placeholder("config", argument.toUpperCase())), "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
					} catch (final Exception ex) {
						cmde.exception();
					}
				} else
					invalidConfigs = true;
			if (invalidConfigs) {
				final String translation = cmde.getTranslation("availableconfigs", new Placeholder("configs", reloadableMethods.toString()));
				System.out.println(translation);
				channel.sendMessage(cmde.error().addField("invalidconfig", translation, false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			}
		} else
			channel.sendMessage(cmde.success().addField("reloading", "messagedelete5", false).build()).queue(msg -> {
				final ScheduledFuture<?> task = Bot.scheduledExecutor.schedule(() -> {
					msg.editMessage(cmde.error().addField("i fucked up", "lol reload command broken XD go fix it dumbass", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				}, 5, TimeUnit.SECONDS);
				
				reload();
				task.cancel(true);
				
				final MessageEmbed builder = cmde.success().addField("configsreload", "messagedelete5", false).build();
				if (msg == null)
					channel.sendMessage(builder).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				else
					msg.editMessage(builder).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			});
	}
	
	public static void reload() {
		for (final Method m : reloadableMethods.values())
			try {
				m.invoke(m.getClass());
			} catch (final Exception e) {
				e.printStackTrace();
			}
	}
}