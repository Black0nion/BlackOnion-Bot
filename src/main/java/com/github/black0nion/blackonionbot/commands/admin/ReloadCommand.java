package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ReloadCommand extends Command {

    private static HashMap<String, Method> reloadableMethods = null;

    public ReloadCommand() {
	this.setCommand("reload", "rl").setHidden();
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (!guild.getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE)) return;
	message.delete().queue();

	if (reloadableMethods == null) {
	    initReloadableMethods();
	}

	if (args.length >= 2) {
	    if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("ls")) {
		String reloadableMethodsString = "```";
		for (final Map.Entry<String, Method> entry : reloadableMethods.entrySet()) {
		    final Method meth = entry.getValue();
		    reloadableMethodsString += "\n" + entry.getKey() + ": " + meth.getDeclaringClass().getSimpleName() + "." + meth.getName();
		}
		channel.sendMessageEmbeds(cmde.success().addField("reloadables", reloadableMethodsString + "```", false).build()).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
		return;
	    }
	    boolean invalidConfigs = false;
	    for (final String argument : Utils.removeFirstArg(args)) if (reloadableMethods.containsKey(argument)) {
		final Method method = reloadableMethods.get(argument);
		try {
		    method.invoke(method.getClass());
		    channel.sendMessageEmbeds(cmde.success().addField(cmde.getTranslation("configreload", new Placeholder("config", argument.toUpperCase())), "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} catch (final Exception ex) {
		    cmde.exception();
		}
	    } else {
		invalidConfigs = true;
	    }
	    if (invalidConfigs) {
		final String translation = cmde.getTranslation("availableconfigs", new Placeholder("configs", reloadableMethods.toString()));
		System.out.println(translation);
		channel.sendMessageEmbeds(cmde.error().addField("invalidconfig", translation, false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
	    }
	} else {
	    channel.sendMessageEmbeds(cmde.success().addField("reloading", "messagedelete5", false).build()).queue(msg -> {
		final ScheduledFuture<?> task = Bot.scheduledExecutor.schedule(() -> {
		    msg.editMessageEmbeds(cmde.error().addField("i fucked up", "lol reload command broken XD go fix it dumbass", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		}, 5, TimeUnit.SECONDS);

		reload();
		task.cancel(true);

		final MessageEmbed builder = cmde.success().addField("configsreload", "messagedelete5", false).build();
		if (msg == null) {
		    channel.sendMessageEmbeds(builder).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} else {
		    msg.editMessageEmbeds(builder).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		}
	    });
	}
    }

    @Reloadable("reloadablemethods")
    private static void initReloadableMethods() {
	reloadableMethods = new HashMap<>();
	// todo: maybe Scanners.MethodsAnnotated.withAnnotation(Reloadable.class)
	final Reflections reflections = new Reflections(Main.class.getPackage().getName(), Scanners.MethodsAnnotated);
	try {
	    reflections.getMethodsAnnotatedWith(Reloadable.class).forEach(method -> {
		reloadableMethods.put(method.getAnnotation(Reloadable.class).value(), method);
	    });
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void reload() {
	if (reloadableMethods == null) {
	    initReloadableMethods();
	}
	for (final Method m : reloadableMethods.values()) {
	    try {
		m.invoke(m.getClass());
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
    }
}