package com.github.black0nion.blackonionbot.misc;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * ID is the millis SINCE 2020/1/1 00:00:00 because discord
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

	private static final String ISSUER = "issuer";
	private static final String USERID = "userid";
	private static final String GUILDID = "guildid";
	private static final String ID = "id";
	private static final String REASON = "reason";

	public Warn(long issuer, long user, long guild, long date) {
		this(issuer, user, guild, date, null);
	}

	public Document toDocument() {
		return new Document(ISSUER, issuer)
				.append(USERID, userid)
				.append(GUILDID, guildid)
				.append(ID, id)
				.append(REASON, reason);
	}

	@Nullable
	public String getReasonEscaped() {
		return reason != null ? Utils.escapeMarkdown(reason) : null;
	}

	private static final MongoCollection<Document> warnsCollection = MongoDB.getInstance().getDatabase().getCollection("warns");
	public static void loadWarns(List<Warn> warns, Document identifier) {
		ImmutableList.copyOf(warnsCollection.find(identifier)).stream().filter(Objects::nonNull).map(doc -> {
			if (doc.containsKey(ISSUER) && doc.containsKey(USERID) && doc.containsKey(GUILDID) && doc.containsKey(ID))
				return new Warn(doc.getLong(ISSUER), doc.getLong(USERID), doc.getLong(GUILDID), doc.getLong(ID), doc.getString(REASON));
			LoggerFactory.getLogger(Warn.class).warn("Invalid warn document: {}", doc);
			return null;
		}).filter(Objects::nonNull).forEach(warns::add);
	}

	public static void saveWarns(List<Warn> warns, Document identifier) {
		warnsCollection.deleteMany(identifier);
		warns.forEach(warn -> warnsCollection.insertOne(warn.toDocument()));
	}
}