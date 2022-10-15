package com.github.black0nion.blackonionbot.systems.giveaways;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;

public class GiveawaySystem {

	private static final Logger logger = LoggerFactory.getLogger(GiveawaySystem.class);

	private GiveawaySystem() {}

	private static final List<Giveaway> giveaways = new ArrayList<>();

	private static final Collection<String> giveawayKeys = new ArrayList<>();
	private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1,
		new ThreadFactoryBuilder().setNameFormat("giveaway-scheduler-%d").build()
	);

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
		try {
			try (PreparedStatement ps = new SQLHelper("SELECT * FROM giveaways").create();
					ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					giveaways.add(new Giveaway(
							LocalDateTime.ofEpochSecond(rs.getLong("endDate"), 0, ZoneOffset.UTC),
							rs.getLong("messageId"),
							rs.getLong("channelId"),
							rs.getLong("createrId"),
							rs.getLong("guildId"),
							rs.getString("item"),
							rs.getInt("winners")
					));
				}
			}
		} catch (SQLException e) {
			logger.error("Error while initializing GiveawaySystem", e);
		}
	}

	@Nullable
	public static Giveaway getGiveaway(final long messageid) {
		return giveaways.stream().filter(giveaway -> giveaway.messageId() == messageid).findFirst().orElse(null);
	}

	public static void createGiveaway(final LocalDateTime endDate, final long messageId, final long channelId, final long createrId, final long guildId, final String item, final int winners) {
		try {
			final Giveaway giveaway = new Giveaway(endDate, messageId, channelId, createrId, guildId, item, winners);
			if (giveaways.contains(giveaway)) return;
			giveaways.add(giveaway);
			giveaway.writeToDatabase();
			scheduleGiveaway(giveaway);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static void scheduleGiveaway(final Giveaway giveaway) {
		final BlackGuild guild = BlackGuild.from(Bot.getInstance().getJDA().getGuildById(giveaway.guildId()));
		assert guild != null;
		Objects.requireNonNull(guild.getTextChannelById(giveaway.channelId())).retrieveMessageById(giveaway.messageId()).queue(msg -> {
			if (msg == null) {
				deleteGiveaway(giveaway);
				return;
			}

			scheduler.schedule(() -> endGiveaway(giveaway, msg, guild), giveaway.endSeconds() - System.currentTimeMillis() / 1000, TimeUnit.SECONDS);
		});
	}

	public static void endGiveaway(final Giveaway giveaway, final Message msg, final BlackGuild guild) {
		try {
			msg.retrieveReactionUsers(Emoji.fromUnicode("U+D83CU+DF89")).queue(users -> {
				final SelfUser selfUser = Bot.getInstance().getJDA().getSelfUser();
				if (users.isEmpty() || users.stream().noneMatch(user -> (user.getIdLong() != selfUser.getIdLong()))) {
					msg.editMessageEmbeds(EmbedUtils.getSuccessEmbed(null, guild).setTitle("GIVEAWAY").addField("nowinner", "nobodyparticipated", false).build()).queue();
					deleteGiveaway(giveaway);
					return;
				}

				users.remove(selfUser);
				final int winnerCountGiveawy = giveaway.winners();
				final int winnerCount = Math.min(winnerCountGiveawy, users.size());
				final String[] winners = new String[winnerCount];
				final long[] winnersIds = new long[winnerCount];

				Collections.shuffle(users, ThreadLocalRandom.current());

				for (int i = 0; i < winners.length; i++) {
					final User currentWinner = users.get(i);
					winners[i] = currentWinner.getAsMention();
					winnersIds[i] = currentWinner.getIdLong();
				}

				msg.editMessageEmbeds(EmbedUtils.getSuccessEmbed(null, guild).setTitle("GIVEAWAY").addField("Winner Winner Chicken Dinner :)", LanguageSystem.getTranslation("giveawaywinner", null, guild).replace("%winner%", String.join("\n", winners)), false).build()).mentionUsers(winnersIds).queue();
			});
		} catch (final Exception ex) {
			logger.error("Error while ending giveaway", ex);
		} finally {
			deleteGiveaway(giveaway);
		}
	}

	private static void deleteGiveaway(final Giveaway giveaway) {
		try {
			giveaways.remove(giveaway);
			giveaway.deleteFromDatabase();
		} catch (final Exception e) {
			logger.error("Error while deleting giveaway", e);
		}
	}
}
