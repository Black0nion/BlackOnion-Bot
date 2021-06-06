package com.github.black0nion.blackonionbot.blackobjects;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

public class BlackUser extends BlackObject implements User {
	
	private final User user;
	
	private static final MongoCollection<Document> configs = MongoManager.getCollection("usersettings", MongoDB.botDatabase);

	private static final LoadingCache<User, BlackUser> users = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<User, BlackUser>() {
                @Override
                public BlackUser load(final User user) {
                	final BlackUser blacc = new BlackUser(user);
                    return blacc;
                }
            });
	
	public static BlackUser from(@NotNull final Member member) {
		return from(member.getUser());
	}

	public static BlackUser from(@NotNull final User user) {
		try {
			return users.get(user);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<BlackUser> from(final List<User> users) {
		return users.stream().map(user -> from(user)).collect(Collectors.toList());
	}
	
	@Deprecated
	/**
	 * Deprecated as a warning
	 * @param guild
	 * @return
	 */
	@Reloadable("usercache")
	public static final void clearCache() {
		users.invalidateAll();
	}
	
	private Language language;
	private List<CustomPermission> permissions;
	
	private BlackUser(final User user) {
		this.user = user;
		
		try {
			Document config = configs.find(Filters.eq("userid", user.getIdLong())).first();
			
			if (config == null) config = new Document();
			
			permissions = CustomPermission.parse(gOD(config.getList("permissions", String.class), new ArrayList<>()));
		
			if (config.getString("language") != null)
				this.language = gOD(LanguageSystem.getLanguageFromName(config.getString("language")), LanguageSystem.defaultLocale);
			else
				this.language = LanguageSystem.getDefaultLanguage();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public void setLanguage(final Language language) {
		this.language = language;
		save("language", language.getLanguageCode());
	}
	
	public List<CustomPermission> getPermissions() {
		return permissions;
	}
	
	public boolean hasPermission(final CustomPermission... permissions) {
		for (final CustomPermission requiredPerm : permissions)
			if (!hasPermission(requiredPerm)) return false;
		return true;
	}
	
	public boolean hasPermission(final CustomPermission permission) {
		for (final CustomPermission perm : this.permissions)
			if (perm.hasPermission(permission))
				return true;
		return false;
	}
	
	public void addPermissions(final CustomPermission... permissions) {
		final List<CustomPermission> perms = this.permissions;
		for (final CustomPermission perm : permissions)
			if (!perms.contains(perm)) perms.add(perm);
		setPermissions(perms);
	}
	
	public void setPermissions(final List<CustomPermission> permissions) {
		this.permissions = permissions;
		save("permissions", permissions.stream().map(perm -> perm.name()).collect(Collectors.toList()));
	}
	
	// override methods
	@Override
	public Document getIdentifier() {
		return new Document("userid", this.user.getIdLong());
	}
	
	@Override
	MongoCollection<Document> getCollection() {
		return configs;
	}
	
	// built in methods
	@Override
	@NotNull
	public String getName() {
		return this.user.getName();
	}

	@Override
	@NotNull
	public String getDiscriminator() {
		return this.user.getDiscriminator();
	}

	@Override
	public String getAvatarId() {
		return this.user.getAvatarId();
	}

	@Override
	public String getAvatarUrl() {
		return this.user.getAvatarUrl();
	}

	@Override
	@NotNull
	public String getDefaultAvatarId() {
		return this.user.getDefaultAvatarId();
	}

	@Override
	@NotNull
	public String getDefaultAvatarUrl() {
		return this.user.getDefaultAvatarUrl();
	}

	@Override
	@NotNull
	public String getEffectiveAvatarUrl() {
		return this.user.getEffectiveAvatarUrl();
	}

	@Override
	@NotNull
	public String getAsTag() {
		return this.user.getAsTag();
	}

	@Override
	public boolean hasPrivateChannel() {
		return this.user.hasPrivateChannel();
	}

	@Override
	@NotNull
	public RestAction<PrivateChannel> openPrivateChannel() {
		return this.user.openPrivateChannel();
	}

	@Override
	@NotNull
	public List<Guild> getMutualGuilds() {
		return this.user.getMutualGuilds();
	}

	@Override
	public boolean isBot() {
		return this.user.isBot();
	}

	@Override
	@NotNull
	public JDA getJDA() {
		return this.user.getJDA();
	}

	@NotNull
	@Override
	public EnumSet<UserFlag> getFlags() {
		return this.user.getFlags();
	}

	@Override
	public int getFlagsRaw() {
		return this.user.getFlagsRaw();
	}

	@Deprecated
	@Override
	public boolean isFake() {
		return this.user.isFake();
	}

	@Override
	@NotNull
	public String getAsMention() {
		return this.user.getAsMention();
	}

	@Override
	public void formatTo(final Formatter formatter, final int flags, final int width, final int precision) {
		this.user.formatTo(formatter, flags, width, precision);
	}

	@Override
	@NotNull
	public String getId() {
		return this.user.getId();
	}

	@Override
	public long getIdLong() {
		return this.user.getIdLong();
	}

	@Override
	@NotNull
	public OffsetDateTime getTimeCreated() {
		return this.user.getTimeCreated();
	}

	@Override
	public boolean isSystem() {
		return this.user.isSystem();
	}
}