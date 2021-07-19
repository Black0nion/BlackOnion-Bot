package com.github.black0nion.blackonionbot.commands.admin;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ReloadCommand extends SlashCommand {

    private static HashMap<String, Method> reloadableMethods = null;

    public ReloadCommand() {
	this.setData(new CommandData("reload", "Reloads a few configs").addOptions(new OptionData(OptionType.BOOLEAN, "showlist", "Show a overview of all reloadable configs?", false), new OptionData(OptionType.STRING, "toreload", "Configs to reload", false))).setHidden();
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (reloadableMethods == null) {
	    initReloadableMethods();
	}
	// e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID,
	// "k").queue();
	final List<OptionMapping> showlist = e.getOptionsByName("showlist");
	if (showlist.size() != 0 && showlist.get(0).getAsBoolean()) {
	    String reloadableMethodsString = "```";
	    for (final Map.Entry<String, Method> entry : reloadableMethods.entrySet()) {
		final Method meth = entry.getValue();
		reloadableMethodsString += "\n" + entry.getKey() + ": " + meth.getDeclaringClass().getSimpleName() + "." + meth.getName();
	    }
	    channel.sendMessageEmbeds(cmde.success().addField("reloadables", reloadableMethodsString + "```", false).build()).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
	    return;
	} else {
	    final List<OptionMapping> toreload = e.getOptionsByName("toreload");
	    if (toreload.size() == 0 || toreload.get(0).getAsString().equals("")) {
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
	    } else {
		boolean invalidConfigs = false;
		for (final String argument : toreload.get(0).getAsString().split(" ")) {
		    if (reloadableMethods.containsKey(argument)) {
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
		}
		if (invalidConfigs) {
		    final String translation = cmde.getTranslation("availableconfigs", new Placeholder("configs", reloadableMethods.toString()));
		    System.out.println(translation);
		    channel.sendMessageEmbeds(cmde.error().addField("invalidconfig", translation, false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		}
	    }
	}
    }

    @Reloadable("reloadablemethods")
    private static void initReloadableMethods() {
	reloadableMethods = new HashMap<>();
	final Reflections reflections = new Reflections(Main.class.getPackage().getName(), new MethodAnnotationsScanner());
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