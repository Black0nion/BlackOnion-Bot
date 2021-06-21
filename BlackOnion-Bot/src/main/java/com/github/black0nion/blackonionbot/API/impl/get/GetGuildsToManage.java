package com.github.black0nion.blackonionbot.API.impl.get;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.GetRequest;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

public class GetGuildsToManage extends GetRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession session) {
	final DiscordUser user = session.getUser();
	final JSONObject guildsObj = new JSONObject(session.getUser());
	final JSONArray guilds = new JSONArray();
	final long uid = user.getUserId();
	Bot.jda.getMutualGuilds(User.fromId(uid)).forEach(entry -> {
	    final Member mem = entry.getMemberById(uid);
	    if (mem != null && mem.getPermissions().contains(Permission.ADMINISTRATOR)) {
		final String iconUrl = entry.getIconUrl();
		final JSONObject append = new JSONObject().put("name", entry.getName()).put("icon", iconUrl != null ? iconUrl : "none");
		guilds.put(append);
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