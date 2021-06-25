package com.github.black0nion.blackonionbot.systems.dashboard;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackHashMap;
import com.github.black0nion.blackonionbot.blackobjects.BlackObject;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

public class Dashboard {

    public static final HashMap<String, Method> setters = new HashMap<>();
    public static final HashMap<String, Method> getters = new HashMap<>();

    public static final JSONObject dashboardJson = new JSONObject();

    public static void init() {
	getters.clear();
	setters.clear();
	dashboardJson.clear();

	final Reflections reflections = new Reflections(BlackObject.class.getPackage().getName());
	final Set<Class<? extends BlackObject>> annotated = reflections.getSubTypesOf(BlackObject.class);

	for (final Class<?> blackobject : annotated) {
	    try {
		final Class<?> objectClass = Class.forName(blackobject.getName());

		for (final Method method : objectClass.getDeclaredMethods()) {
		    if (method.isAnnotationPresent(DashboardSetter.class)) {
			final DashboardSetter annotation = method.getAnnotation(DashboardSetter.class);
			setters.put(annotation.value(), method);
		    }
		}

		for (final Method method : objectClass.getDeclaredMethods()) {
		    if (method.isAnnotationPresent(DashboardGetter.class)) {
			final DashboardGetter annotation = method.getAnnotation(DashboardGetter.class);

			final String categoryId = annotation.category().getId();
			if (!dashboardJson.has(categoryId)) {
			    dashboardJson.put(categoryId, new JSONObject());
			}

			final JSONObject includingPages = dashboardJson.getJSONObject(categoryId);
			final String pageId = annotation.page().getId();
			if (!includingPages.has(pageId)) {
			    includingPages.put(pageId, new JSONObject());
			}

			final JSONObject includingSections = includingPages.getJSONObject(pageId);
			final String sectionId = annotation.section().getId();
			if (!includingSections.has(sectionId)) {

			}

			final BlackHashMap<Object, Object> methObj = new BlackHashMap<>().add("id", annotation.id()).add("prettyName", annotation.prettyName()).add("nullable", annotation.nullable());
			if (setters.get(annotation.id()) == null) {
			    methObj.put("readonly", true);
			} else {
			    methObj.put("readonly", annotation.readonly());
			}
			pagesObj.getJSONArray(pageId).put(methObj);
		    }
		}
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
	Logger.logInfo("Generated Dashboard JSON: " + dashboardJson, LogOrigin.DASHBOARD);
    }

    public static void tryUpdateValue(final JSONObject message, final DiscordUser user, final Consumer<ResponseCode> callback) {
	if (!(message.has("guildid") && message.has("setting") && message.has("values"))) {
	    callback.accept(ResponseCode.WRONG_ARGUMENTS);
	    return;
	}
	final Method method = setters.get(message.getString("setting"));
	if (method == null) {
	    callback.accept(ResponseCode.WRONG_SETTING);
	    return;
	}
	final BlackGuild guild = BlackGuild.from(message.getLong("guildid"));
	if (guild == null) {
	    callback.accept(ResponseCode.NO_GUILD);
	    return;
	}
	if (!guild.retrieveMemberById(user.getUserId()).submit().join().hasPermission(Permission.MANAGE_SERVER)) {
	    callback.accept(ResponseCode.NO_PERMISSIONS);
	    return;
	}
	final List<Object> list = message.getJSONArray("values").toList();
	if (saveValue(guild, method, list.toArray())) {
	    callback.accept(ResponseCode.SUCCESS);
	} else {
	    callback.accept(ResponseCode.PARSE_ERROR);
	}
    }

    public static final boolean saveValue(final Object objectToInvokeMethodIn, final Method method, final Object... args) {
	try {
	    final Object[] parsed = new Object[args.length];
	    final Parameter[] parameters = method.getParameters();
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
		    }
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

    public static JSONArray parseArguments(final Parameter[] parameters) {
	try {
	    final JSONArray result = new JSONArray();
	    for (final Parameter parameter : parameters) {
		final Class<?> type = parameter.getType();
		if (type == String.class) {
		    result.put("STRING");
		} else if (type == Long.class || type == long.class) {
		    result.put("LONG");
		} else if (type == TextChannel.class) {
		    result.put("CHANNEL");
		} else if (type.isEnum()) {
		    result.put(new JSONObject().put("MULTIPLE_CHOICE", type.getEnumConstants()));
		} else if (type.isArray()) {
		    result.put(new JSONObject().put("ARRAY", type.getSimpleName().toUpperCase().replace("[]", "")));
		} else if (List.class.isAssignableFrom(type)) {
		    result.put(new JSONObject().put("LIST", type));
		} else return null;
	    }
	    return result;
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
}