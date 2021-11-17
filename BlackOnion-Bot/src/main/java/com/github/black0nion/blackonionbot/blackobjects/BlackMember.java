package com.github.black0nion.blackonionbot.blackobjects;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.Nullable;

public class BlackMember extends BlackObject implements Member {

    private static final MongoCollection<Document> configs = MongoDB.botDatabase.getCollection("membersettings");
    private static final MongoCollection<Document> warnsCollection = MongoDB.botDatabase.getCollection("warns");

    private static final LoadingCache<Member, BlackMember> members = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<Member, BlackMember>() {
	@Override
	public BlackMember load(final Member member) {
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

    @Deprecated
    /**
     * Deprecated as a warning
     *
     * @param guild
     * @return
     */
    @Reloadable("membercache")
    public static final void clearCache() {
	members.invalidateAll();
    }

    private final Member member;
    private final BlackGuild blackGuild;
    private final List<Warn> warns = new ArrayList<>();

    private BlackMember(@NotNull final Member member, final BlackGuild blackGuild) {
	this.member = member;
	this.blackGuild = blackGuild;

	this.save("username", member.getEffectiveName());
	this.save("guildname", blackGuild.getName());
	ImmutableList.copyOf(warnsCollection.find(this.getIdentifier())).stream().map(doc -> {
	    if (doc.containsKey("reason") && doc.containsKey("issuer") && doc.containsKey("date")) return new Warn(doc.getLong("guildid"), doc.getLong("issuer"), doc.getLong("userid"), doc.getLong("date"), doc.getString("reason"));
	    else if (doc.containsKey("issuer") && doc.containsKey("date")) return new Warn(doc.getLong("guildid"), doc.getLong("issuer"), doc.getLong("userid"), doc.getLong("date"));
	    return null;
	}).filter(warn -> warn != null).forEach(this.warns::add);
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
    MongoCollection<Document> getCollection() {
	return configs;
    }

	@Nullable
	@Override
	public String getAvatarUrl() {
		return this.member.getAvatarUrl();
	}

	@NotNull
	@Override
	public String getEffectiveAvatarUrl() {
		return this.member.getEffectiveAvatarUrl();
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(int delDays) {
		return this.member.ban(delDays);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(int delDays, @Nullable String reason) {
		return this.member.ban(delDays, reason);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> kick() {
		return this.member.kick();
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> kick(@Nullable String reason) {
		return this.member.kick(reason);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> mute(boolean mute) {
		return this.member.mute(mute);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> deafen(boolean deafen) {
		return this.member.deafen(deafen);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> modifyNickname(@Nullable String nickname) {
		return this.member.modifyNickname(nickname);
	}

	@Override
	public void formatTo(Formatter formatter, int flags, int width, int precision) {
		this.member.formatTo(formatter, flags, width, precision);
	}

	@Override
	public boolean hasAccess(@NotNull GuildChannel channel) {
		return this.member.hasAccess(channel);
	}

	@NotNull
	@Override
	public String getId() {
		return this.member.getId();
	}

	@NotNull
	@Override
	public OffsetDateTime getTimeCreated() {
		return this.member.getTimeCreated();
	}

	@NotNull
	@Override
	public User getUser() {
		return this.member.getUser();
	}

	public BlackUser getBlackUser() {
        return BlackUser.from(this.member.getUser());
    }

	@NotNull
	@Override
	public Guild getGuild() {
		return this.member.getGuild();
	}

	@NotNull
	@Override
	public JDA getJDA() {
		return this.member.getJDA();
	}

	@NotNull
	@Override
	public OffsetDateTime getTimeJoined() {
		return this.member.getTimeJoined();
	}

	@Override
	public boolean hasTimeJoined() {
		return this.member.hasTimeJoined();
	}

	@Nullable
	@Override
	public OffsetDateTime getTimeBoosted() {
		return this.member.getTimeBoosted();
	}

	@Nullable
	@Override
	public GuildVoiceState getVoiceState() {
		return this.member.getVoiceState();
	}

	@NotNull
	@Override
	public List<Activity> getActivities() {
		return this.member.getActivities();
	}

	@NotNull
	@Override
	public OnlineStatus getOnlineStatus() {
		return this.member.getOnlineStatus();
	}

	@NotNull
	@Override
	public OnlineStatus getOnlineStatus(@NotNull ClientType type) {
		return this.member.getOnlineStatus(type);
	}

	@NotNull
	@Override
	public EnumSet<ClientType> getActiveClients() {
		return this.member.getActiveClients();
	}

	@Nullable
	@Override
	public String getNickname() {
		return this.member.getNickname();
	}

	@NotNull
	@Override
	public String getEffectiveName() {
		return this.member.getEffectiveName();
	}

	@Nullable
	@Override
	public String getAvatarId() {
		return this.member.getAvatarId();
	}

	@NotNull
	@Override
	public List<Role> getRoles() {
		return this.member.getRoles();
	}

	@Nullable
	@Override
	public Color getColor() {
		return this.member.getColor();
	}

	@Override
	public int getColorRaw() {
		return this.member.getColorRaw();
	}

	@Override
	public boolean canInteract(@NotNull Member member) {
		return this.member.canInteract(member);
	}

	@Override
	public boolean canInteract(@NotNull Role role) {
		return this.member.canInteract(role);
	}

	@Override
	public boolean canInteract(@NotNull Emote emote) {
		return this.member.canInteract(emote);
	}

	@Override
	public boolean isOwner() {
		return this.member.isOwner();
	}

	@Override
	public boolean isPending() {
		return this.member.isPending();
	}

	@Nullable
	@Override
	public TextChannel getDefaultChannel() {
		return this.member.getDefaultChannel();
	}

	@NotNull
	@Override
	public String getAsMention() {
		return this.member.getAsMention();
	}

	@NotNull
	@Override
	public EnumSet<Permission> getPermissions() {
		return this.member.getPermissions();
	}

	@NotNull
	@Override
	public EnumSet<Permission> getPermissions(@NotNull GuildChannel channel) {
		return this.member.getPermissions(channel);
	}

	@NotNull
	@Override
	public EnumSet<Permission> getPermissionsExplicit() {
		return this.member.getPermissionsExplicit();
	}

	@NotNull
	@Override
	public EnumSet<Permission> getPermissionsExplicit(@NotNull GuildChannel channel) {
		return this.member.getPermissionsExplicit(channel);
	}

	@Override
	public boolean hasPermission(@NotNull Permission... permissions) {
		return this.member.hasPermission(permissions);
	}

	@Override
	public boolean hasPermission(@NotNull Collection<Permission> permissions) {
		return this.member.hasPermission(permissions);
	}

	@Override
	public boolean hasPermission(@NotNull GuildChannel channel, @NotNull Permission... permissions) {
		return this.member.hasPermission(channel, permissions);
	}

	@Override
	public boolean hasPermission(@NotNull GuildChannel channel, @NotNull Collection<Permission> permissions) {
		return this.member.hasPermission(channel, permissions);
	}

	@Override
	public boolean canSync(@NotNull GuildChannel targetChannel, @NotNull GuildChannel syncSource) {
		return this.member.canSync(targetChannel, syncSource);
	}

	@Override
	public boolean canSync(@NotNull GuildChannel channel) {
		return this.member.canSync(channel);
	}

	@Override
	public long getIdLong() {
		return this.member.getIdLong();
	}
}