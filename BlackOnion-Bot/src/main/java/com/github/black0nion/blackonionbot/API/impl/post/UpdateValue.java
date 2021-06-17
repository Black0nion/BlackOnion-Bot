package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;

import net.dv8tion.jda.api.entities.Guild;
import spark.Request;
import spark.Response;

public class UpdateValue extends PostRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession user) {
	final String newValue = headers.get("newValue");
	final String databaseKey = headers.get("databaseKey");
	final String guildId = headers.get("guild");
	Guild guild;
	try {
	    guild = Bot.jda.getGuildById(guildId);
	} catch (final Exception e) {
	    return "bing bong request gone";
	}
	final DashboardValue value = Dashboard.getDashboardValueFromKey(databaseKey);
	if (value != null) {
	    value.save(databaseKey, newValue, BlackGuild.from(guild));
	}
	return "ding dong done";
    }

    @Override
    public String url() {
	return "updatevalue";
    }

    @Override
    public String[] requiredParameters() {
	return new String[] { "databaseKey", "newValue" };
    }

    @Override
    public boolean requiresLogin() {
	return false;
    }
}