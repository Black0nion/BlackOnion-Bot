package com.github.black0nion.blackonionbot.api.impl.get;

import com.github.black0nion.blackonionbot.api.routes.IGetRoute;
import com.github.black0nion.blackonionbot.api.sessions.RestSession;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import io.javalin.http.Context;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class GetGuildsToManage implements IGetRoute {

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable RestSession session, DiscordUser user) throws Exception {
		final JSONObject guildsObj = new JSONObject()
			.put("id", Long.parseLong(user.getUser().getId()))
			.put("name", user.getUser().getUsername())
			.put("discriminator", user.getUser().getDiscriminator())
			.put("locale", user.getUser().getLocale())
			.put("mfa", user.getUser().getMfaEnabled());

		final List<io.mokulu.discord.oauth.model.Guild> guildsResponse = user.getGuilds();
		if (guildsResponse == null) {
			throw new NullPointerException("Guilds response is null");
		}
		final JSONArray guilds = new JSONArray();

		guildsResponse.forEach(oauthGuild -> {
			final JSONObject guildAsJson = new JSONObject();
			final Guild guild = Bot.getInstance().getJda().getGuildById(oauthGuild.getId());
			final long permissions = oauthGuild.getPermissions();
			if ((permissions & (1 << 3 | 1 << 5)) != 0) {
				guildAsJson.put("bot_in_guild", guild != null);
				guilds.put(guildAsJson);
			}
		});

		guildsObj.put("guilds", guilds);
		return guildsObj;
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