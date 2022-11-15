package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.MemberImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.black0nion.blackonionbot.misc.Warn.loadWarns;

public class BlackMember extends MemberImpl {

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

	@Reloadable("membercache")
	public static void clearCache() {
		members.invalidateAll();
	}

	private final BlackGuild blackGuild;
	private final List<Warn> warns = new ArrayList<>();

	private BlackMember(@NotNull final Member member, final BlackGuild blackGuild) {
		super(member);
		this.blackGuild = blackGuild;

		loadWarns(Bot.getInstance().getSqlHelperFactory(), "user", this.getIdLong());
		this.warns.forEach(this.blackGuild::addWarn);
	 	this.blackUser = BlackUser.from(this.member.getUser());
	}

	private final BlackUser blackUser;
	public BlackUser getBlackUser() {
		return blackUser;
	}

	/**
	 * Will also update the warn on the guild
	 */
	public void warn(final Warn w) {
		this.warns.add(w);
		this.blackGuild.addWarn(w);
		saveWarns();
	}

	public void deleteWarn(final Warn w) {
		this.warns.remove(w);
		this.blackGuild.removeWarn(w);
		saveWarns();
	}

	public void clearWarns() {
		this.warns.clear();
		this.blackGuild.deleteUserWarns(this.getIdLong());
		saveWarns();
	}

	private void saveWarns() {
		Warn.saveWarns(Bot.getInstance().getSqlHelperFactory(), this.warns, this.getIdLong());
	}

	/**
	 * @return an unmodifiable list of all warns
	 */
	public List<Warn> getWarns() {
		return Collections.unmodifiableList(this.warns);
	}

	@Override
	public String toString() {
		return "BlackMember{" +
			"blackGuild=" + blackGuild +
			", warns=" + warns +
			", blackUser=" + blackUser +
			'}';
	}
}
