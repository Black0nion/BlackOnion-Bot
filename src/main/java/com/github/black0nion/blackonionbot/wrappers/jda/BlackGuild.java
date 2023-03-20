package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.reload.ReloadSystem;
import com.github.black0nion.blackonionbot.systems.reload.Reloadable;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.GuildImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class BlackGuild extends GuildImpl {

	private static final LoadingCache<Guild, BlackGuild> guilds = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Override
		public @NotNull BlackGuild load(final @NotNull Guild guild) {
			return new BlackGuild(guild);
		}
	});

	public static class GuildCacheClear implements Reloadable {

		public GuildCacheClear(ReloadSystem reloadSystem) {
			reloadSystem.registerReloadable(this);
		}

		@Override
		public void reload() {
			guilds.invalidateAll();
		}
	}

	public static BlackGuild from(@Nullable final Guild guild) {
		if (guild == null) return null;
		try {
			return guilds.get(guild);
		} catch (final Exception e) {
			if (!(e instanceof IllegalStateException)) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Nullable
	public static BlackGuild from(final long guildid) {
		final Optional<Entry<Guild, BlackGuild>> first = guilds.asMap().entrySet().stream().filter(entry -> entry.getKey().getIdLong() == guildid).findFirst();
		return first.isPresent() ? first.get().getValue() : from(Bot.getInstance().getJDA().getGuildById(guildid));
	}

	private Language language;
	private final List<Warn> warns = new ArrayList<>();

	private BlackGuild(@NotNull final Guild guild) {
		super(guild);

		try {
			this.warns.addAll(Warn.loadWarns(Bot.getInstance().getSqlHelperFactory(), "guild", this.getIdLong()));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void addWarn(Warn w) {
		this.warns.add(w);
		// don't save the list here, already done by the BlackMember class
	}

	public void removeWarn(Warn w) {
		this.warns.remove(w);
	}

	void deleteUserWarns(long userId) {
		this.warns.removeIf(w -> w.userid() == userId);
	}

	@Override
	public String toString() {
		return "BlackGuild{" +
			", language=" + language +
			'}';
	}
}
