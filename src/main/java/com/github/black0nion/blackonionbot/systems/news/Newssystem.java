package com.github.black0nion.blackonionbot.systems.news;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.mongodb.client.MongoCollection;

public class Newssystem {

    public static List<Newspost> posts = new ArrayList<>();

    private static final MongoCollection<Document> collection = MongoDB.generalDatabase.getCollection("News");

    public static void init() {
	reload();
	Bot.executor.submit(() -> {
	    while (true) {
		collection.watch().first();
		Logger.logInfo("Reloading NewsPosts...");
		reload();
	    }
	});
    }

    @Reloadable("newssystem")
    public static void reload() {
	Bot.executor.submit(() -> {
	    posts.clear();
	    final List<Document> postsRaw = collection.find().into(new ArrayList<>());
	    postsRaw.forEach(document -> {
		posts.add(new Newspost(document.getString("title"), document.getString("content").replace("#", ""), document.getDate("date")));
	    });
	});
    }
}