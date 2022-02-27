package com.github.black0nion.blackonionbot.blackobjects;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
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

	private static final MongoCollection<Document> configs = MongoManager.getCollection("usersettings", MongoDB.DATABASE);

	private static final LoadingCache<User, BlackUser> users = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<User, BlackUser>() {
		@Override
		public BlackUser load(final User user) {
			return new BlackUser(user);
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
		return users.stream().map(BlackUser::from).collect(Collectors.toList());
	}

	@Deprecated
	/**
	 * Deprecated as a warning
	 *
	 * @param  guild
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

		this.save("name", user.getName());

		try {
			Document config = configs.find(Filters.eq("userid", user.getIdLong())).first();

			if (config == null) {
				config = new Document();
			}

			this.permissions = Utils.gOD(CustomPermission.parse(Utils.gOD(config.getList("permissions", String.class), new ArrayList<>())), new ArrayList<>());

			if (config.getString("language") != null) {
				this.language = Utils.gOD(LanguageSystem.getLanguageFromName(config.getString("language")), LanguageSystem.defaultLocale);
			} else {
				this.language = LanguageSystem.getDefaultLanguage();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(final Language language) {
		this.language = language;
		this.save("language", language.getLanguageCode());
	}

	public List<CustomPermission> getPermissions() {
		return this.permissions;
	}

	public boolean hasPermission(final CustomPermission... permissions) {
		if (permissions == null || permissions.length == 0) return true;
		if (this.permissions == null || this.permissions.size() == 0) return false;
		for (final CustomPermission requiredPerm : permissions) {
			if (!this.hasPermission(requiredPerm)) return false;
		}
		return true;
	}

	public boolean hasPermission(final CustomPermission permission) {
		return CustomPermission.hasRights(permission, this.permissions);
	}

	public List<CustomPermission> addPermissions(final CustomPermission... permissions) {
		return this.addPermissions(Arrays.asList(permissions));
	}

	public List<CustomPermission> addPermissions(final List<CustomPermission> permissions) {
		final List<CustomPermission> perms = this.permissions;
		final List<CustomPermission> addedPerms = new ArrayList<>();
		for (final CustomPermission perm : permissions) {
			if (!perms.contains(perm)) {
				perms.add(perm);
				addedPerms.add(perm);
			}
		}
		this.setPermissions(perms);
		return addedPerms;
	}

	public List<CustomPermission> removePermissions(final CustomPermission... permissions) {
		return this.removePermissions(Arrays.asList(permissions));
	}

	public List<CustomPermission> removePermissions(final List<CustomPermission> permissions) {
		final List<CustomPermission> perms = this.permissions;
		final List<CustomPermission> removedPerms = new ArrayList<>();
		for (final CustomPermission perm : permissions) {
			if (perms.contains(perm)) {
				perms.remove(perm);
				removedPerms.add(perm);
			}
		}
		this.setPermissions(perms);
		return removedPerms;
	}

	public void setPermissions(final List<CustomPermission> permissions) {
		this.permissions = permissions;
		this.save("permissions", permissions.stream().map(CustomPermission::name).collect(Collectors.toList()));
	}

	@Nonnull
	/**
	 * @return Full name WITH REPLACED MARKDOWN
	 */
	public String getFullName() {
		return Utils.removeMarkdown(this.getName() + "#" + this.getDiscriminator());
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

	@NotNull
	@Override
	public RestAction<Profile> retrieveProfile() {
		return this.user.retrieveProfile();
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