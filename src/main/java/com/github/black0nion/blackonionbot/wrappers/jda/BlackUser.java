package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.reload.ReloadSystem;
import com.github.black0nion.blackonionbot.systems.reload.Reloadable;
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

	public static class UserCacheClear implements Reloadable {

		public UserCacheClear(ReloadSystem reloadSystem) {
			reloadSystem.registerReloadable(this);
		}

		@Override
		public void reload() {
			USER_CACHE.invalidateAll();
		}
	}

	private Language language;

	private BlackUser(final User user) {
		super(user);
	}

	@Nullable
	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(final @Nullable Language language) {
		this.language = language;
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
			'}';
	}
}
