package com.github.black0nion.blackonionbot.systems.giveaways;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

public class GiveawaySystem {
	private static List<Giveaway> giveaways = new ArrayList<>();
	
	private static MongoCollection<Document> collection;
	
	private static final Collection<String> giveawayKeys = new ArrayList<>();
	
	static {
		giveawayKeys.add("endDate");
		giveawayKeys.add("messageId");
		giveawayKeys.add("channelId");
		giveawayKeys.add("createrId");
		giveawayKeys.add("guildId");
		giveawayKeys.add("item");
		giveawayKeys.add("winners");
	}
	
	public static void init() {
		collection = MongoDB.botDatabase.getCollection("giveaways");
		
		Bot.executor.submit(() -> {
			try {
				Bot.jda.awaitReady();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			
			for (final Document doc : collection.find())
				if (doc.keySet().containsAll(giveawayKeys))
					createGiveaway(doc.getDate("endDate"), doc.getLong("messageId"), doc.getLong("channelId"), doc.getLong("createrId"), doc.getLong("guildId"), doc.getString("item"), doc.getInteger("winners"));
		});
	}
	
	@Nullable
	public static Giveaway getGiveaway(final long messageid) {
		return giveaways.stream().filter(giveaway -> giveaway.getMessageId() == messageid).findFirst().orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	public static Giveaway createGiveaway(final Date endDate, final long messageId, final long channelId, final long createrId, final long guildId, final String item, final int winners) {
		try { 
			final Giveaway giveaway = new Giveaway(endDate, messageId, channelId, createrId, guildId, item, winners);
			if (giveaways.contains(giveaway))
				return giveaway;
			giveaways.add(giveaway);
			final ObjectMapper mapper = new ObjectMapper();
			final HashMap<String, Object> values = mapper.readValue(mapper.writeValueAsString(giveaway), HashMap.class);
			values.remove("endDate");
			values.put("endDate", endDate);
			collection.insertOne(new Document(values));
			scheduleGiveaway(giveaway);
			return giveaway;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void scheduleGiveaway(final Giveaway giveaway) {
		final Date endDate = giveaway.getEndDate();
		final BlackGuild guild = BlackGuild.from(Bot.jda.getGuildById(giveaway.getGuildId()));
		guild.getTextChannelById(giveaway.getChannelId()).retrieveMessageById(giveaway.getMessageId()).queue(msg -> {
			if (msg == null) {
				deleteGiveaway(giveaway);
				return;
			}
			
			Bot.scheduledExecutor.schedule(() -> {
				endGiveaway(giveaway, msg, guild);
			}, endDate.getTime() - Calendar.getInstance().getTime().getTime(), TimeUnit.MILLISECONDS);
		});
	}
	
	public static final void endGiveaway(final Giveaway giveaway, final Message msg, final BlackGuild guild) {
		try {
			msg.retrieveReactionUsers("\uD83C\uDF89").queue(users -> {
				final SelfUser selfUser = Bot.jda.getSelfUser();
				if (users.size() == 0 || users.stream().filter(user -> {return user.getIdLong() != selfUser.getIdLong();}).count() == 0) {
					msg.editMessage(EmbedUtils.getSuccessEmbed(null, guild).setTitle("GIVEAWAY").addField("nowinner", "nobodyparticipated", false).build()).queue();
					deleteGiveaway(giveaway);
					return;
				}
				
				users.remove(selfUser);
				final int winnerCountGiveawy = giveaway.getWinners();
				final int winnerCount = winnerCountGiveawy < users.size() ? winnerCountGiveawy : users.size();
				final String[] winners = new String[winnerCount];
				final long[] winnersIds = new long[winnerCount];
				
				Collections.shuffle(users, Bot.random);
				
				for (int i = 0; i < winners.length; i++) {
					final User currentWinner = users.get(i);
					winners[i] = currentWinner.getAsMention();
					winnersIds[i] = currentWinner.getIdLong(); 
				}
				
				msg.editMessage(EmbedUtils.getSuccessEmbed(null, guild).setTitle("GIVEAWAY").addField("Winner Winner Chicken Dinner :)", LanguageSystem.getTranslation("giveawaywinner", null, guild).replace("%winner%", String.join("\n", winners)), false).build()).mentionUsers(winnersIds).queue();
			});
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		deleteGiveaway(giveaway);
	}
	
	private static final void deleteGiveaway(final Giveaway giveaway) {
		try {
			giveaways.remove(giveaway);
		try { collection.deleteOne(new BasicDBObject().append("messageId", giveaway.getMessageId()).append("guildId", giveaway.getGuildId()).append("channelId", giveaway.getChannelId())); } catch (final Exception ex) { ex.printStackTrace(); }
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
