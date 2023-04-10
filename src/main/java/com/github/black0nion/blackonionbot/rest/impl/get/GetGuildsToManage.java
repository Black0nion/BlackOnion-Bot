package com.github.black0nion.blackonionbot.rest.impl.get;

import com.github.black0nion.blackonionbot.oauth.OAuthUser;
import com.github.black0nion.blackonionbot.rest.api.IGetRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.Context;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.List;

public class GetGuildsToManage implements IGetRoute {

	private final JDA jda;

	public GetGuildsToManage(JDA jda) {
		this.jda = jda;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, OAuthUser user) throws Exception {
		final List<io.mokulu.discord.oauth.model.Guild> guildsResponse = user.getGuilds();
		if (guildsResponse == null) {
			throw new NullPointerException("Guilds response is null");
		}
		final JSONArray guilds = new JSONArray();

		guildsResponse.forEach(oauthGuild -> {
			final JSONObject guildAsJson = new JSONObject()
				.put("id", oauthGuild.getId())
				.put("name", oauthGuild.getName())
				.put("icon", oauthGuild.getIcon());

			final long permissions = oauthGuild.getPermissions();
			if ((permissions & (1 << 3 | 1 << 5)) != 0) {
				final Guild guild = jda.getGuildById(oauthGuild.getId());

				if (guild != null)
					guildAsJson.put("banner", guild.getBannerUrl());

				guildAsJson.put("bot_in_guild", guild != null);

				guilds.put(guildAsJson);
			}
		});

		return guilds;
	}

	@Override
	public @Nonnull String url() {
		return "guilds";
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}
}