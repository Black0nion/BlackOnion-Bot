package com.github.black0nion.blackonionbot.blackobjects;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.entities.*;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlackMember extends BlackObject implements Member {

	private static final MongoCollection<Document> configs = MongoDB.DATABASE.getCollection("membersettings");

	@Override
	MongoCollection<Document> getCollection() {
		return configs;
	}

	private static final MongoCollection<Document> warnsCollection = MongoDB.DATABASE.getCollection("warns");

	private static final LoadingCache<Member, BlackMember> members = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Nonnull
		@Override
		public BlackMember load(@Nonnull final Member member) {
			final BlackGuild from = BlackGuild.from(member.getGuild());
			return new BlackMember(member, from);
		}
	});

	public static BlackMember from(final Member member) {
		try {
			return members.get(member);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BlackMember from(final Member member, final BlackGuild guild) {
		try {
			return members.get(member, () -> new BlackMember(member, guild));
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<BlackMember> from(@NotNull final List<Member> members) {
		return members.stream().map(BlackMember::from).collect(Collectors.toList());
	}

	@Reloadable("membercache")
	public static void clearCache() {
		members.invalidateAll();
	}

	private final Member member;
	private final BlackGuild blackGuild;
	private final List<Warn> warns = new ArrayList<>();

	private BlackMember(@NotNull final Member member, final BlackGuild blackGuild) {
		this.member = member;
		this.blackGuild = blackGuild;

		this.save("username", this.member.getEffectiveName());
		this.save("guildname", blackGuild.getName());
		ImmutableList.copyOf(warnsCollection.find(this.getIdentifier())).stream().map(doc -> {
			if (doc.containsKey("reason") && doc.containsKey("issuer") && doc.containsKey("date"))
				return new Warn(doc.getLong("guildid"), doc.getLong("issuer"), doc.getLong("userid"), doc.getLong("date"), doc.getString("reason"));
			else if (doc.containsKey("issuer") && doc.containsKey("date"))
				return new Warn(doc.getLong("guildid"), doc.getLong("issuer"), doc.getLong("userid"), doc.getLong("date"));
			return null;
		}).filter(Objects::nonNull).forEach(this.warns::add);
	 	this.blackUser = BlackUser.from(this.member.getUser());
	}

	private final BlackUser blackUser;
	public BlackUser getBlackUser() {
		return blackUser;
	}

	public Member getMember() {
		return this.member;
	}

	public void warn(final Warn w) {
		this.warns.add(w);
		final Document doc = new Document();
		doc.putAll(this.getIdentifier());
		doc.put("issuer", w.getIssuer());
		final long l = w.getDate();
		doc.put("date", l);
		if (w.getReasonNullable() != null) {
			doc.put("reason", w.getReason());
		}
		warnsCollection.insertOne(doc);
	}

	public void deleteWarn(final Warn w) {
		this.warns.remove(w);
		warnsCollection.deleteOne(this.getIdentifier().append("date", w.getDate()));
	}

	// TODO: check why it's never used
	public void saveWarns() {
		warnsCollection.insertMany(this.warns.stream().map(warn -> {
			final Document doc = new Document();
			doc.put("issuer", warn.getIssuer());
			doc.putAll(this.getIdentifier());
			if (warn.getReasonNullable() != null) {
				doc.put("reason", warn.getReason());
			}
			return doc;
		}).collect(Collectors.toList()));
	}

	/**
	 * @return the warns
	 */
	public List<Warn> getWarns() {
		return this.warns;
	}

	// override methods
	@Override
	public Document getIdentifier() {
		return new Document().append("guildid", this.blackGuild.getIdLong()).append("userid", this.member.getIdLong());
	}

	@Override
	@Nonnull
	public User getUser() {
		return this.member.getUser();
	}

	@Override
	@Nonnull
	public Guild getGuild() {
		return this.member.getGuild();
	}

	@Override
	@Nonnull
	public JDA getJDA() {
		return this.member.getJDA();
	}

	@Override
	@Nonnull
	public OffsetDateTime getTimeJoined() {
		return this.member.getTimeJoined();
	}

	@Override
	public boolean hasTimeJoined() {
		return this.member.hasTimeJoined();
	}

	@Override
	@Nullable
	public OffsetDateTime getTimeBoosted() {
		return this.member.getTimeBoosted();
	}

	@Override
	public boolean isBoosting() {
		return this.member.isBoosting();
	}

	@Override
	@Nullable
	public OffsetDateTime getTimeOutEnd() {
		return this.member.getTimeOutEnd();
	}

	@Override
	public boolean isTimedOut() {
		return this.member.isTimedOut();
	}

	@Override
	@Nullable
	public GuildVoiceState getVoiceState() {
		return this.member.getVoiceState();
	}

	@Override
	@Nonnull
	public List<Activity> getActivities() {
		return this.member.getActivities();
	}

	@Override
	@Nonnull
	public OnlineStatus getOnlineStatus() {
		return this.member.getOnlineStatus();
	}

	@Override
	@Nonnull
	public OnlineStatus getOnlineStatus(@Nonnull ClientType type) {
		return this.member.getOnlineStatus(type);
	}

	@Override
	@Nonnull
	public EnumSet<ClientType> getActiveClients() {
		return this.member.getActiveClients();
	}

	@Override
	@Nullable
	public String getNickname() {
		return this.member.getNickname();
	}

	@Override
	@Nonnull
	public String getEffectiveName() {
		return this.member.getEffectiveName();
	}

	@Override
	@Nullable
	public String getAvatarId() {
		return this.member.getAvatarId();
	}

	@Override
	@Nullable
	public String getAvatarUrl() {
		return this.member.getAvatarUrl();
	}

	@Override
	@Nonnull
	public String getEffectiveAvatarUrl() {
		return this.member.getEffectiveAvatarUrl();
	}

	@Override
	@Nonnull
	public List<Role> getRoles() {
		return this.member.getRoles();
	}

	@Override
	@Nullable
	public Color getColor() {
		return this.member.getColor();
	}

	@Override
	public int getColorRaw() {
		return this.member.getColorRaw();
	}

	@Override
	public boolean canInteract(@Nonnull Member member) {
		return this.member.canInteract(member);
	}

	@Override
	public boolean canInteract(@Nonnull Role role) {
		return this.member.canInteract(role);
	}

	@Override
	public boolean canInteract(@Nonnull Emote emote) {
		return this.member.canInteract(emote);
	}

	@Override
	public boolean isOwner() {
		return this.member.isOwner();
	}

	@Override
	@Incubating
	public boolean isPending() {
		return this.member.isPending();
	}

	@Override
	@Nullable
	public BaseGuildMessageChannel getDefaultChannel() {
		return this.member.getDefaultChannel();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(int delDays) {
		return this.member.ban(delDays);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(int delDays, @Nullable String reason) {
		return this.member.ban(delDays, reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick() {
		return this.member.kick();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick(@Nullable String reason) {
		return this.member.kick(reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(long amount, @Nonnull TimeUnit unit) {
		return this.member.timeoutFor(amount, unit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(@Nonnull Duration duration) {
		return this.member.timeoutFor(duration);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutUntil(@Nullable TemporalAccessor temporal) {
		return this.member.timeoutUntil(temporal);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> removeTimeout() {
		return this.member.removeTimeout();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> mute(boolean mute) {
		return this.member.mute(mute);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> deafen(boolean deafen) {
		return this.member.deafen(deafen);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyNickname(@Nullable String nickname) {
		return this.member.modifyNickname(nickname);
	}

	@Override
	@Nonnull
	public String getAsMention() {
		return this.member.getAsMention();
	}

	@Override
	public void formatTo(Formatter formatter, int flags, int width, int precision) {
		this.member.formatTo(formatter, flags, width, precision);
	}

	@Override
	@Nonnull
	public String getId() {
		return this.member.getId();
	}

	@Override
	public long getIdLong() {
		return this.member.getIdLong();
	}

	@Override
	@Nonnull
	public OffsetDateTime getTimeCreated() {
		return this.member.getTimeCreated();
	}

	@Override
	@Nonnull
	public EnumSet<Permission> getPermissions() {
		return this.member.getPermissions();
	}

	@Override
	@Nonnull
	public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
		return this.member.getPermissions(channel);
	}

	@Override
	@Nonnull
	public EnumSet<Permission> getPermissionsExplicit() {
		return this.member.getPermissionsExplicit();
	}

	@Override
	@Nonnull
	public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
		return this.member.getPermissionsExplicit(channel);
	}

	@Override
	public boolean hasPermission(@Nonnull Permission... permissions) {
		return this.member.hasPermission(permissions);
	}

	@Override
	public boolean hasPermission(@Nonnull Collection<Permission> permissions) {
		return this.member.hasPermission(permissions);
	}

	@Override
	public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
		return this.member.hasPermission(channel, permissions);
	}

	@Override
	public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions) {
		return this.member.hasPermission(channel, permissions);
	}

	@Override
	public boolean hasAccess(@Nonnull GuildChannel channel) {
		return this.member.hasAccess(channel);
	}

	@Override
	public boolean canSync(@Nonnull IPermissionContainer targetChannel, @Nonnull IPermissionContainer syncSource) {
		return this.member.canSync(targetChannel, syncSource);
	}

	@Override
	public boolean canSync(@Nonnull IPermissionContainer channel) {
		return this.member.canSync(channel);
	}
}