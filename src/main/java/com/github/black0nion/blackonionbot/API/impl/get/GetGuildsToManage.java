package com.github.black0nion.blackonionbot.api.impl.get;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IGetRoute;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

import static com.github.black0nion.blackonionbot.api.API.*;

public class GetGuildsToManage implements IGetRoute {

	@Override
	public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, @SuppressWarnings("NullableProblems") final @Nonnull BlackSession session, DiscordUser user) {
		final JSONObject guildsObj = new JSONObject()
			.put("id", Long.parseLong(user.getUser().getId()))
			.put("name", user.getUser().getUsername())
			.put("discriminator", user.getUser().getDiscriminator())
			.put("locale", user.getUser().getLocale())
			.put("mfa", user.getUser().getMfaEnabled());

		try {
			final List<io.mokulu.discord.oauth.model.Guild> guildsResponse = user.getGuilds();
			if (guildsResponse == null) return exception("No guilds found", response);
			final JSONArray guilds = new JSONArray();

			guildsResponse.forEach(oauthGuild -> {
				final JSONObject guildAsJson = new JSONObject();
				final Guild guild = Bot.jda.getGuildById(oauthGuild.getId());
				final long permissions = oauthGuild.getPermissions();
				if ((permissions & (1 << 3 | 1 << 5)) != 0) {
					guildAsJson.put("bot_in_guild", guild != null);
					guilds.put(guildAsJson);
				}
			});

			guildsObj.put("guilds", guilds);
			return guildsObj.toString();
		} catch (final Exception e) {
			e.printStackTrace();
			return exception(e, response);
		}
	}

	@Override
	public String url() {
		return "guilds";
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}
}