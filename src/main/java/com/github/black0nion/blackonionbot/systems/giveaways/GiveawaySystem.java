package com.github.black0nion.blackonionbot.systems.giveaways;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

// TODO: refactor to DI & tests
public class GiveawaySystem {

	private static final Logger logger = LoggerFactory.getLogger(GiveawaySystem.class);

	private final SQLHelperFactory sql;

	public GiveawaySystem(SQLHelperFactory sql) {
		this.sql = sql;
	}

	private final List<Giveaway> giveaways = new ArrayList<>();

	private static int schedulerId = 1;
	private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1,
		new ThreadFactoryBuilder().setNameFormat("giveaway-scheduler-" + schedulerId++ + "-%d").build()
	);

	public void init() {
		try {
			try (SQLHelper sq = sql.create("SELECT * FROM giveaways");
					ResultSet rs = sq.executeQuery()) {
				while (rs.next()) {
					giveaways.add(new Giveaway(
						sql,
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
	public Giveaway getGiveaway(final long messageId) {
		return giveaways.stream().filter(giveaway -> giveaway.messageId() == messageId).findFirst().orElse(null);
	}

	public void createGiveaway(final LocalDateTime endDate, final long messageId, final long channelId, final long createrId, final long guildId, final String item, final int winners) {
		try {
			final Giveaway giveaway = new Giveaway(sql, endDate, messageId, channelId, createrId, guildId, item, winners);
			if (giveaways.contains(giveaway)) return;
			giveaways.add(giveaway);
			giveaway.writeToDatabase();
			scheduleGiveaway(giveaway);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void scheduleGiveaway(final Giveaway giveaway) {
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

	public void endGiveaway(final Giveaway giveaway, final Message msg, final BlackGuild guild) {
		try {
			msg.retrieveReactionUsers(Emoji.fromUnicode("U+D83CU+DF89")).queue(users -> {
				final SelfUser selfUser = Bot.getInstance().getJDA().getSelfUser();
				Language lang = LanguageSystem.getLanguage(null, guild);
				if (users.isEmpty() || users.stream().noneMatch(user -> (user.getIdLong() != selfUser.getIdLong()))) {
					msg.reply(lang.getTranslationNonNull("nowinner")).queue();
					updateGiveawayMessage(msg, lang);
					deleteGiveaway(giveaway);
					return;
				}

				users.remove(selfUser);
				final int winnerCountGiveaway = giveaway.winners();
				final int winnerCount = Math.min(winnerCountGiveaway, users.size());
				final String[] winners = new String[winnerCount];

				Collections.shuffle(users, ThreadLocalRandom.current());

				for (int i = 0; i < winners.length; i++) {
					final User currentWinner = users.get(i);
					winners[i] = "- " + currentWinner.getAsMention();
				}

				updateGiveawayMessage(msg, lang);
				msg.reply("GIVEAWAY\n" + lang.getTranslation("giveawaywinner", new Placeholder("winner", String.join("\n", winners)))).queue();
			});
		} catch (final Exception ex) {
			logger.error("Error while ending giveaway", ex);
		} finally {
			deleteGiveaway(giveaway);
		}
	}

	private void updateGiveawayMessage(Message msg, Language lang) {
		MessageEmbed embed = msg.getEmbeds().get(0);
		EmbedBuilder builder = new EmbedBuilder(embed);
		builder.setDescription(embed.getDescription() + "\n" + lang.getTranslation("giveawayended"));
		msg.editMessageEmbeds(builder.build()).queue();
	}

	private void deleteGiveaway(final Giveaway giveaway) {
		try {
			giveaways.remove(giveaway);
			giveaway.deleteFromDatabase();
		} catch (final Exception e) {
			logger.error("Error while deleting giveaway", e);
		}
	}
}
