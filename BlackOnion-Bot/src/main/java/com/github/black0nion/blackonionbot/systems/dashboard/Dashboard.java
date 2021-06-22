package com.github.black0nion.blackonionbot.systems.dashboard;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackObject;
import com.github.black0nion.blackonionbot.utils.Utils;

public class Dashboard {

    // TODO: better name XD
    public static final HashMap<String, Method> setters = new HashMap<>();

    public static void init() {
	setters.clear();

	final Reflections reflections = new Reflections(BlackObject.class.getPackage().getName());
	final Set<Class<? extends BlackObject>> annotated = reflections.getSubTypesOf(BlackObject.class);

	for (final Class<?> blackobject : annotated) {
	    try {
		final Class<?> objectClass = Class.forName(blackobject.getName());

		for (final Method method : objectClass.getDeclaredMethods()) if (method.isAnnotationPresent(com.github.black0nion.blackonionbot.misc.DashboardValue.class)) {
		    final com.github.black0nion.blackonionbot.misc.DashboardValue annotation = method.getAnnotation(com.github.black0nion.blackonionbot.misc.DashboardValue.class);
		    setters.put(annotation.value(), method);
		}

		// this is how you invoke methods:
		// Method method = objectClass.getMethod("setPrefix", String.class);
		// method.invoke(valueObject, objectToSendIn);
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public static boolean tryUpdateValue(final String message) {
	// should be only "updatevalue"
	final String[] input = message.split(" ");
	// syntax: guildid key values...
	final String[] args = Utils.removeFirstArg(input);
	final String guildid = args[0];
	if (args.length < 3 || !Utils.isLong(guildid)) return false;
	final Method method = setters.get(args[1]);
	if (method == null) return false;
	final BlackGuild guild = BlackGuild.from(Long.parseLong(guildid));
	if (guild == null) return false;
	return saveValue(guild, method, Utils.toObjectArray(Utils.subArray(args, 2)));
    }

    public static final boolean saveValue(final Object objectToInvokeMethodIn, final Method method, final Object... args) {
	try {
	    final Object[] parsed = new Object[args.length];
	    final Parameter[] parameters = method.getParameters();
	    for (int i = 0; i < args.length; i++) {
		final Class<?> parameterType = parameters[i].getType();
		if (parameterType == boolean.class || parameterType == Boolean.class) {
		    parsed[i] = (boolean) args[i];
		} else if (parameterType == long.class || parameterType == Long.class) {
		    parsed[i] = parseLong(args[i]);
		} else if (parameterType == int.class || parameterType == Integer.class) {
		    parsed[i] = parseInt(args[i]);
		} else if (parameterType.isEnum()) {
		    final Method parse = parameterType.getDeclaredMethod("parse", String.class);
		    if (parse != null) {
			parsed[i] = parse.invoke(parameterType, (String) args[i]);
		    } else {
			parsed[i] = parameterType.getDeclaredMethod("valueOf", String.class).invoke(parameterType, ((String) args[i]).toUpperCase());
		    }
		} else if (parameterType.isArray()) {
		    final Object[] newObject = new Object[args.length - i];
		    for (int j = i; j < args.length; j++) {
			newObject[i] = args[j];
		    }
		    parsed[i] = newObject;
		    break;
		} else {
		    parsed[i] = getValue(parameterType, args[i]);
		}
		if (parsed[i] == null) throw new IllegalArgumentException("args[" + i + "] is null, should be of type " + parameterType.getName() + "!");
	    }
	    method.invoke(objectToInvokeMethodIn, parsed);
	    return true;
	} catch (final Exception e) {
	    e.printStackTrace();
	    if (!(e instanceof IllegalArgumentException)) {
		e.printStackTrace();
	    }
	    return false;
	}
    }

    private static <T> T getValue(final Class<T> desiredType, final Object o) {
	if (o.getClass().isAssignableFrom(desiredType)) return desiredType.cast(o);
	else throw new IllegalArgumentException();
    }

    private static long parseLong(final Object obj) {
	try {
	    return Long.parseLong((String) obj);
	} catch (final Exception e) {
	    throw new IllegalArgumentException();
	}
    }

    private static int parseInt(final Object obj) {
	try {
	    return Integer.parseInt((String) obj);
	} catch (final Exception e) {
	    throw new IllegalArgumentException();
	}
    }
}