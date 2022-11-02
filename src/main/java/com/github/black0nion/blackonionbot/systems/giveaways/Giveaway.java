package com.github.black0nion.blackonionbot.systems.giveaways;

import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.misc.SQLSetup;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record Giveaway(LocalDateTime endDate, long messageId, long channelId, long createrId, long guildId, String item, int winners) {

	/**
	 * In seconds since 1970-01-01T00:00:00Z because millis is too accurate
	 */
	public long endSeconds() {
		return endDate.toInstant(ZoneOffset.UTC).getEpochSecond();
	}

	@SQLSetup
	public static void setup() throws SQLException {
		SQLHelper.run("CREATE TABLE IF NOT EXISTS giveaways (" +
			"endDate BIGINT, " +
			"messageId BIGINT, " +
			"channelId BIGINT, " +
			"createrId BIGINT, " +
			"guildId BIGINT, " +
			"item VARCHAR(255), " +
			"winners INT)"
		);
	}

	@Override
	public String toString() {
		return "Giveaway [endDate=" + endDate + ", messageId=" + messageId + ", channelId=" + channelId + ", guildId="
			+ guildId + ", item=" + item + ", winners=" + winners + "]";
	}

	public void writeToDatabase() throws SQLException {
		try (SQLHelper sq = new SQLHelper(
					"INSERT INTO giveaways (endDate, messageId, channelId, createrId, guildId, item, winners) VALUES (?, ?, ?, ?, ?, ?, ?)",
					endSeconds(), messageId, channelId, createrId, guildId, item, winners);
				PreparedStatement ps = sq.create()) {
			ps.executeUpdate();
		}
	}

	public void deleteFromDatabase() throws SQLException {
		try (SQLHelper sq = new SQLHelper("DELETE FROM giveaways WHERE messageId = ?", messageId);
				PreparedStatement ps = sq.create()) {
			ps.executeUpdate();
		}
	}
}
