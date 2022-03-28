package com.github.black0nion.blackonionbot.systems.news;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.mongodb.client.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Newssystem {

	private static final MongoCollection<Document> collection = MongoDB.generalDatabase.getCollection("News");
	public static final List<Newspost> posts = new ArrayList<>();

	private static final Logger logger = LoggerFactory.getLogger(Newssystem.class);
	public static void init() {
		reload();
		Bot.executor.submit(() -> {
			//noinspection InfiniteLoopStatement
			while (true) {
				// will block until a change is detected
				collection.watch().first();
				logger.info("Reloading Newsposts...");
				reload();
			}
		});
	}

	@Reloadable("newssystem")
	public static void reload() {
		Bot.executor.submit(() -> {
			posts.clear();
			final List<Document> postsRaw = collection.find().into(new ArrayList<>());
			postsRaw.forEach(document -> posts.add(new Newspost(document.getString("title"), document.getString("content").replace("#", ""), document.getDate("date"))));
		});
	}
}