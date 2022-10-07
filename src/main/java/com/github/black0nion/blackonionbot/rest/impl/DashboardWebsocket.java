package com.github.black0nion.blackonionbot.rest.impl;

import com.github.black0nion.blackonionbot.rest.sessions.AbstractSession;
import com.github.black0nion.blackonionbot.rest.sessions.WebSocketSession;
import com.github.black0nion.blackonionbot.rest.api.IWebSocketEndpoint;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.Permission;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.github.black0nion.blackonionbot.systems.dashboard.ResponseCode.*;

public class DashboardWebsocket implements IWebSocketEndpoint {

	@Override
	public @Nonnull String url() {
		return "dashboard";
	}

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final List<Session> sessions = new ArrayList<>();

	private static final LoadingCache<Session, WebSocketSession> blackWebsocketSessions = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Override
		public @NotNull WebSocketSession load(final @NotNull Session key) throws IllegalArgumentException {
			return new WebSocketSession(key);
		}
	});

	private static final HashMap<Session, ScheduledFuture<?>> futures = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(DashboardWebsocket.class);

	@Override
	public void onConnect(Session sessionRaw) {
		logger.info("IP {} tried to connect to the dashboard websocket", sessionRaw.getRemoteAddress());
		try {
			final String sessionId = sessionRaw.getUpgradeRequest().getHeader("Sec-WebSocket-Protocol");
			if (sessionId == null || !sessionId.matches(AbstractSession.generateSessionId())) {
				logger.debug("IP {} tried to connect to the dashboard websocket with an invalid session id", sessionRaw.getRemoteAddress());
				sessionRaw.close(4401, "Unauthorized");
				return;
			}

			WebSocketSession session;
			try {
				session = blackWebsocketSessions.get(sessionRaw);
			} catch (Exception e) {
				e.printStackTrace();
				Throwable cause = e;
				while ((cause = cause.getCause()) != null) {
					if (cause instanceof HttpStatusException http) {
						sessionRaw.close(4000 + http.getStatusCode(), "Unauthorized");
						return;
					}
				}
				sessionRaw.close(4401, "Unauthorized");
				return;
			}

			if (session.getUser() == null) {
				sessionRaw.close(4401, "Unauthorized");
				return;
			}

			LOGGED_IN.send(session);
			sessions.add(session);
			futures.put(sessionRaw, this.scheduleTimeout(session));
			logger.info("IP {} connected to dashboard websocket", session.getIp());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(final Session session, final int statusCode, final String reason) {
		sessions.remove(session);
	}

	@Override
	public void onMessage(final Session sessionUnchecked, final String messageRaw) {
		final WebSocketSession session = blackWebsocketSessions.getUnchecked(sessionUnchecked);
		logger.info("IP {}: received {}", session.getIp(), messageRaw.replace("\n", "\\n"));
		if (messageRaw.charAt(0) == 'r') {
			try {
				final String[] args = messageRaw.split(" ");
				final int id = args[0].length();
				if (args.length < 2 || !Utils.isInteger(messageRaw.substring(1, id))) {
					WRONG_ARGUMENTS.send(session);
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
					guild.retrieveMemberById(session.getUser().getUser().getId()).queue(member -> {
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
					guild.retrieveMemberById(session.getUser().getUser().getId()).queue(member -> {
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
			session.heartbeat(messageRaw.substring(9));
			logger.debug("IP {} sent heartbeat", session.getIp());
		} else {
			reply(session, null, INVALID_TYPE.getJson());
		}
	}

	private BlackGuild getGuild(WebSocketSession session, JSONObject request) throws IllegalArgumentException {
		final Object guildid = request.get("guildid");
		BlackGuild guild;
		if (guildid instanceof String string) {
			guild = BlackGuild.from(Long.parseLong(string));
		} else if (guildid instanceof Long input) {
			guild = BlackGuild.from(input);
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

	@Override
	public void onError(final Session session, final Throwable error) {
		logger.error("Error on session {}: {}", session.getRemoteAddress(), error.fillInStackTrace());
		try {
			session.getRemote().sendString("Some error happened :/");
		} catch (final IOException e) {
			if (e instanceof ClosedChannelException || e.getCause() instanceof ClosedChannelException) return;
			logger.error("Error while sending error message to client", e);
		}
	}

	private ScheduledFuture<?> scheduleTimeout(final WebSocketSession session) {
		return Bot.getInstance().getScheduledExecutor().schedule(() -> {
			logger.info("IP {} timed out.", session.getIp());
			session.close(4408, "Timed out.");
		}, 1, TimeUnit.MINUTES);
	}

	public static void reply(final WebSocketSession session, final @Nullable JSONObject request, @Nullable Object response) {
		if (response == null) {
			response = new JSONObject();
		} else if (!(response instanceof JSONObject) && !(response instanceof JSONArray)) {
			response = Bot.GSON.toJson(response);
		}

		if (request != null && request.has("id")) {
			session.send("a" + request.getInt("id") + " " + response);
		} else {
			session.send("n" + response);
		}
	}
}
