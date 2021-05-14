package com.github.black0nion.blackonionbot.systems.news;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.mongodb.client.MongoCollection;

public class Newssystem {
	
	public static List<Newspost> posts = new ArrayList<>();
	
	public static void init() {
		Bot.executor.submit(() -> {
			final MongoCollection<Document> collection = MongoDB.generalDatabase.getCollection("News");
			List<Document> postsRaw = collection.find().into(new ArrayList<>());
			
			postsRaw.forEach(document -> {
				posts.add(new Newspost(document.getString("title"), document.getString("content").replace("#", ""), document.getDate("date")));
			});
		});
	}
}