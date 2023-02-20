package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.systems.customcommand.CustomCommand;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.reload.ReloadSystem;
import com.github.black0nion.blackonionbot.systems.reload.Reloadable;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.GuildImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class BlackGuild extends GuildImpl {

	private BlackMember selfBlackMember;

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
	private long suggestionsChannel;
	private HashMap<String, CustomCommand> customCommands;
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

	public Warn getWarn(long id) {
		return this.warns.stream().filter(w -> w.id() == id).findFirst().orElse(null);
	}

	/**
	 * @return an unmodifiable list of all warns
	 */
	public List<Warn> getWarns() {
		return Collections.unmodifiableList(this.warns);
	}

	@Nullable
	public Language getLanguage() {
		return this.language;
	}

	public String getLanguageString() {
		return this.language != null ? this.language.getLanguageCode() : null;
	}

	public void setLanguage(final @Nullable Language language) {
		this.language = language;
	}

	public long getSuggestionsChannel() {
		return this.suggestionsChannel;
	}

	public void setSuggestionsChannel(final TextChannel channel) {
		this.setSuggestionsChannel(channel.getIdLong());
	}

	public void setSuggestionsChannel(final long suggestionsChannel) {
		this.suggestionsChannel = suggestionsChannel;
	}

	public BlackMember getSelfBlackMember() {
		if (this.selfBlackMember == null) {
			this.selfBlackMember = BlackMember.from(this.guild.getSelfMember(), this);
		}
		return this.selfBlackMember;
	}

	public void setSelfBlackMember(final BlackMember selfBlackMember) {
		this.selfBlackMember = selfBlackMember;
	}


	/**
	 * @return the customCommands
	 */
	public Map<String, CustomCommand> getCustomCommands() {
		return this.customCommands;
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean addCustomCommand(final CustomCommand cmd) {
		if (this.customCommands.containsKey(cmd.getCommand())) return false;
		this.customCommands.put(cmd.getCommand(), cmd);
		return true;
	}

	public void deleteCustomCommand(final String commandName) {
		this.customCommands.remove(commandName);
	}

	public String getEscapedName() {
		return Utils.escapeMarkdown(this.getName());
	}

	@Override
	public String toString() {
		return "BlackGuild{" +
			"selfBlackMember=" + selfBlackMember +
			", language=" + language +
			", suggestionsChannel=" + suggestionsChannel +
			", customCommands=" + customCommands +
			'}';
	}
}
