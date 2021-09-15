package com.github.black0nion.blackonionbot.commands.bot;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.ConfigGetter;
import com.github.black0nion.blackonionbot.misc.ConfigSetResponse;
import com.github.black0nion.blackonionbot.misc.ConfigSetter;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Quadruple;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.entities.TextChannelImpl;

public class ConfigCommand extends Command {

    public ConfigCommand() {
	getters.clear();
	setters.clear();
	both.clear();
	this.setCommand("config").setSyntax("<list | get | set | clear> [config name (only not required for list)]").setRequiredArgumentCount(1).setRequiredPermissions(Permission.ADMINISTRATOR);
	try {
	    for (final Method method : BlackGuild.class.getDeclaredMethods()) {
		if (method.isAnnotationPresent(ConfigSetter.class)) {
		    final ConfigSetter annotation = method.getAnnotation(ConfigSetter.class);
		    setters.put(annotation.key(), new Pair<>(method, annotation));
		} else if (method.isAnnotationPresent(ConfigGetter.class)) {
		    final ConfigGetter annotation = method.getAnnotation(ConfigGetter.class);
		    getters.put(annotation.key(), new Pair<>(method, annotation));
		}
	    }

	    for (final Entry<String, Pair<Method, ConfigGetter>> entry : getters.entrySet()) {
		if (setters.containsKey(entry.getKey())) {
		    final Pair<Method, ConfigGetter> getter = entry.getValue();
		    final Pair<Method, ConfigSetter> setter = setters.get(entry.getKey());
		    both.put(entry.getKey(), new Quadruple<>(getter.getKey(), getter.getValue(), setter.getKey(), setter.getValue()));
		    getters.remove(entry.getKey());
		    setters.remove(entry.getKey());
		}
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Getters: " + getters);
	System.out.println("Setters: " + setters);
	System.out.println("Both: " + both);
    }

    private static final HashMap<String, Pair<Method, ConfigGetter>> getters = new HashMap<>();
    private static final HashMap<String, Pair<Method, ConfigSetter>> setters = new HashMap<>();
    private static final HashMap<String, Quadruple<Method, ConfigGetter, Method, ConfigSetter>> both = new HashMap<>();

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String mode = args[1];
	if (mode.equalsIgnoreCase("list")) {
	    String help = "```";
	    if (getters.size() != 0) {
		help += "Read-only:\n" + getters.entrySet().stream().map(en -> "- " + en.getKey() + " | " + en.getValue().getValue().description()).collect(Collectors.joining("\n"));
	    }
	    if (setters.size() != 0) {
		help += "Only setable:\n" + setters.entrySet().stream().map(s -> "- " + s).collect(Collectors.joining("\n"));
	    }
	    if (both.size() != 0) {
		help += "Get and Setable:\n" + both.entrySet().stream().map(en -> "- " + en.getKey() + " | " + en.getValue().getSecond().description()).collect(Collectors.joining("\n"));
	    }
	    help += "```";
	    cmde.success("configlist", help);
	} else if (mode.equalsIgnoreCase("get")) {
	    if (args.length < 3) {
		cmde.sendPleaseUse();
		return;
	    }
	    final String key = args[2];
	    @Nullable
	    final Pair<Method, ConfigGetter> onlyGetter = getters.get(key);
	    @Nullable
	    final Quadruple<Method, ConfigGetter, Method, ConfigSetter> bothGetterAndSetter = both.get(key);
	    if (onlyGetter == null && bothGetterAndSetter == null) {
		cmde.error("bruh", "unknownkey");
		return;
	    }
	    @Nonnull
	    final Pair<Method, ConfigGetter> finalGetter = onlyGetter != null ? onlyGetter : new Pair<Method, ConfigGetter>(bothGetterAndSetter.getFirst(), bothGetterAndSetter.getSecond());
	    try {
		final Object invoke = finalGetter.getKey().invoke(guild);
		String result = "";
		if (invoke != null) {
		    invoke.toString();
		    if (invoke instanceof TextChannel) {
			result = ((TextChannel) invoke).getAsMention();
		    }
		}
		cmde.success("Value for: %for%", "Current Value: %value%", new Placeholder("for", finalGetter.getValue().key()), new Placeholder("value", invoke == null ? "empty" : result));
	    } catch (final Exception ex) {
		ex.printStackTrace();
		cmde.exception();
	    }
	} else if (Utils.equalsOneIgnoreCase(mode, "set", "clear")) {
	    if (args.length < 3 || (mode.equalsIgnoreCase("set") && args.length < 4)) {
		cmde.sendPleaseUse();
		return;
	    }
	    final String key = args[2];
	    @Nullable
	    final Pair<Method, ConfigSetter> onlySetter = setters.get(key);
	    @Nullable
	    final Quadruple<Method, ConfigGetter, Method, ConfigSetter> bothGetterAndSetter = both.get(key);
	    if (onlySetter == null && bothGetterAndSetter == null) {
		cmde.error("bruh", "unknownkey");
		return;
	    }
	    @Nonnull
	    final Pair<Method, ConfigSetter> finalSetter = onlySetter != null ? onlySetter : new Pair<Method, ConfigSetter>(bothGetterAndSetter.getThird(), bothGetterAndSetter.getFourth());
	    if (!finalSetter.getValue().nullable()) {
		cmde.error("notclearable", "thisnotclearable");
		return;
	    }
	    final Method method = finalSetter.getKey();
	    final Parameter[] parameters = method.getParameters();
	    if (mode.equalsIgnoreCase("set")) {
		final ConfigSetResponse saveValue = saveValue(guild, method, finalSetter, response -> {
		    cmde.success("valueset", "valuesetto %newvalue%", new Placeholder("newvalue", arrayToString(response)));
		    return;
		}, (Object[]) Utils.subArray(args, 3));
		if (saveValue != ConfigSetResponse.SUCCESS) {
		    cmde.error("error", "code: %code%", new Placeholder("code", saveValue.name()));
		}
	    } else {
		final Object[] nullArray = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
		    final Class<?> type = parameters[i].getType();
		    if (type == Long.class || type == Integer.class) {
			nullArray[i] = -1;
		    } else {
			nullArray[i] = null;
		    }
		}
		try {
		    method.invoke(guild, nullArray);
		    cmde.success("configcleared", "yes");
		} catch (final Exception ex) {
		    ex.printStackTrace();
		    cmde.exception();
		}
	    }
	} else {
	    cmde.sendPleaseUse();
	}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ConfigSetResponse saveValue(final Object objectToInvokeMethodIn, final Method method, final Pair<Method, ConfigSetter> pair, final Consumer<Object[]> response, final Object... args) {
	try {
	    final Parameter[] parameters = method.getParameters();
	    final Object[] parsed = new Object[parameters.length];
	    boolean hasArray = false;
	    for (int i = 0; i < args.length; i++) {
		final Class<?> parameterType = parameters[i].getType();
		if (parameterType == String.class) {
		    parsed[i] = String.valueOf(args[i]);
		} else if (parameterType == boolean.class || parameterType == Boolean.class) {
		    parsed[i] = (boolean) args[i];
		} else if (parameterType == TextChannel.class) {
		    final Object arg = args[i];
		    if (arg instanceof String) {
			parsed[i] = Bot.jda.getTextChannelById((String) arg);
		    } else if (arg instanceof Long) {
			parsed[i] = Bot.jda.getTextChannelById((Long) arg);
		    } else {
			args[i] = null;
		    }
		    if (args[i] == null) return ConfigSetResponse.NOSUCHCHANNEL;
		} else if (parameterType == long.class || parameterType == Long.class) {
		    if (!Utils.isLong(args[1])) return ConfigSetResponse.NOTANUMBER;
		    parsed[i] = Dashboard.parseLong(args[i]);
		} else if (parameterType == int.class || parameterType == Integer.class) {
		    if (!Utils.isInteger(args[i])) return ConfigSetResponse.NOTANUMBER;
		    parsed[i] = Dashboard.parseInt(args[i]);
		} else if (parameterType.isEnum()) {
		    final Method parse = parameterType.getDeclaredMethod("parse", String.class);
		    if (parse != null) {
			parsed[i] = parse.invoke(parameterType, (String) args[i]);
		    } else {
			parsed[i] = Enum.valueOf((Class<? extends Enum>) parameterType, (String) args[i]);
		    }
		} else if (parameterType.isArray()) {
		    hasArray = true;
		    final Class<?> clazz = parameterType.getComponentType();
		    if (clazz == String.class) {
			final String[] obj = new String[args.length - i];
			for (int j = i; j < args.length; j++) {
			    obj[j - i] = String.valueOf(args[j]);
			}
			parsed[i] = obj;
		    } else if (clazz == Long.class) {
			parsed[i] = Dashboard.parseLong(i, args);
		    } else if (clazz == long.class) {
			parsed[i] = Dashboard.parseLongPrimitive(i, args);
		    } else if (clazz == Integer.class) {
			parsed[i] = Dashboard.parseInteger(i, args);
		    } else if (clazz == int.class) {
			parsed[i] = Dashboard.parseIntegerPrimitive(i, args);
		    } else if (clazz == Object.class) {
			final Object[] obj = new Object[args.length - i];
			for (int j = i; j < args.length; j++) {
			    obj[j - i] = args[j];
			}
			parsed[i] = obj;
		    } else {
			Logger.logError("No array casting way provided for " + clazz, LogOrigin.DASHBOARD);
		    }
		    break;
		} else {
		    parsed[i] = Dashboard.getValue(parameterType, args[i]);
		}
		if ((parsed[i] == null || (Utils.isLong(parsed[i]) && (Long) parsed[i] == -1))) throw new IllegalArgumentException("args[" + i + "] is null, should be of type " + parameterType.getName() + "!");
	    }
	    if (hasArray) {
		for (int i = 0; i < parsed.length; i++) {
		    if ((parsed[i] == null || (Utils.isLong(parsed[i]) && (Long) parsed[i] == -1))) throw new IllegalArgumentException("args[" + i + "] is null!");
		}
	    }
	    method.invoke(objectToInvokeMethodIn, parsed);
	    response.accept(parsed);
	    return ConfigSetResponse.SUCCESS;
	} catch (final Exception e) {
	    e.printStackTrace();
	    if (!(e instanceof IllegalArgumentException)) {
		e.printStackTrace();
	    }
	    return ConfigSetResponse.ERROR;
	}
    }

    private static String arrayToString(final Object[] input) {
	final String[] result = new String[input.length];
	for (int i = 0; i < input.length; i++) {
	    final Object obj = input[i];
	    final Class<?> type = obj.getClass();
	    if (type == TextChannel.class || type == TextChannelImpl.class) {
		result[i] = ((TextChannel) obj).getAsMention();
	    } else {
		result[i] = obj.toString();
	    }
	}
	return String.join(", ", result);
    }
}