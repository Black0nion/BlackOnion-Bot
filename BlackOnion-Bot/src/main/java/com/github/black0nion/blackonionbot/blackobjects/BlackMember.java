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

    // built in methods
    @Override
    @NotNull
    @Deprecated
    public User getUser() {
	return this.member.getUser();
    }

    public BlackUser getBlackUser() {
	return BlackUser.from(this.member);
    }

    @Override
    @NotNull
    @Deprecated
    public Guild getGuild() {
	return this.member.getGuild();
    }

    @NotNull
    public BlackGuild getBlackGuild() {
	return this.blackGuild;
    }

    @Override
    @NotNull
    public EnumSet<Permission> getPermissions() {
	return this.member.getPermissions();
    }

    @Override
    @NotNull
    public EnumSet<Permission> getPermissions(@NotNull final GuildChannel guildChannel) {
	return this.member.getPermissions(guildChannel);
    }

    @Override
    @NotNull
    public EnumSet<Permission> getPermissionsExplicit() {
	return this.member.getPermissionsExplicit();
    }

    @Override
    @NotNull
    public EnumSet<Permission> getPermissionsExplicit(@NotNull final GuildChannel guildChannel) {
	return this.member.getPermissionsExplicit(guildChannel);
    }

    @Override
    public boolean hasPermission(@NotNull final Permission... permissions) {
	return this.member.hasPermission(permissions);
    }

    @Override
    public boolean hasPermission(@NotNull final Collection<Permission> collection) {
	return this.member.hasPermission(collection);
    }

    @Override
    public boolean hasPermission(@NotNull final GuildChannel guildChannel, @NotNull final Permission... permissions) {
	return this.member.hasPermission(guildChannel, permissions);
    }

    @Override
    public boolean hasPermission(@NotNull final GuildChannel guildChannel, @NotNull final Collection<Permission> collection) {
	return this.member.hasPermission(guildChannel, collection);
    }

    @Override
    @NotNull
    public JDA getJDA() {
	return this.member.getJDA();
    }

    @Override
    @NotNull
    public OffsetDateTime getTimeJoined() {
	return this.member.getTimeJoined();
    }

    @Override
    public boolean hasTimeJoined() {
	return this.member.hasTimeJoined();
    }

    @Override
    public OffsetDateTime getTimeBoosted() {
	return this.member.getTimeBoosted();
    }

    @Override
    public GuildVoiceState getVoiceState() {
	return this.member.getVoiceState();
    }

    @Override
    @NotNull
    public List<Activity> getActivities() {
	return this.member.getActivities();
    }

    @Override
    @NotNull
    public OnlineStatus getOnlineStatus() {
	return this.member.getOnlineStatus();
    }

    @Override
    @NotNull
    public OnlineStatus getOnlineStatus(@NotNull final ClientType clientType) {
	return this.member.getOnlineStatus(clientType);
    }

    @Override
    @NotNull
    public EnumSet<ClientType> getActiveClients() {
	return this.member.getActiveClients();
    }

    @Override
    public String getNickname() {
	return this.member.getNickname();
    }

    @Override
    @NotNull
    public String getEffectiveName() {
	return this.member.getEffectiveName();
    }

    @Override
    @NotNull
    public List<Role> getRoles() {
	return this.member.getRoles();
    }

    @Override
    public Color getColor() {
	return this.member.getColor();
    }

    @Override
    public int getColorRaw() {
	return this.member.getColorRaw();
    }

    @Override
    public boolean canInteract(@NotNull final Member member) {
	return this.member.canInteract(member);
    }

    @Override
    public boolean canInteract(@NotNull final Role role) {
	return this.member.canInteract(role);
    }

    @Override
    public boolean canInteract(@NotNull final Emote emote) {
	return this.member.canInteract(emote);
    }

    @Override
    public boolean isOwner() {
	return this.member.isOwner();
    }

    @Override
    public TextChannel getDefaultChannel() {
	return this.member.getDefaultChannel();
    }

    @Override
    @NotNull
    public AuditableRestAction<Void> ban(final int delDays) {
	return this.getMember().ban(delDays);
    }

    @Override
    @NotNull
    public AuditableRestAction<Void> ban(final int delDays, final String reason) {
	return this.getMember().ban(delDays, reason);
    }

    @Override
    @NotNull
    public AuditableRestAction<Void> kick() {
	return this.getMember().kick();
    }

    @Override
    @NotNull
    public AuditableRestAction<Void> kick(final String reason) {
	return this.getMember().kick(reason);
    }

    @Override
    @NotNull
    public AuditableRestAction<Void> modifyNickname(final String nickname) {
	return this.getMember().modifyNickname(nickname);
    }

    @Override
    @NotNull
    public String getAsMention() {
	return this.member.getAsMention();
    }

    @Override
    public void formatTo(final Formatter formatter, final int flags, final int width, final int precision) {
	this.getMember().formatTo(formatter, flags, width, precision);
    }

    @Override
    @NotNull
    public String getId() {
	return this.getMember().getId();
    }

    @Override
    public long getIdLong() {
	return this.member.getIdLong();
    }

    @Override
    @NotNull
    public OffsetDateTime getTimeCreated() {
	return this.getMember().getTimeCreated();
    }

    @Override
    public boolean canSync(final GuildChannel targetChannel, final GuildChannel syncSource) {
	return this.member.canSync(targetChannel, syncSource);
    }

    @Override
    public boolean canSync(final GuildChannel channel) {
	return this.member.canSync(channel);
    }

    @Override
    public boolean isPending() {
	return this.member.isPending();
    }
}