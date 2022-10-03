package com.github.black0nion.blackonionbot.rest.impl.get;

import com.github.black0nion.blackonionbot.rest.api.IGetRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import io.javalin.Javalin;
import io.javalin.core.event.HandlerMetaInfo;
import io.javalin.core.event.WsHandlerMetaInfo;
import io.javalin.core.plugin.Plugin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class Paths implements IGetRoute {

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, DiscordUser user) throws Exception {
		PathListener instance = PathListener.getInstance();
		Map<HandlerType, List<String>> collected = instance.handlerMetaInfoList.stream().collect(
			Collectors.groupingBy(HandlerMetaInfo::getHttpMethod, mapping(HandlerMetaInfo::getPath, toList()))
		);
		return new JSONObject(collected).put("WS", new JSONArray(instance.wsHandlerMetaInfoList.stream().map(WsHandlerMetaInfo::getPath).toList()));
	}

	@Override
	public @Nonnull String url() {
		return "paths";
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public CustomPermission[] requiredCustomPermissions() {
		return new CustomPermission[] { CustomPermission.ADMIN };
	}

	/**
	 * A listener that listens for all the paths that are registered to the Javalin instance.
	 * Taken from {@link io.javalin.core.util.RouteOverviewConfig RouteOverviewConfig} and {@link io.javalin.core.util.RouteOverviewUtil RouteOverviewUtil}
	 * @author tipsy, SIMULATAN
	 */
	public static class PathListener implements Plugin {
		final List<HandlerMetaInfo> handlerMetaInfoList = new ArrayList<>();
		final List<WsHandlerMetaInfo> wsHandlerMetaInfoList = new ArrayList<>();

		private static PathListener instance;

		public static PathListener getInstance() {
			return instance;
		}

		public PathListener() {
			instance = this;
		}

		@Override
		public void apply(@NotNull Javalin app) {
			app.events(it -> it.handlerAdded(handlerMetaInfoList::add));
			app.events(it -> it.wsHandlerAdded(wsHandlerMetaInfoList::add));
		}
	}
}
