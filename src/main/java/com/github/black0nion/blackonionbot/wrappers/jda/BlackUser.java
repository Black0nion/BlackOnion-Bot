package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.UserImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BlackUser extends UserImpl {

	private static final LoadingCache<User, BlackUser> USER_CACHE = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Override
		public @Nonnull BlackUser load(final @Nonnull User user) {
		return Utils.uncheckedSupplier(() -> new BlackUser(user));
		}
	});

	public static BlackUser from(@Nonnull final Member member) {
		return from(member.getUser());
	}

	@Nonnull
	public static BlackUser from(@Nonnull final User user) {
		try {
			return USER_CACHE.get(user);
		} catch (ExecutionException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	@Reloadable("usercache")
	public static void clearCache() {
		USER_CACHE.invalidateAll();
	}

	@SQLSetup(after = LanguageSystem.class)
	public static void setup(SQLHelperFactory sql) throws SQLException {
		sql.run("CREATE TABLE IF NOT EXISTS usersettings (id BIGINT PRIMARY KEY NOT NULL, language VARCHAR(2), permissions VARCHAR(255), FOREIGN KEY (language) REFERENCES language (code))");
	}

	private Language language;
	private List<CustomPermission> permissions;

	private BlackUser(final User user) throws SQLException {
		super(user);

		try (SQLHelper sq = Bot.getInstance().getSqlHelperFactory().create("SELECT UPPER(permissions) AS permissions FROM usersettings WHERE id = ?", getIdLong()); ResultSet rs = sq.executeQuery()) {
			if (rs.next()) {
				permissions = CustomPermission.parseListToList(rs.getString("permissions"));
			}
		}
	}

	@Nullable
	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(final @Nullable Language language) {
		this.language = language;
	}

	public List<CustomPermission> getPermissions() {
		return this.permissions;
	}

	public boolean hasPermission(final CustomPermission... permissions) {
		if (permissions == null || permissions.length == 0) return true;
		if (this.permissions == null || this.permissions.isEmpty()) return false;
		for (final CustomPermission requiredPerm : permissions) {
			if (!this.hasPermission(requiredPerm)) return false;
		}
		return true;
	}

	public boolean hasPermission(final CustomPermission permission) {
		return CustomPermission.hasRights(permission, this.permissions);
	}

	public void addPermissions(final CustomPermission... permissions) {
		this.addPermissions(Arrays.asList(permissions));
	}

	public void addPermissions(final List<CustomPermission> permissions) {
		final List<CustomPermission> perms = this.permissions;
		for (final CustomPermission perm : permissions) {
			if (!perms.contains(perm)) {
				perms.add(perm);
			}
		}
		this.setPermissions(perms);
	}

	public void removePermissions(final CustomPermission... permissions) {
		this.removePermissions(Arrays.asList(permissions));
	}

	public void removePermissions(final List<CustomPermission> permissions) {
		final List<CustomPermission> perms = this.permissions;
		for (final CustomPermission perm : permissions) {
			perms.remove(perm);
		}
		this.setPermissions(perms);
	}

	public void setPermissions(final List<CustomPermission> permissions) {
		this.permissions = permissions;
	}

	public String getEscapedName() {
		return Utils.escapeMarkdown(this.getName());
	}

	public String getEscapedEffectiveName() {
		return this.getEscapedName() + "#" + this.getDiscriminator();
	}

	@Override
	public String toString() {
		return "BlackUser{" +
			"fullName=" + this.getFullName() +
			"language=" + language +
			", permissions=" + permissions +
			'}';
	}
}
