package com.github.black0nion.blackonionbot.API.impl.get;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.GetRequest;
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

public class GetGuildsToManage extends GetRequest {

	@Override
	public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, @SuppressWarnings("NullableProblems") final @Nonnull BlackSession session) {
		final DiscordUser user = session.getUser();
		final JSONObject guildsObj = new JSONObject()
			.put("id", Long.parseLong(user.getUser().getId()))
			.put("name", user.getUser().getUsername())
			.put("discriminator", user.getUser().getDiscriminator())
			.put("locale", user.getUser().getLocale())
			.put("mfa", user.getUser().getMfaEnabled());
		
		try {
			final List<io.mokulu.discord.oauth.model.Guild> guildsResponse = user.getGuilds();
			if (guildsResponse == null) return exception("No guilds found", 500, response);
			final JSONArray guilds = new JSONArray();

			guildsResponse.forEach(obj -> {
				final JSONObject guildAsJson = new JSONObject();
				final Guild guild = Bot.jda.getGuildById(obj.getId());
				final long permissions = obj.getPermissions();
				if ((permissions & (1 << 3 | 1 << 5)) != 0) {
					guildAsJson.put("bot_in_guild", guild != null);
					guilds.put(guildAsJson);
				}
			});

			guildsObj.put("guilds", guilds);
			return guildsObj.toString();
		} catch (final Exception e) {
			e.printStackTrace();
			response.status(500);
			return exception(e);
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