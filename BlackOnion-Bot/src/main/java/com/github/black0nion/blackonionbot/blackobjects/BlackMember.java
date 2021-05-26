package com.github.black0nion.blackonionbot.blackobjects;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
	
	private static final MongoCollection<Document> configs = MongoManager.getCollection("membersettings", MongoDB.botDatabase);
	
	private final Member member;
	private final BlackGuild blackGuild;
	
	private static final LoadingCache<Member, BlackMember> members = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<Member, BlackMember>() {
                @Override
                public BlackMember load(final Member member) {
                    return new BlackMember(member, BlackGuild.from(member.getGuild()));
                }
            });
	
	public static BlackMember from(final Member member) {
		try {
			return members.get(member);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<BlackMember> from(@NotNull final List<Member> members) {
		return members.stream().map(mem -> from(mem)).collect(Collectors.toList());
	}
	
	private BlackMember(@NotNull final Member member, BlackGuild blackGuild) {
		this.member = member;
		this.blackGuild = blackGuild;
	}
	
	public Member getMember() {
        return this.member;
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
        return member.canInteract(member);
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

    @Deprecated
    @Override
    public boolean isFake() {
        return this.member.isFake();
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
	public boolean canSync(GuildChannel targetChannel, GuildChannel syncSource) {
		return this.member.canSync(targetChannel, syncSource);
	}

	@Override
	public boolean canSync(GuildChannel channel) {
		return this.member.canSync(channel);
	}

	@Override
	public boolean isPending() {
		return this.member.isPending();
	}
}