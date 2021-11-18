package com.github.black0nion.blackonionbot.API.impl.get;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.GetRequest;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import net.dv8tion.jda.api.entities.Guild;
import spark.Request;
import spark.Response;

public class GetGuildsToManage extends GetRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession session) {
	final DiscordUser user = session.getUser();
	final JSONObject guildsObj = new JSONObject().put("id", user.getUserId()).put("name", user.getUserName()).put("discriminator", user.getDiscriminator()).put("locale", user.getLocale()).put("mfa", user.isMfaEnabled());

	final JSONArray guildsResponse = user.getGuilds();
	final JSONArray guilds = new JSONArray();

	guildsResponse.forEach(obj -> {
	    final JSONObject guildAsJson = (JSONObject) obj;
	    final Guild guild = Bot.jda.getGuildById(guildAsJson.getString("id"));
	    final long permissions = guildAsJson.getLong("permissions");
	    if ((permissions & (1 << 3 | 1 << 5)) != 0) {
		if (guild != null) {
		    guildAsJson.put("bot_in_guild", true);
		} else {
		    guildAsJson.put("bot_in_guild", false);
		}
		guilds.put(guildAsJson);
	    }
	});

	guildsObj.put("guilds", guilds);
	return guildsObj.toString();
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