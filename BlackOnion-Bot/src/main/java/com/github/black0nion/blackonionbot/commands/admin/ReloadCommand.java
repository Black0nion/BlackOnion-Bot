package com.github.black0nion.blackonionbot.commands.admin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
	    cmde.successPrivate("reloadables", getReloadableMethodsString());
	    return;
	} else {
	    final List<OptionMapping> toreload = e.getOptionsByName("toreload");
	    if (toreload.size() == 0 || toreload.get(0).getAsString().equals("")) {
		e.replyEmbeds(cmde.loading().build()).setEphemeral(true).queue(msg -> {
		    final ScheduledFuture<?> task = Bot.scheduledExecutor.schedule(() -> {
			cmde.errorPrivate("i fuckd up", "rl command broken");
		    }, 5, TimeUnit.SECONDS);

		    reload();
		    task.cancel(true);

		    final MessageEmbed builder = cmde.success().addField("configsreload", "messagedelete5", false).build();
		    e.getHook().editOriginalEmbeds(builder).queue();
		});
	    } else {
		boolean invalidConfigs = false;
		final String[] split = toreload.get(0).getAsString().split(" ");
		for (final String argument : split) {
		    if (reloadableMethods.containsKey(argument)) {
			final Method method = reloadableMethods.get(argument);
			try {
			    method.invoke(method.getClass());
			    // channel.sendMessageEmbeds(cmde.success().addField(cmde.getTranslation("configreload",
			    // new Placeholder("config", argument.toUpperCase())), "messagedelete5",
			    // false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			} catch (final Exception ex) {
			    cmde.privateException();
			}
		    } else {
			invalidConfigs = true;
		    }
		}
		if (invalidConfigs) {
		    final String translation = cmde.getTranslation("availableconfigs", new Placeholder("configs", getReloadableMethodsString()));
		    cmde.errorPrivate("invalidconfig", translation);
		} else {
		    cmde.successPrivate("configsreloaded", "configreload", new Placeholder("config", String.join(" ", split).toUpperCase()));
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

    private static String getReloadableMethodsString() {
	return "```" + reloadableMethods.entrySet().stream().map(entry -> {
	    final Method value = entry.getValue();
	    return entry.getKey() + ": " + value.getDeclaringClass().getSimpleName() + "." + value.getName();
	}).collect(Collectors.joining("\n")) + "```";
    }
}