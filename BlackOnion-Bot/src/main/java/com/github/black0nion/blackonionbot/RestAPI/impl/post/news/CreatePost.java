package com.github.black0nion.blackonionbot.RestAPI.impl.post.news;

import org.bson.Document;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.PostRequest;
import com.github.black0nion.blackonionbot.bot.BotSecrets;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

public class CreatePost implements PostRequest {
	
	MongoCollection<Document> collection = MongoDB.newsDatabase.getCollection("posts");

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		if (user == null) {
			response.status(401);
			return new JSONObject().put("success", false).put("reason", 401).toString();
		} else if (!BotSecrets.isAdmin(user.getUserId())) {
			response.status(403);
			return new JSONObject().put("success", false).put("reason", 403).toString();
		}
		
		Document post = new Document();
		post.put("userid", user.getUserId());
		post.put("useravatar", "https://cdn.discordapp.com/avatars/" + user.getUserId() + "/" + user.getAvatar());
		post.put("title", body.getString("title"));
		post.put("text", body.getString("text"));
		
		MongoManager.insertOne(collection, post);
		
		// the request shouldn't contain that crap
		post.remove("_id");
		
		return new JSONObject(post).put("success", true).toString();
	}

	@Override
	public String url() {
		return "createpost";
	}
	
	@Override
	public String[] requiredParameters() {
		return new String[] { "text", "title" };
	}
	
}
