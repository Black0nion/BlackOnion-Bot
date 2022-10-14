package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.misc.*;
import com.github.black0nion.blackonionbot.systems.CustomCommand;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.systems.dashboard.DashboardGetter;
import com.github.black0nion.blackonionbot.systems.dashboard.DashboardSetter;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.GuildImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BlackGuild extends GuildImpl {

	private BlackMember selfBlackMember;

	private static final LoadingCache<Guild, BlackGuild> guilds = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Override
		public @NotNull BlackGuild load(final @NotNull Guild guild) {
		return new BlackGuild(guild);
		}
	});

	@Reloadable("guildcache")
	public static void clearCache() {
		guilds.invalidateAll();
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
	private GuildType guildType;
	private AntiSpoilerSystem.AntiSpoilerType antiSpoilerType;
	private List<String> antiSwearWhitelist;
	private String joinMessage;
	private long joinChannel;
	private String leaveMessage;
	private long leaveChannel;
	@Nullable
	private List<SlashCommand> disabledCommands;
	private long suggestionsChannel;
	private List<Long> autoRoles;
	private boolean loop;
	private HashMap<String, CustomCommand> customCommands;
	private final List<Warn> warns = new ArrayList<>();

	private BlackGuild(@NotNull final Guild guild) {
		super(guild);

		try {
			this.warns.addAll(Warn.loadWarns("guild", this.getIdLong()));
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

	@DashboardGetter("setup.language")
	public String getLanguageString() {
		return this.language != null ? this.language.getLanguageCode() : null;
	}

	@DashboardSetter("setup.language")
	public void setLanguage(final @Nullable String language) {
		this.setLanguage(LanguageSystem.getLanguageFromName(language));
	}

	public void setLanguage(final @Nullable Language language) {
		this.language = language;
	}

	@DashboardGetter("setup.guildtype")
	public GuildType getGuildType() {
		return this.guildType;
	}

	public void setGuildType(final GuildType type) {
		this.guildType = type;
	}

	public boolean isPremium() {
		return this.getGuildType().higherThanOrEqual(GuildType.PREMIUM);
	}

	@DashboardGetter("utils.joinleave.join.message")
	public String getJoinMessage() {
		return this.joinMessage;
	}

	@DashboardSetter("utils.joinleave.join.message")
	public void setJoinMessage(final String newMessage) {
		this.joinMessage = newMessage;
	}

	@DashboardGetter(value = "utils.joinleave.join.channel", nullable = true)
	public long getJoinChannel() {
		return this.joinChannel;
	}

	@DashboardSetter(value = "utils.joinleave.join.channel", nullable = true)
	public void setJoinChannel(final TextChannel channel) {
		if (channel == null) {
			this.setJoinChannel(-1);
		} else {
			this.setJoinChannel(channel.getIdLong());
		}
	}

	public void setJoinChannel(final long joinChannel) {
		this.joinChannel = joinChannel;
	}

	@DashboardGetter("utils.joinleave.leave.message")
	public String getLeaveMessage() {
		return this.leaveMessage;
	}

	@DashboardSetter("utils.joinleave.leave.message")
	public void setLeaveMessage(final String leaveMessage) {
		this.leaveMessage = leaveMessage;
	}

	@DashboardGetter(value = "utils.joinleave.leave.channel", nullable = true)
	public long getLeaveChannel() {
		return this.leaveChannel;
	}

	@ConfigGetter(key = "leavechannel", requiredPermissions = { Permission.MANAGE_SERVER }, description = "Set the Channel the Message on leave should be sent to")
	public TextChannel getLeaveChannelAsChannel() {
		if (this.leaveChannel == -1) return null;
		return this.guild.getTextChannelById(this.leaveChannel);
	}

	@DashboardSetter(value = "utils.joinleave.leave.channel", nullable = true)
	@ConfigSetter(argumentsDescription = { "The Channel where the message should get sent to" }, key = "leavechannel", requiredPermissions = { Permission.MANAGE_SERVER }, nullable = true)
	public void setLeaveChannel(final TextChannel channel) {
		if (channel == null) {
			this.setLeaveChannel(-1);
		} else {
			this.setLeaveChannel(channel.getIdLong());
		}
	}

	public void setLeaveChannel(final long leaveChannel) {
		this.leaveChannel = leaveChannel;
	}

	public @Nullable List<SlashCommand> getDisabledCommands() {
		return this.disabledCommands;
	}

	public void setDisabledCommands(final String[] disabledCommands) {
		this.setDisabledCommands(Arrays.stream(disabledCommands)
			.map(SlashCommandBase::getCommand)
			.filter(Objects::nonNull)
			.collect(Collectors.toList()));
	}

	public void setDisabledCommands(final List<SlashCommand> disabledCommands) {
		this.disabledCommands = disabledCommands;
	}

	public boolean isCommandActivated(final SlashCommand cmd) {
		return this.disabledCommands == null || !this.disabledCommands.contains(cmd);
	}

	public boolean setCommandActivated(final SlashCommand cmd, final boolean activated) {
		if (!cmd.isToggleable()) return false;
		if (this.disabledCommands == null) {
			if (activated) return true;
			else this.disabledCommands = new ArrayList<>();
		}

		if (!activated) {
			this.disabledCommands.add(cmd);
		} else {
			this.disabledCommands.remove(cmd);
		}

		this.setDisabledCommands(this.disabledCommands);
		return true;
	}

	public AntiSpoilerSystem.AntiSpoilerType getAntiSpoilerType() {
		return this.antiSpoilerType != null ? this.antiSpoilerType : AntiSpoilerSystem.AntiSpoilerType.OFF;
	}

	public void setAntiSpoilerType(final AntiSpoilerSystem.AntiSpoilerType antiSpoilerType) {
		this.antiSpoilerType = antiSpoilerType;
	}

	public List<String> getAntiSwearWhitelist() {
		return this.antiSwearWhitelist;
	}

	public void setAntiSwearWhitelist(final List<String> antiSwearWhitelist) {
		this.antiSwearWhitelist = antiSwearWhitelist;
	}

	public void addToAntiSwearWhitelist(final String toAdd) {
		this.antiSwearWhitelist.add(toAdd);
	}

	public void removeFromAntiSwearWhitelist(final String toRemove) {
		this.antiSwearWhitelist.remove(toRemove);
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

	public void removeAutoRole(final long roleId) {
		this.autoRoles.remove(roleId);
	}

	public void addAutoRole(final long roleId) {
		if (!this.autoRoles.contains(roleId)) {
			this.autoRoles.add(roleId);
		}
	}

	public void setAutoRoles(final List<Long> autoRoles) {
		this.autoRoles = autoRoles;
	}

	public List<Long> getAutoRoles() {
		return this.autoRoles;
	}

	public void setSelfBlackMember(final BlackMember selfBlackMember) {
		this.selfBlackMember = selfBlackMember;
	}

	public boolean loopActivated() {
		return this.loop;
	}

	public void setLoop(final boolean loop) {
		this.loop = loop;
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
			", guildType=" + guildType +
			", antiSpoilerType=" + antiSpoilerType +
			", antiSwearWhitelist=" + antiSwearWhitelist +
			", joinMessage='" + joinMessage + '\'' +
			", joinChannel=" + joinChannel +
			", leaveMessage='" + leaveMessage + '\'' +
			", leaveChannel=" + leaveChannel +
			", disabledCommands=" + disabledCommands +
			", suggestionsChannel=" + suggestionsChannel +
			", autoRoles=" + autoRoles +
			", loop=" + loop +
			", customCommands=" + customCommands +
			'}';
	}
}
