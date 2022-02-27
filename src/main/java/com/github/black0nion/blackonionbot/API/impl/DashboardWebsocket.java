package com.github.black0nion.blackonionbot.API.impl;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.BlackWebsocketSession;
import com.github.black0nion.blackonionbot.API.WebSocketEndpoint;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.github.black0nion.blackonionbot.systems.dashboard.ResponseCode.*;

@WebSocket
public class DashboardWebsocket extends WebSocketEndpoint {

	public DashboardWebsocket() {
		this.setRoute("dashboard");
	}

	private static final boolean LOG_HEARTBEATS = Config.log_heartbeats;

	private static final List<Session> sessions = new ArrayList<>();

	private static final LoadingCache<Session, BlackWebsocketSession> blackWebsocketSessions = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build(new CacheLoader<Session, BlackWebsocketSession>() {
		@Override
		public BlackWebsocketSession load(final @NotNull Session key) throws IllegalArgumentException {
			return new BlackWebsocketSession(key);
		}
	});

	private static final HashMap<Session, ScheduledFuture<?>> futures = new HashMap<>();

	@OnWebSocketConnect
	public void connected(final Session sessionRaw) {
		try {
			final String sessionId = sessionRaw.getUpgradeRequest().getHeader("Sec-WebSocket-Protocol");
			if (sessionId == null) {
				sessionRaw.close(4401, "Unauthorized");
				return;
			}
			final Document doc = BlackSession.collection.find(Filters.eq("sessionid", sessionId)).first();
			if (doc == null) {
				sessionRaw.close(4401, "Unauthorized");
				return;
			}
			boolean hasError = false;
			BlackWebsocketSession session = null;
			try {
				session = blackWebsocketSessions.get(sessionRaw);
			} catch (Exception e) {
				hasError = true;
			}
			if (hasError || session.getUser() == null) {
				sessionRaw.close(4401, "Unauthorized");
				return;
			}
			LOGGED_IN.send(session, null);
			sessions.add(session);
			futures.put(sessionRaw, this.scheduleTimeout(session));
			Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Connected to Dashboard Websocket.", LogOrigin.DASHBOARD);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@OnWebSocketClose
	public void closed(final Session session, final int statusCode, final String reason) {
		sessions.remove(session);
	}

	@OnWebSocketMessage
	public void message(final Session sessionUnchecked, final String messageRaw) {
		final BlackWebsocketSession session = blackWebsocketSessions.getUnchecked(sessionUnchecked);
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Received: " + messageRaw.replace("\n", "\\n"), LogOrigin.DASHBOARD);
		if (messageRaw.charAt(0) == 'r') {
			try {
				final String[] args = messageRaw.split(" ");
				final int id = args[0].length();
				if (args.length < 2 || !Utils.isInteger(messageRaw.substring(1, id))) {
					WRONG_ARGUMENTS.send(session, null);
					return;
				}
				final JSONObject request = new JSONObject(messageRaw.substring(id)).put("id", messageRaw.substring(1, id));
				if (!request.has("action")) {
					NO_ACTION.send(session, request);
					return;
				}
				final String command = request.getString("action");
				if (command.equalsIgnoreCase("updatesetting")) {
					Dashboard.tryUpdateValue(request, session.getUser(), code -> code.send(session, request));
				} else if (command.equalsIgnoreCase("userinfo")) {
					reply(session, request, session.getUser());
				} else if (command.equalsIgnoreCase("guildsettings")) {
					if (!(request.has("guildid") && request.has("settings"))) {
						WRONG_ARGUMENTS.send(session, request);
						return;
					}
					BlackGuild guild;
					try {
						guild = getGuild(session, request);
					} catch (final Exception e) {
						return;
					}
					guild.retrieveMemberById(session.getUser().getUserId()).queue(member -> {
						if (!member.hasPermission(Permission.MESSAGE_MANAGE)) {
							UNAUTHORIZED.send(session, request);
							return;
						}
						final JSONObject response = new JSONObject();
						final JSONArray settings = request.getJSONArray("settings");
						for (int i = 0; i < settings.length(); i++) {
							try {
								if (!(settings.get(i) instanceof String)) {
									continue;
								}
								final String arg = settings.getString(i);
								if (!Dashboard.getters.containsKey(arg)) {
									response.put(arg, "null");
								} else {
									response.put(arg, Dashboard.getters.get(arg).invoke(guild));
								}
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
						reply(session, request, response);
					});
				} else if (command.equalsIgnoreCase("guildchannels")) {
					if (!(request.has("guildid"))) {
						WRONG_ARGUMENTS.send(session, request);
						return;
					}
					BlackGuild guild;
					try {
						guild = getGuild(session, request);
					} catch (final Exception e) {
						return;
					}
					guild.retrieveMemberById(session.getUser().getUserId()).queue(member -> {
						if (!member.hasPermission(Permission.MESSAGE_MANAGE)) {
							UNAUTHORIZED.send(session, request);
							return;
						}
						final JSONArray response = new JSONArray();
						final JSONObject channelsWithoutCategory = new JSONObject().put("id", -1);
						final JSONArray chs = new JSONArray();
						guild.getTextChannels().stream().filter(c -> c.getParentCategory() == null).forEach(channel -> chs.put(new JSONObject().put("name", channel.getName()).put("id", channel.getId())));
						if (!chs.isEmpty()) {
							response.put(channelsWithoutCategory.put("channels", chs));
						}
						guild.getCategories().forEach(cat -> {
							final JSONObject categoryInfo = new JSONObject().put("name", cat.getName()).put("id", cat.getId());
							final JSONArray channels = new JSONArray();
							cat.getTextChannels().forEach(ch -> channels.put(new JSONObject().put("name", ch.getName()).put("id", ch.getId())));
							response.put(categoryInfo.put("channels", channels));
						});
						//guild.getTextChannels().forEach(ch -> response.put(new JSONObject().put("name", ch.getName()).put("id", ch.getIdLong())));
						reply(session, request, response);
					});
				}
			} catch (final Exception e) {
				if (!(e instanceof JSONException)) {
					e.printStackTrace();
				} else {
					session.close(4400, "wrong json dumbass");
				}
			}
		} else if (messageRaw.startsWith("heartbeat")) {
			futures.get(sessionUnchecked).cancel(true);
			futures.put(sessionUnchecked, this.scheduleTimeout(session));
			if (LOG_HEARTBEATS) {
				Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Heartbeat.", LogOrigin.DASHBOARD);
			}
			final String id = messageRaw.substring(9);
			session.send("heartbeat" + id);
		} else {
			reply(session, null, INVALID_TYPE.getJson());
		}
	}

	private BlackGuild getGuild(BlackWebsocketSession session, JSONObject request) throws IllegalArgumentException {
		final Object guildid = request.get("guildid");
		BlackGuild guild;
		if (guildid instanceof String) {
			guild = BlackGuild.from(Long.parseLong((String) guildid));
		} else if (guildid instanceof Long) {
			guild = BlackGuild.from((Long) guildid);
		} else {
			NO_GUILD.send(session, request);
			throw new IllegalArgumentException("Invalid type of Input");
		}
		if (guild == null) {
			NO_GUILD.send(session, request);
			throw new IllegalArgumentException("Guild not found");
		}
		return guild;
	}

	@OnWebSocketError
	public void onError(final Session session, final Throwable error) {
		error.printStackTrace();
		try {
			session.getRemote().sendString("Some error happened :/");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private ScheduledFuture<?> scheduleTimeout(final BlackWebsocketSession session) {
		return Bot.scheduledExecutor.schedule(() -> {
			Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Timed Out.", LogOrigin.DASHBOARD);
			session.close(4408, "Mach dich aus meiner Leitung raus, du Birne!");
		}, 1, TimeUnit.MINUTES);
	}

	public static void reply(final BlackWebsocketSession session, final @Nullable JSONObject request, @Nullable Object response) {
		if (response == null) {
			response = new JSONObject();
		} else if (!(response instanceof JSONObject) && !(response instanceof JSONArray)) {
			response = Bot.gson.toJson(response);
		}

		if (request != null && request.has("id")) {
			session.send("a" + request.getInt("id") + " " + response);
		} else {
			session.send("n" + response);
		}
	}
}