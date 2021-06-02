package com.github.black0nion.blackonionbot.blackobjects;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.DashboardValue;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VanityInvite;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.MemberAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;

public class BlackGuild extends BlackObject implements Guild {

	private final Guild guild;
	private final BlackMember selfBlackMember;

	private static final MongoCollection<Document> configs = MongoManager.getCollection("guildsettings",
			MongoDB.botDatabase);

	private static final LoadingCache<Guild, BlackGuild> guilds = CacheBuilder.newBuilder()
			.expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<Guild, BlackGuild>() {
				@Override
				public BlackGuild load(final Guild guild) {
					return new BlackGuild(guild);
				}
			});

	@Deprecated
	/**
	 * Deprecated as a warning
	 * 
	 * @param guild
	 * @return
	 */
	public static final void clearCache() {
		guilds.invalidateAll();
	}

	public static BlackGuild from(@Nullable final Guild guild) {
		if (guild == null)
			return null;
		try {
			return guilds.get(guild);
		} catch (Exception e) {
			if (!(e instanceof IllegalStateException))
				e.printStackTrace();
			return null;
		}
	}

	@Nullable
	public static BlackGuild from(@NotNull final long guildid) {
		final Optional<Entry<Guild, BlackGuild>> first = guilds.asMap().entrySet().stream()
				.filter(entry -> entry.getKey().getIdLong() == guildid).findFirst();
		return first.isPresent() ? first.get().getValue() : from(Bot.jda.getGuildById(guildid));
	}

	private Language language;
	private boolean isPremium;
	private AntiSpoilerType antiSpoilerType;
	private AntiSwearType antiSwearType;
	private List<String> antiSwearWhitelist;
	private String prefix;
	private String joinMessage;
	private long joinChannel;
	private String leaveMessage;
	private long leaveChannel;
	private List<Command> disabledCommands;
	private long suggestionsChannel;
	private List<Long> autoRoles;

	private BlackGuild(@NotNull final Guild guild) {
		this.guild = guild;
		this.selfBlackMember = BlackMember.from(guild.getSelfMember());

		try {
			Document config = configs.find(this.getIdentifier()).first();

			if (config == null)
				config = new Document();

			this.language = gOD(LanguageSystem.getLanguageFromName(config.getString("language")),
					LanguageSystem.defaultLocale);
			this.isPremium = gOS("isPremium", config.getBoolean("isPremium"), false);
			this.prefix = gOD(config.getString("prefix"), BotInformation.defaultPrefix);
			this.antiSpoilerType = gOD(AntiSpoilerType.parse(config.getString("antiSpoiler")), AntiSpoilerType.OFF);
			this.antiSwearType = gOD(AntiSwearType.parse(config.getString("antiSwear")), AntiSwearType.OFF);
			this.joinMessage = gOD(config.getString("joinmessage"),
					this.language.getTranslationNonNull("defaultjoinmessage"));
			this.joinChannel = gOD(config.getLong("joinchannel"), -1L);
			this.leaveMessage = gOD(config.getString("leavemessage"),
					this.language.getTranslationNonNull("defaultleavemessage"));
			this.leaveChannel = gOD(config.getLong("leavechannel"), -1L);
			this.suggestionsChannel = gOD(config.getLong("suggestionschannel"), -1L);
			this.autoRoles = gOD(config.getList("autoroles", Long.class), new ArrayList<>());
			this.antiSwearWhitelist = gOD(config.getList("antiswearwhitelist", String.class), new ArrayList<>());
			final List<String> disabledCommandsString = config.getList("disabledCommands", String.class);
			if (!(disabledCommandsString == null || disabledCommandsString.isEmpty())) 
				this.disabledCommands = disabledCommandsString.stream().map(cmd -> CommandBase.commands.get(cmd)).collect(Collectors.toList());
			else 
				this.disabledCommands = new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Language getLanguage() {
		return language;
	}

	@DashboardValue("guildlanguage")
	public void setLanguage(Language language) {
		this.language = language;
		save("language", language.getLanguageCode());
	}

	public boolean isPremium() {
		return isPremium;
	}

	public String getPrefix() {
		return prefix;
	}

	@DashboardValue("prefix")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		save("prefix", prefix);
	}

	public String getJoinMessage() {
		return joinMessage;
	}

	@DashboardValue("joinmessage")
	public void setJoinMessage(String newMessage) {
		this.joinMessage = newMessage;
		save("joinmessage", this.joinMessage);
	}

	public long getJoinChannel() {
		return joinChannel;
	}

	@DashboardValue("joinchannel")
	public void setJoinChannel(long joinChannel) {
		this.joinChannel = joinChannel;
		if (joinChannel == -1)
			clear("joinchannel");
		save("joinChannel", joinChannel);
	}

	public String getLeaveMessage() {
		return leaveMessage;
	}

	@DashboardValue("leavemessage")
	public void setLeaveMessage(String leaveMessage) {
		this.leaveMessage = leaveMessage;
		save("leavemessage", leaveMessage);
	}

	public long getLeaveChannel() {
		return leaveChannel;
	}

	@DashboardValue("leavechannel")
	public void setLeaveChannel(long leaveChannel) {
		this.leaveChannel = leaveChannel;
		if (leaveChannel == -1)
			clear("leavechannel");
		save("leaveChannel", leaveChannel);
	}

	public List<Command> getDisabledCommands() {
		return this.disabledCommands;
	}

	@DashboardValue("disabledcommands")
	public void setDisabledCommands(Object[] disabledCommands) {
		this.setDisabledCommands(Arrays.asList(disabledCommands).stream()
				.map(cmd -> CommandBase.commands.get((String) cmd)).collect(Collectors.toList()));
	}

	public void setDisabledCommands(Command[] disabledCommands) {
		this.setDisabledCommands(Arrays.asList(disabledCommands));
	}

	public void setDisabledCommands(List<Command> disabledCommands) {
		this.disabledCommands = disabledCommands;
		saveList("disabledCommands", disabledCommands.stream().map(cmd -> cmd.getCommand()[0]).collect(Collectors.toList()));
	}
	
	public boolean isCommandActivated(Command cmd) {
		return !this.disabledCommands.contains(cmd);
	}
	
	public boolean setCommandActivated(Command cmd, boolean activated) {
		if  (!cmd.isToggleable()) return false;
		if (disabledCommands.contains(cmd) && activated) {
			disabledCommands.remove(cmd);
		} else if (activated && !disabledCommands.contains(cmd)) {
			disabledCommands.add(cmd);
		}
		setDisabledCommands(this.disabledCommands);
		return true;
	}

	public AntiSpoilerType getAntiSpoilerType() {
		return antiSpoilerType;
	}

	@DashboardValue("antispoilertype")
	public void setAntiSpoilerType(AntiSpoilerType antiSpoilerType) {
		this.antiSpoilerType = antiSpoilerType;
		save("antiSpoiler", antiSpoilerType.name());
	}

	public AntiSwearType getAntiSwearType() {
		return antiSwearType;
	}

	public void setAntiSwearType(AntiSwearType antiSwearType) {
		this.antiSwearType = antiSwearType;
		save("antiSwear", antiSwearType.name());
	}
	
	public List<String> getAntiSwearWhitelist() {
		return antiSwearWhitelist;
	}
	
	public void setAntiSwearWhitelist(List<String> antiSwearWhitelist) {
		this.antiSwearWhitelist = antiSwearWhitelist;
		saveAntiSwearWhitelist();
	}
	
	public void addToAntiSwearWhitelist(String toAdd) {
		this.antiSwearWhitelist.add(toAdd);
		saveAntiSwearWhitelist();
	}
	
	public void removeFromAntiSwearWhitelist(String toRemove) {
		this.antiSwearWhitelist.remove(toRemove);
		saveAntiSwearWhitelist();
	}
	
	public void saveAntiSwearWhitelist() {
		saveList("antiswearwhitelist", antiSwearWhitelist);
	}
	
	public long getSuggestionsChannel() {
		return suggestionsChannel;
	}
	
	@DashboardValue(value = "suggestionschannel", channelSelector = true)
	public void setSuggestionsChannel(long suggestionsChannel) {
		this.suggestionsChannel = suggestionsChannel;
		save("suggestionschannel", suggestionsChannel);
	}
	
	public BlackMember getSelfBlackMember() {
		return selfBlackMember;
	}
	
	public void removeAutoRole(long roleId) {
		this.autoRoles.remove(roleId);
		saveAutoRoles();
	}
	
	public void addAutoRole(long roleId) {
		if (!this.autoRoles.contains(roleId)) this.autoRoles.add(roleId);
		saveAutoRoles();
	}
	
	public void setAutoRoles(List<Long> autoRoles) {
		this.autoRoles = autoRoles;
		saveAutoRoles();
	}
	
	public void saveAutoRoles() {
		saveList("autoroles", autoRoles);
	}
	
	public List<Long> getAutoRoles() {
		return autoRoles;
	}

	// override methods
	@Override
	public Document getIdentifier() {
		return new Document("guildid", this.guild.getIdLong());
	}

	@Override
	MongoCollection<Document> getCollection() {
		return configs;
	}

	// built in methods
	@NotNull
	@Override
	public RestAction<EnumSet<Region>> retrieveRegions(final boolean b) {
		return this.guild.retrieveRegions(b);
	}

	@NotNull
	@Override
	public MemberAction addMember(@NotNull final String s, @NotNull final String s1) {
		return this.guild.addMember(s, s1);
	}

	@Override
	public boolean isLoaded() {
		return this.guild.isLoaded();
	}

	@Override
	public void pruneMemberCache() {
		this.guild.pruneMemberCache();
	}

	@Override
	public boolean unloadMember(final long l) {
		return this.guild.unloadMember(l);
	}

	@Override
	public int getMemberCount() {
		return this.guild.getMemberCount();
	}

	@Override
	@NotNull
	public String getName() {
		return this.guild.getName();
	}

	@Override
	public String getIconId() {
		return this.guild.getIconId();
	}

	@Override
	public String getIconUrl() {
		return this.guild.getIconUrl();
	}

	@Override
	@NotNull
	public Set<String> getFeatures() {
		return this.guild.getFeatures();
	}

	@Override
	public String getSplashId() {
		return this.guild.getSplashId();
	}

	@NotNull
	@Deprecated
	@Override
	public RestAction<String> retrieveVanityUrl() {
		return this.guild.retrieveVanityUrl();
	}

	@Override
	public String getVanityCode() {
		return this.guild.getVanityCode();
	}

	@Override
	public String getDescription() {
		return this.guild.getDescription();
	}

	@NotNull
	@Override
	public Locale getLocale() {
		return this.guild.getLocale();
	}

	@Override
	public String getBannerId() {
		return this.guild.getBannerId();
	}

	@Override
	public String getBannerUrl() {
		return this.guild.getBannerUrl();
	}

	@Override
	@NotNull
	public Guild.BoostTier getBoostTier() {
		return this.guild.getBoostTier();
	}

	@Override
	public int getBoostCount() {
		return this.guild.getBoostCount();
	}

	@Override
	@NotNull
	@Deprecated
	public List<Member> getBoosters() {
		return this.guild.getBoosters();
	}

	public List<BlackMember> getBlackBoosters() {
		return BlackMember.from(this.guild.getBoosters());
	}

	@Override
	public int getMaxMembers() {
		return this.guild.getMaxMembers();
	}

	@Override
	public int getMaxPresences() {
		return this.guild.getMaxPresences();
	}

	@NotNull
	@Override
	public RestAction<MetaData> retrieveMetaData() {
		return this.guild.retrieveMetaData();
	}

	@Override
	public VoiceChannel getAfkChannel() {
		return this.guild.getAfkChannel();
	}

	@Override
	public TextChannel getSystemChannel() {
		return this.guild.getSystemChannel();
	}

	@Deprecated
	@Override
	public Member getOwner() {
		return this.guild.getOwner();
	}

	public BlackMember getBlackOwner() {
		return BlackMember.from(this.guild.getOwner());
	}

	@Override
	public long getOwnerIdLong() {
		return this.guild.getOwnerIdLong();
	}

	@Override
	@NotNull
	public String getOwnerId() {
		return this.guild.getOwnerId();
	}

	@Override
	@NotNull
	public Guild.Timeout getAfkTimeout() {
		return this.guild.getAfkTimeout();
	}

	@Override
	@NotNull
	public Region getRegion() {
		return this.guild.getRegion();
	}

	@NotNull
	@Override
	public String getRegionRaw() {
		return this.guild.getRegionRaw();
	}

	@Override
	public boolean isMember(@NotNull final User user) {
		return this.guild.isMember(user);
	}

	@Override
	@NotNull
	public BlackMember getSelfMember() {
		return BlackMember.from(this.guild.getSelfMember());
	}

	@NotNull
	public BlackUser getSelfUser() {
		return BlackUser.from(this.getJDA().getSelfUser());
	}

	@Override
	public BlackMember getMember(@NotNull final User user) {
		return BlackMember.from(this.guild.getMember(user));
	}

	@Override
	public BlackMember getMemberById(@NotNull final String userId) {
		return BlackMember.from(this.guild.getMemberById(userId));
	}

	@Override
	@NotNull
	@Deprecated
	public List<Member> getMembers() {
		return this.guild.getMembers();
	}

	@NotNull
	public List<BlackMember> getBlackMembers() {
		return BlackMember.from(this.guild.getMembers());
	}

	@NotNull
	@Override
	public MemberCacheView getMemberCache() {
		return this.guild.getMemberCache();
	}

	@Override
	public GuildChannel getGuildChannelById(@NotNull final String id) {
		return this.guild.getGuildChannelById(id);
	}

	@Override
	public Category getCategoryById(@NotNull final String id) {
		return this.guild.getCategoryById(id);
	}

	@Override
	@NotNull
	public List<Category> getCategories() {
		return this.guild.getCategories();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<Category> getCategoryCache() {
		return this.guild.getCategoryCache();
	}

	@Override
	public StoreChannel getStoreChannelById(@NotNull final String id) {
		return this.guild.getStoreChannelById(id);
	}

	@Override
	@NotNull
	public List<StoreChannel> getStoreChannels() {
		return this.guild.getStoreChannels();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache() {
		return this.guild.getStoreChannelCache();
	}

	@Override
	public TextChannel getTextChannelById(@NotNull final String id) {
		return this.guild.getTextChannelById(id);
	}

	@Override
	@NotNull
	public List<TextChannel> getTextChannels() {
		return this.guild.getTextChannels();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
		return this.guild.getTextChannelCache();
	}

	@Override
	public VoiceChannel getVoiceChannelById(@NotNull final String id) {
		return this.guild.getVoiceChannelById(id);
	}

	@Override
	@NotNull
	public List<VoiceChannel> getVoiceChannels() {
		return this.guild.getVoiceChannels();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
		return this.guild.getVoiceChannelCache();
	}

	@Override
	@NotNull
	public List<GuildChannel> getChannels() {
		return this.guild.getChannels();
	}

	@NotNull
	@Override
	public List<GuildChannel> getChannels(final boolean b) {
		return this.guild.getChannels(b);
	}

	@Override
	public Role getRoleById(@NotNull final String id) {
		return this.guild.getRoleById(id);
	}

	@Override
	@NotNull
	public List<Role> getRoles() {
		return this.guild.getRoles();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<Role> getRoleCache() {
		return this.guild.getRoleCache();
	}

	@Override
	public Emote getEmoteById(@NotNull final String id) {
		return this.guild.getEmoteById(id);
	}

	@Override
	@NotNull
	public List<Emote> getEmotes() {
		return this.guild.getEmotes();
	}

	@NotNull
	@Override
	public SnowflakeCacheView<Emote> getEmoteCache() {
		return this.guild.getEmoteCache();
	}

	@NotNull
	@Override
	public RestAction<List<ListedEmote>> retrieveEmotes() {
		return this.guild.retrieveEmotes();
	}

	@NotNull
	@Override
	public RestAction<ListedEmote> retrieveEmoteById(@NotNull final String s) {
		return this.guild.retrieveEmoteById(s);
	}

	@Override
	@NotNull
	public RestAction<List<Guild.Ban>> retrieveBanList() {
		return this.guild.retrieveBanList();
	}

	@Override
	@NotNull
	public RestAction<Guild.Ban> retrieveBanById(@NotNull final String userId) {
		return this.guild.retrieveBanById(userId);
	}

	@Override
	@NotNull
	public RestAction<Guild.Ban> retrieveBan(@NotNull final User bannedUser) {
		return this.guild.retrieveBan(bannedUser);
	}

	@NotNull
	@Override
	public RestAction<Integer> retrievePrunableMemberCount(final int i) {
		return this.guild.retrievePrunableMemberCount(i);
	}

	@Override
	@NotNull
	public Role getPublicRole() {
		return this.guild.getPublicRole();
	}

	@Override
	public TextChannel getDefaultChannel() {
		return this.guild.getDefaultChannel();
	}

	@Override
	@NotNull
	public GuildManager getManager() {
		return this.guild.getManager();
	}

	@Override
	@NotNull
	public AuditLogPaginationAction retrieveAuditLogs() {
		return this.guild.retrieveAuditLogs();
	}

	@Override
	@NotNull
	public RestAction<Void> leave() {
		return this.guild.leave();
	}

	@NotNull
	@Override
	public RestAction<Void> delete() {
		return this.guild.delete();
	}

	@NotNull
	@Override
	public RestAction<Void> delete(final String s) {
		return this.guild.delete(s);
	}

	@Override
	@NotNull
	public AudioManager getAudioManager() {
		return this.guild.getAudioManager();
	}

	@Override
	@NotNull
	public JDA getJDA() {
		return this.guild.getJDA();
	}

	@Override
	@NotNull
	public RestAction<List<Invite>> retrieveInvites() {
		return this.guild.retrieveInvites();
	}

	@Override
	@NotNull
	public RestAction<List<Webhook>> retrieveWebhooks() {
		return this.guild.retrieveWebhooks();
	}

	@Override
	@NotNull
	public List<GuildVoiceState> getVoiceStates() {
		return this.guild.getVoiceStates();
	}

	@NotNull
	@Override
	public VerificationLevel getVerificationLevel() {
		return this.guild.getVerificationLevel();
	}

	@NotNull
	@Override
	public NotificationLevel getDefaultNotificationLevel() {
		return this.guild.getDefaultNotificationLevel();
	}

	@NotNull
	@Override
	public MFALevel getRequiredMFALevel() {
		return this.guild.getRequiredMFALevel();
	}

	@NotNull
	@Override
	public ExplicitContentLevel getExplicitContentLevel() {
		return this.guild.getExplicitContentLevel();
	}

	@Deprecated
	@Override
	public boolean checkVerification() {
		return this.guild.checkVerification();
	}

	@Deprecated
	@Override
	public boolean isAvailable() {
		return this.guild.isAvailable();
	}

	@Deprecated
	@NotNull
	@Override
	public CompletableFuture<Void> retrieveMembers() {
		return this.guild.retrieveMembers();
	}

	@NotNull
	@Override
	public Task<Void> loadMembers(@NotNull final Consumer<Member> consumer) {
		return this.guild.loadMembers(consumer);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveMemberById(final long l, final boolean b) {
		return this.guild.retrieveMemberById(l, b);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByIds(final boolean b, @NotNull final long... longs) {
		return this.guild.retrieveMembersByIds(b, longs);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByPrefix(@NotNull final String s, final int i) {
		return this.guild.retrieveMembersByPrefix(s, i);
	}

	@Override
	@NotNull
	public RestAction<Void> moveVoiceMember(@NotNull final Member member, final VoiceChannel voiceChannel) {
		return this.guild.moveVoiceMember(member, voiceChannel);
	}

	@Override
	@NotNull
	public RestAction<Void> kickVoiceMember(@NotNull final Member member) {
		return this.guild.kickVoiceMember(member);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> modifyNickname(@NotNull final Member member, final String nickname) {
		return this.guild.modifyNickname(member, nickname);
	}

	@NotNull
	@Override
	public AuditableRestAction<Integer> prune(final int i, final boolean b, @NotNull final Role... roles) {
		return this.guild.prune(i, b, roles);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> kick(@NotNull final Member member, final String reason) {
		return this.guild.kick(member, reason);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> kick(@NotNull final String userId, final String reason) {
		return this.guild.kick(userId, reason);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> kick(@NotNull final Member member) {
		return this.guild.kick(member);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> kick(@NotNull final String userId) {
		return this.guild.kick(userId);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> ban(@NotNull final User user, final int delDays, final String reason) {
		return this.guild.ban(user, delDays, reason);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> ban(@NotNull final String userId, final int delDays, final String reason) {
		return this.guild.ban(userId, delDays, reason);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> ban(@NotNull final Member member, final int delDays, final String reason) {
		return this.guild.ban(member, delDays, reason);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> ban(@NotNull final Member member, final int delDays) {
		return this.guild.ban(member, delDays);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> ban(@NotNull final User user, final int delDays) {
		return this.guild.ban(user, delDays);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> ban(@NotNull final String userId, final int delDays) {
		return this.guild.ban(userId, delDays);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> unban(@NotNull final User user) {
		return this.guild.unban(user);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> unban(@NotNull final String userId) {
		return this.guild.unban(userId);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> deafen(@NotNull final Member member, final boolean b) {
		return this.guild.deafen(member, b);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> mute(@NotNull final Member member, final boolean b) {
		return this.guild.mute(member, b);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> addRoleToMember(@NotNull final Member member, @NotNull final Role role) {
		return this.guild.addRoleToMember(member, role);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> addRoleToMember(@NotNull final String userId, @NotNull final Role role) {
		return this.guild.addRoleToMember(userId, role);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> removeRoleFromMember(@NotNull final Member member, @NotNull final Role role) {
		return this.guild.removeRoleFromMember(member, role);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> removeRoleFromMember(@NotNull final String userId, @NotNull final Role role) {
		return this.guild.removeRoleFromMember(userId, role);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull final Member member, final Collection<Role> rolesToAdd,
			final Collection<Role> rolesToRemove) {
		return this.guild.modifyMemberRoles(member, rolesToAdd, rolesToRemove);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull final Member member, @NotNull final Role... roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@Override
	@NotNull
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull final Member member,
			@NotNull final Collection<Role> roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> transferOwnership(@NotNull final Member member) {
		return this.guild.transferOwnership(member);
	}

	@Override
	@NotNull
	public ChannelAction<TextChannel> createTextChannel(@NotNull final String name) {
		return this.guild.createTextChannel(name);
	}

	@Override
	@NotNull
	public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull final String name) {
		return this.guild.createVoiceChannel(name);
	}

	@Override
	@NotNull
	public ChannelAction<Category> createCategory(@NotNull final String name) {
		return this.guild.createCategory(name);
	}

	@Override
	@NotNull
	public <T extends GuildChannel> ChannelAction<T> createCopyOfChannel(@NotNull final T channel) {
		return this.guild.createCopyOfChannel(channel);
	}

	@Override
	@NotNull
	public RoleAction createRole() {
		return this.guild.createRole();
	}

	@Override
	@NotNull
	public RoleAction createCopyOfRole(@NotNull final Role role) {
		return this.guild.createCopyOfRole(role);
	}

	@Override
	@NotNull
	public AuditableRestAction<Emote> createEmote(@NotNull final String name, @NotNull final Icon icon,
			@NotNull final Role... roles) {
		return this.guild.createEmote(name, icon, roles);
	}

	@Override
	@NotNull
	public ChannelOrderAction modifyCategoryPositions() {
		return this.guild.modifyCategoryPositions();
	}

	@Override
	@NotNull
	public ChannelOrderAction modifyTextChannelPositions() {
		return this.guild.modifyTextChannelPositions();
	}

	@Override
	@NotNull
	public ChannelOrderAction modifyVoiceChannelPositions() {
		return this.guild.modifyVoiceChannelPositions();
	}

	@Override
	@NotNull
	public RoleOrderAction modifyRolePositions() {
		return this.guild.modifyRolePositions();
	}

	@Override
	@NotNull
	public RoleOrderAction modifyRolePositions(final boolean useAscendingOrder) {
		return this.guild.modifyRolePositions(useAscendingOrder);
	}

	@Override
	@NotNull
	public String getId() {
		return this.guild.getId();
	}

	@Override
	public long getIdLong() {
		return this.guild.getIdLong();
	}

	@Override
	@NotNull
	public OffsetDateTime getTimeCreated() {
		return this.guild.getTimeCreated();
	}

	@Override
	public RestAction<VanityInvite> retrieveVanityInvite() {
		return this.guild.retrieveVanityInvite();
	}

	@Override
	public TextChannel getRulesChannel() {
		return this.guild.getRulesChannel();
	}

	@Override
	public TextChannel getCommunityUpdatesChannel() {
		return this.guild.getCommunityUpdatesChannel();
	}

	@Override
	public ChannelAction<TextChannel> createTextChannel(String name, Category parent) {
		return this.guild.createTextChannel(name, parent);
	}

	@Override
	public ChannelAction<VoiceChannel> createVoiceChannel(String name, Category parent) {
		return this.guild.createVoiceChannel(name, parent);
	}

	@Override
	public CategoryOrderAction modifyTextChannelPositions(Category category) {
		return this.guild.modifyTextChannelPositions(category);
	}

	@Override
	public CategoryOrderAction modifyVoiceChannelPositions(Category category) {
		return this.guild.modifyVoiceChannelPositions(category);
	}

	@Override
	public String toString() {
		return "BlackGuild [guild=" + guild + ", language=" + language + ", isPremium=" + isPremium
				+ ", antiSpoilerType=" + antiSpoilerType + ", antiSwearType=" + antiSwearType + ", prefix=" + prefix
				+ ", joinMessage=" + joinMessage + ", joinChannel=" + joinChannel + ", leaveMessage=" + leaveMessage
				+ ", leaveChannel=" + leaveChannel + ", disabledCommands=" + disabledCommands + "]";
	}
}