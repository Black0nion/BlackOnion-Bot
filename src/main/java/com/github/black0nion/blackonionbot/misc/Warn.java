package com.github.black0nion.blackonionbot.misc;

import com.github.black0nion.blackonionbot.database.PostgresConnection;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.utils.Utils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ID is the millis SINCE 2020/1/1 00:00:00 because discord<br>
 * Calculate the date: <code>{@link Warn#START_TIME_STAMP} + {@link Warn#id}</code>
 */
public record Warn(
	long issuer,
	long userid,
	long guildid,
	long id,
	String reason
) {
	public static final long START_TIME_STAMP = 1577833200000L; // 2020/1/1 00:00:00

	private static final String ISSUER_STR = "issuer";
	private static final String USERID_STR = "userid";
	private static final String GUILDID_STR = "guildid";
	private static final String ID_STR = "id";
	private static final String REASON_STR = "reason";

	public Warn(long issuer, long user, long guild, long date) {
		this(issuer, user, guild, date, null);
	}

	@Nullable
	public String getReasonEscaped() {
		return reason != null ? Utils.escapeMarkdown(reason) : null;
	}

	@SQLSetup
	public static void setupDatabase() {
		try (PreparedStatement statement = PostgresConnection.getConnection().prepareStatement(
			"CREATE TABLE IF NOT EXISTS warns (" +
				"issuer BIGINT NOT NULL," +
				"userid BIGINT NOT NULL," +
				"guildid BIGINT NOT NULL," +
				"id BIGINT NOT NULL," +
				"reason TEXT" +
				");"
		)) {
			statement.execute();
		} catch (SQLException e) {
			LoggerFactory.getLogger(Warn.class).error("Error while creating table 'warns'", e);
		}
	}

	/**
	 * @param entity either "guild" or "user"
	 * @param entityID the ID of the entity
	 */
	public static List<Warn> loadWarns(String entity, long entityID) {
		List<Warn> warns = new ArrayList<>();
		try (PreparedStatement ps = new SQLHelper("SELECT * FROM warns WHERE " + entity + "id = ?")
				.addParameter(entityID)
				.create();
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Warn warn = new Warn(
					rs.getLong(ISSUER_STR),
					rs.getLong(USERID_STR),
					rs.getLong(GUILDID_STR),
					rs.getLong(ID_STR),
					rs.getString(REASON_STR)
				);
				warns.add(warn);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(Warn.class).error("Error while loading warns", e);
		}
		return warns;
	}

	public static void saveWarns(List<Warn> warns, long guildID) {
		try {
			try (PreparedStatement ps = new SQLHelper("SELECT * FROM warns WHERE guildid = ?").addParameter(guildID).create();
				 ResultSet rs = ps.executeQuery()) {
				while (!rs.isClosed() && rs.next()) {
					Warn warn = new Warn(
						rs.getLong(ISSUER_STR),
						rs.getLong(USERID_STR),
						rs.getLong(GUILDID_STR),
						rs.getLong(ID_STR),
						rs.getString(REASON_STR)
					);
					if (!warns.contains(warn)) {
						new SQLHelper("DELETE FROM warns WHERE guildid = ? AND userid = ? AND id = ?")
							.addParameter(guildID)
							.addParameter(warn.userid())
							.addParameter(warn.id())
							.execute();
					} else {
						warns.remove(warn);
					}
				}
				if (!warns.isEmpty()) {
					try (PreparedStatement ps2 = PostgresConnection.getConnection().prepareStatement("INSERT INTO warns VALUES (?, ?, ?, ?, ?)")) {
						for (Warn warn : warns) {
							ps2.setLong(1, warn.issuer());
							ps2.setLong(2, warn.userid());
							ps2.setLong(3, warn.guildid());
							ps2.setLong(4, warn.id());
							ps2.setString(5, warn.reason());
							ps2.addBatch();
						}
						ps2.executeBatch();
					}
				}
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(Warn.class).error("Error while saving warns", e);
		}
	}
}
