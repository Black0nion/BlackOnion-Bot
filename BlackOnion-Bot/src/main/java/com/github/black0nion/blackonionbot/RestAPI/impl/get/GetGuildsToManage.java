package com.github.black0nion.blackonionbot.RestAPI.impl.get;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.GetRequest;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotSecrets;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

public class GetGuildsToManage implements GetRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		if (request.headers("token") == null) {
			response.status(401);
			return new JSONObject().put("success", false).toString();
		}
		
		JSONObject userInfo = Utils.getUserInfoFromToken(request.headers("token"));
		
		List<Guild> guildsToManage = new ArrayList<>();
		
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> resp = Unirest.get("https://discord.com/api/users/@me/guilds")
			  .header("Authorization", "Bot " + BotSecrets.bot_token)
			  .asString();
			
			JSONArray jsonResponse = new JSONArray(resp.getBody());
			
			for (int i = 0; i < jsonResponse.length(); i++) {
				JSONObject obj = jsonResponse.getJSONObject(i);
				System.out.println(obj + "\n\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Bot.jda.getMutualGuilds(User.fromId(userInfo.getString("id"))).forEach(entry -> {
			if (entry.getMemberById(userInfo.getString("id")) != null && entry.getMemberById(userInfo.getString("id")).getPermissions().contains(Permission.ADMINISTRATOR)) guildsToManage.add(entry);
		});
		
		System.out.println(guildsToManage);
		
		return new JSONObject().toString();
	}

	@Override
	public String url() {
		return "getguildstomanage";
	}
	
	@Override
	public String[] requiredParameters() {
		return new String[0];
	}

}
