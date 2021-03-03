package com.github.black0nion.blackonionbot.systems.giveaways;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

public class GiveawaysSystem {
	private static List<Giveaway> giveaways = new ArrayList<>();
	
	private static MongoCollection<Document> collection;
	
	private static final Collection<String> giveawayKeys = new ArrayList<>();
	
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private static Random random = new Random();
	
	static {
		giveawayKeys.add("endDate");
		giveawayKeys.add("messageId");
		giveawayKeys.add("channelId");
		giveawayKeys.add("guildId");
		giveawayKeys.add("item");
		giveawayKeys.add("winners");
	}
	
	public static void init() {
		collection = MongoDB.botDatabase.getCollection("giveaways");
		
		Bot.executor.submit(() -> {
			try {
				Bot.jda.awaitReady();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Document doc : collection.find()) {
				if (doc.keySet().containsAll(giveawayKeys))
					createGiveaway(doc.getDate("endDate"), doc.getLong("messageId"), doc.getLong("channelId"), doc.getLong("guildId"), doc.getString("item"), doc.getInteger("winners"));
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static Giveaway createGiveaway(Date endDate, long messageId, long channelId, long guildId, String item, int winners) {
		try { 
			Giveaway giveaway = new Giveaway(endDate, messageId, channelId, guildId, item, winners);
			if (giveaways.contains(giveaway))
				return giveaway;
			giveaways.add(giveaway);
			ObjectMapper mapper = new ObjectMapper();
			final HashMap<String, Object> values = mapper.readValue(mapper.writeValueAsString(giveaway), HashMap.class);
			values.remove("endDate");
			values.put("endDate", endDate);
			collection.insertOne(new Document(values));
			scheduleGiveaway(giveaway);
			return giveaway;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void scheduleGiveaway(Giveaway giveaway) {
		Date endDate = giveaway.getEndDate();
		Guild guild = Bot.jda.getGuildById(giveaway.getGuildId());
		Message msg = guild.getTextChannelById(giveaway.getChannelId()).retrieveMessageById(giveaway.getMessageId()).submit().join();
		final int winnas = giveaway.getWinners();
		if (msg == null) {
			deleteGiveaway(giveaway);
			return;
		}
		executor.schedule(() -> {
			try {
				final List<User> users = msg.retrieveReactionUsers("\uD83C\uDF89").submit().join();
				final SelfUser selfUser = Bot.jda.getSelfUser();
				if (users.size() == 0 || users.stream().filter(user -> {return user.getIdLong() != selfUser.getIdLong();}).count() == 0) {
					msg.editMessage(EmbedUtils.getSuccessEmbed(null, guild).setTitle("GIVEAWAY").addField("nowinner", "nobodyparticipated", false).build()).queue();
					deleteGiveaway(giveaway);
					return;
				}
				
				users.remove(selfUser);
				String[] winners = new String[winnas < users.size() ? winnas : users.size()];
				
				Collections.shuffle(users, random);
				
				for (int i = 0; i < winners.length; i++) {
					winners[i] = users.get(i).getAsMention();
				}
				
				msg.editMessage(EmbedUtils.getSuccessEmbed(null, guild).setTitle("GIVEAWAY").addField("Winner Winner Chicken Dinner :)", LanguageSystem.getTranslatedString("giveawaywinner", null, guild).replace("%winner%", String.join("\n", winners)), false).build()).queue();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			deleteGiveaway(giveaway);
		}, endDate.getTime() - Calendar.getInstance().getTime().getTime(), TimeUnit.MILLISECONDS);
	}
	
	private static final void deleteGiveaway(Giveaway giveaway) {
		try {
			giveaways.remove(giveaway);
		try { collection.deleteOne(new BasicDBObject().append("messageId", giveaway.getMessageId()).append("guildId", giveaway.getGuildId()).append("channelId", giveaway.getChannelId())); } catch (Exception ex) { ex.printStackTrace(); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
