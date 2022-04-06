package com.github.black0nion.blackonionbot.wrappers.jda.impls;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class GuildImpl extends BlackWrapper implements Guild {

	protected final Guild guild;

	protected GuildImpl(Guild guild) {
		this.guild = guild;
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<Command>> retrieveCommands() {
		return this.guild.retrieveCommands();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<net.dv8tion.jda.api.interactions.commands.Command> retrieveCommandById(@NotNull String id) {
		return this.guild.retrieveCommandById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<net.dv8tion.jda.api.interactions.commands.Command> retrieveCommandById(long id) {
		return this.guild.retrieveCommandById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<net.dv8tion.jda.api.interactions.commands.Command> upsertCommand(@NotNull CommandData command) {
		return this.guild.upsertCommand(command);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CommandCreateAction upsertCommand(@NotNull String name, @NotNull String description) {
		return this.guild.upsertCommand(name, description);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CommandListUpdateAction updateCommands() {
		return this.guild.updateCommands();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CommandEditAction editCommandById(@NotNull String id) {
		return this.guild.editCommandById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CommandEditAction editCommandById(long id) {
		return this.guild.editCommandById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> deleteCommandById(@NotNull String commandId) {
		return this.guild.deleteCommandById(commandId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> deleteCommandById(long commandId) {
		return this.guild.deleteCommandById(commandId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(@NotNull String commandId) {
		return this.guild.retrieveCommandPrivilegesById(commandId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(long commandId) {
		return this.guild.retrieveCommandPrivilegesById(commandId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Map<String, List<CommandPrivilege>>> retrieveCommandPrivileges() {
		return this.guild.retrieveCommandPrivileges();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id,
			@NotNull Collection<? extends CommandPrivilege> privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id,
			@NotNull CommandPrivilege @NotNull... privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id,
			@NotNull Collection<? extends CommandPrivilege> privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id,
			@NotNull CommandPrivilege @NotNull... privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Map<String, List<CommandPrivilege>>> updateCommandPrivileges(
			@NotNull Map<String, ? extends Collection<CommandPrivilege>> privileges) {
		return this.guild.updateCommandPrivileges(privileges);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<EnumSet<Region>> retrieveRegions() {
		return this.guild.retrieveRegions();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<EnumSet<Region>> retrieveRegions(boolean includeDeprecated) {
		return this.guild.retrieveRegions(includeDeprecated);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public MemberAction addMember(@NotNull String accessToken, @NotNull String userId) {
		return this.guild.addMember(accessToken, userId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public MemberAction addMember(@NotNull String accessToken, @NotNull User user) {
		return this.guild.addMember(accessToken, user);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public MemberAction addMember(@NotNull String accessToken, long userId) {
		return this.guild.addMember(accessToken, userId);
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
	public boolean unloadMember(long userId) {
		return this.guild.unloadMember(userId);
	}

	@Override
	public int getMemberCount() {
		return this.guild.getMemberCount();
	}

	@Override
	@Nonnull
	public String getName() {
		return this.guild.getName();
	}

	@Override
	@Nullable
	public String getIconId() {
		return this.guild.getIconId();
	}

	@Override
	@Nullable
	public String getIconUrl() {
		return this.guild.getIconUrl();
	}

	@Override
	@Nonnull
	public Set<String> getFeatures() {
		return this.guild.getFeatures();
	}

	@Override
	@Nullable
	public String getSplashId() {
		return this.guild.getSplashId();
	}

	@Override
	@Nullable
	public String getSplashUrl() {
		return this.guild.getSplashUrl();
	}

	@Override
	@Nullable
	public String getVanityCode() {
		return this.guild.getVanityCode();
	}

	@Override
	@Nullable
	public String getVanityUrl() {
		return this.guild.getVanityUrl();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<VanityInvite> retrieveVanityInvite() {
		return this.guild.retrieveVanityInvite();
	}

	@Override
	@Nullable
	public String getDescription() {
		return this.guild.getDescription();
	}

	@Override
	@Nonnull
	public Locale getLocale() {
		return this.guild.getLocale();
	}

	@Override
	@Nullable
	public String getBannerId() {
		return this.guild.getBannerId();
	}

	@Override
	@Nullable
	public String getBannerUrl() {
		return this.guild.getBannerUrl();
	}

	@Override
	@Nonnull
	public BoostTier getBoostTier() {
		return this.guild.getBoostTier();
	}

	@Override
	public int getBoostCount() {
		return this.guild.getBoostCount();
	}

	@Override
	@Nonnull
	public List<Member> getBoosters() {
		return this.guild.getBoosters();
	}

	@Override
	public int getMaxBitrate() {
		return this.guild.getMaxBitrate();
	}

	@Override
	public long getMaxFileSize() {
		return this.guild.getMaxFileSize();
	}

	@Override
	public int getMaxEmotes() {
		return this.guild.getMaxEmotes();
	}

	@Override
	public int getMaxMembers() {
		return this.guild.getMaxMembers();
	}

	@Override
	public int getMaxPresences() {
		return this.guild.getMaxPresences();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<MetaData> retrieveMetaData() {
		return this.guild.retrieveMetaData();
	}

	@Override
	@Nullable
	public VoiceChannel getAfkChannel() {
		return this.guild.getAfkChannel();
	}

	@Override
	@Nullable
	public TextChannel getSystemChannel() {
		return this.guild.getSystemChannel();
	}

	@Override
	@Nullable
	public TextChannel getRulesChannel() {
		return this.guild.getRulesChannel();
	}

	@Override
	@Nullable
	public TextChannel getCommunityUpdatesChannel() {
		return this.guild.getCommunityUpdatesChannel();
	}

	@Override
	@Nullable
	public Member getOwner() {
		return this.guild.getOwner();
	}

	@Override
	public long getOwnerIdLong() {
		return this.guild.getOwnerIdLong();
	}

	@Override
	@Nonnull
	public String getOwnerId() {
		return this.guild.getOwnerId();
	}

	@Override
	@Nonnull
	public Timeout getAfkTimeout() {
		return this.guild.getAfkTimeout();
	}

	@Override
	public boolean isMember(@NotNull User user) {
		return this.guild.isMember(user);
	}

	@Override
	@Nonnull
	public Member getSelfMember() {
		return this.guild.getSelfMember();
	}

	@Override
	@Nonnull
	public NSFWLevel getNSFWLevel() {
		return this.guild.getNSFWLevel();
	}

	@Override
	@Nullable
	public Member getMember(@NotNull User user) {
		return this.guild.getMember(user);
	}

	@Override
	@Nullable
	public Member getMemberById(@NotNull String userId) {
		return this.guild.getMemberById(userId);
	}

	@Override
	@Nullable
	public Member getMemberById(long userId) {
		return this.guild.getMemberById(userId);
	}

	@Override
	@Nullable
	public Member getMemberByTag(@NotNull String tag) {
		return this.guild.getMemberByTag(tag);
	}

	@Override
	@Nullable
	public Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {
		return this.guild.getMemberByTag(username, discriminator);
	}

	@Override
	@Nonnull
	public List<Member> getMembers() {
		return this.guild.getMembers();
	}

	@Override
	@Nonnull
	public List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getMembersByName(name, ignoreCase);
	}

	@Override
	@Nonnull
	public List<Member> getMembersByNickname(@org.jetbrains.annotations.Nullable String nickname, boolean ignoreCase) {
		return this.guild.getMembersByNickname(nickname, ignoreCase);
	}

	@Override
	@Nonnull
	public List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getMembersByEffectiveName(name, ignoreCase);
	}

	@Override
	@Nonnull
	public List<Member> getMembersWithRoles(@NotNull Role @NotNull... roles) {
		return this.guild.getMembersWithRoles(roles);
	}

	@Override
	@Nonnull
	public List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {
		return this.guild.getMembersWithRoles(roles);
	}

	@Override
	@Nonnull
	public MemberCacheView getMemberCache() {
		return this.guild.getMemberCache();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<StageChannel> getStageChannelCache() {
		return this.guild.getStageChannelCache();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
		return this.guild.getThreadChannelCache();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<Category> getCategoryCache() {
		return this.guild.getCategoryCache();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
		return this.guild.getTextChannelCache();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<NewsChannel> getNewsChannelCache() {
		return this.guild.getNewsChannelCache();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
		return this.guild.getVoiceChannelCache();
	}

	@Override
	@Nonnull
	public List<GuildChannel> getChannels() {
		return this.guild.getChannels();
	}

	@Override
	@Nonnull
	public List<GuildChannel> getChannels(boolean includeHidden) {
		return this.guild.getChannels(includeHidden);
	}

	@Override
	@Nullable
	public Role getRoleById(@NotNull String id) {
		return this.guild.getRoleById(id);
	}

	@Override
	@Nullable
	public Role getRoleById(long id) {
		return this.guild.getRoleById(id);
	}

	@Override
	@Nonnull
	public List<Role> getRoles() {
		return this.guild.getRoles();
	}

	@Override
	@Nonnull
	public List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getRolesByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public Role getRoleByBot(long userId) {
		return this.guild.getRoleByBot(userId);
	}

	@Override
	@Nullable
	public Role getRoleByBot(@NotNull String userId) {
		return this.guild.getRoleByBot(userId);
	}

	@Override
	@Nullable
	public Role getRoleByBot(@NotNull User user) {
		return this.guild.getRoleByBot(user);
	}

	@Override
	@Nullable
	public Role getBotRole() {
		return this.guild.getBotRole();
	}

	@Override
	@Nullable
	public Role getBoostRole() {
		return this.guild.getBoostRole();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<Role> getRoleCache() {
		return this.guild.getRoleCache();
	}

	@Override
	@Nullable
	public Emote getEmoteById(@NotNull String id) {
		return this.guild.getEmoteById(id);
	}

	@Override
	@Nullable
	public Emote getEmoteById(long id) {
		return this.guild.getEmoteById(id);
	}

	@Override
	@Nonnull
	public List<Emote> getEmotes() {
		return this.guild.getEmotes();
	}

	@Override
	@Nonnull
	public List<Emote> getEmotesByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getEmotesByName(name, ignoreCase);
	}

	@Override
	@Nonnull
	public SnowflakeCacheView<Emote> getEmoteCache() {
		return this.guild.getEmoteCache();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<ListedEmote>> retrieveEmotes() {
		return this.guild.retrieveEmotes();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<ListedEmote> retrieveEmoteById(@NotNull String id) {
		return this.guild.retrieveEmoteById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<ListedEmote> retrieveEmoteById(long id) {
		return this.guild.retrieveEmoteById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<ListedEmote> retrieveEmote(@NotNull Emote emote) {
		return this.guild.retrieveEmote(emote);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<Ban>> retrieveBanList() {
		return this.guild.retrieveBanList();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Ban> retrieveBanById(long userId) {
		return this.guild.retrieveBanById(userId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Ban> retrieveBanById(@NotNull String userId) {
		return this.guild.retrieveBanById(userId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Ban> retrieveBan(@NotNull User bannedUser) {
		return this.guild.retrieveBan(bannedUser);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Integer> retrievePrunableMemberCount(int days) {
		return this.guild.retrievePrunableMemberCount(days);
	}

	@Override
	@Nonnull
	public Role getPublicRole() {
		return this.guild.getPublicRole();
	}

	@Override
	@Nullable
	public BaseGuildMessageChannel getDefaultChannel() {
		return this.guild.getDefaultChannel();
	}

	@Override
	@Nonnull
	public GuildManager getManager() {
		return this.guild.getManager();
	}

	@Override
	public boolean isBoostProgressBarEnabled() {
		return this.guild.isBoostProgressBarEnabled();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditLogPaginationAction retrieveAuditLogs() {
		return this.guild.retrieveAuditLogs();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> leave() {
		return this.guild.leave();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> delete() {
		return this.guild.delete();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> delete(@org.jetbrains.annotations.Nullable String mfaCode) {
		return this.guild.delete(mfaCode);
	}

	@Override
	@Nonnull
	public AudioManager getAudioManager() {
		return this.guild.getAudioManager();
	}

	@Override
	@Nonnull
	public Task<Void> requestToSpeak() {
		return this.guild.requestToSpeak();
	}

	@Override
	@Nonnull
	public Task<Void> cancelRequestToSpeak() {
		return this.guild.cancelRequestToSpeak();
	}

	@Override
	@Nonnull
	public JDA getJDA() {
		return this.guild.getJDA();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<Invite>> retrieveInvites() {
		return this.guild.retrieveInvites();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<Template>> retrieveTemplates() {
		return this.guild.retrieveTemplates();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Template> createTemplate(@NotNull String name,
			@org.jetbrains.annotations.Nullable String description) {
		return this.guild.createTemplate(name, description);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<Webhook>> retrieveWebhooks() {
		return this.guild.retrieveWebhooks();
	}

	@Override
	@Nonnull
	public List<GuildVoiceState> getVoiceStates() {
		return this.guild.getVoiceStates();
	}

	@Override
	@Nonnull
	public VerificationLevel getVerificationLevel() {
		return this.guild.getVerificationLevel();
	}

	@Override
	@Nonnull
	public NotificationLevel getDefaultNotificationLevel() {
		return this.guild.getDefaultNotificationLevel();
	}

	@Override
	@Nonnull
	public MFALevel getRequiredMFALevel() {
		return this.guild.getRequiredMFALevel();
	}

	@Override
	@Nonnull
	public ExplicitContentLevel getExplicitContentLevel() {
		return this.guild.getExplicitContentLevel();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> loadMembers() {
		return this.guild.loadMembers();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {
		return this.guild.findMembers(filter);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {
		return this.guild.findMembersWithRoles(roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> findMembersWithRoles(@NotNull Role @NotNull... roles) {
		return this.guild.findMembersWithRoles(roles);
	}

	@Override
	@Nonnull
	public Task<Void> loadMembers(@NotNull Consumer<Member> callback) {
		return this.guild.loadMembers(callback);
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveMember(@NotNull User user) {
		return this.guild.retrieveMember(user);
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveMemberById(@NotNull String id) {
		return this.guild.retrieveMemberById(id);
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveMemberById(long id) {
		return this.guild.retrieveMemberById(id);
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveOwner() {
		return this.guild.retrieveOwner();
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveMember(@NotNull User user, boolean update) {
		return this.guild.retrieveMember(user, update);
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveMemberById(@NotNull String id, boolean update) {
		return this.guild.retrieveMemberById(id, update);
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveMemberById(long id, boolean update) {
		return this.guild.retrieveMemberById(id, update);
	}

	@Override
	@Nonnull
	public RestAction<Member> retrieveOwner(boolean update) {
		return this.guild.retrieveOwner(update);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembers(@NotNull Collection<User> users) {
		return this.guild.retrieveMembers(users);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(@NotNull String @NotNull... ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(long @NotNull... ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembers(boolean includePresence, @NotNull Collection<User> users) {
		return this.guild.retrieveMembers(includePresence, users);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull Collection<Long> ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull String @NotNull... ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, long @NotNull... ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByPrefix(@NotNull String prefix, int limit) {
		return this.guild.retrieveMembersByPrefix(prefix, limit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<ThreadChannel>> retrieveActiveThreads() {
		return this.guild.retrieveActiveThreads();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> moveVoiceMember(@NotNull Member member,
			@org.jetbrains.annotations.Nullable AudioChannel audioChannel) {
		return this.guild.moveVoiceMember(member, audioChannel);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> kickVoiceMember(@NotNull Member member) {
		return this.guild.kickVoiceMember(member);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyNickname(@NotNull Member member,
			@org.jetbrains.annotations.Nullable String nickname) {
		return this.guild.modifyNickname(member, nickname);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Integer> prune(int days, @NotNull Role @NotNull... roles) {
		return this.guild.prune(days, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Integer> prune(int days, boolean wait, @NotNull Role @NotNull... roles) {
		return this.guild.prune(days, wait, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick(@NotNull Member member, @org.jetbrains.annotations.Nullable String reason) {
		return this.guild.kick(member, reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick(@NotNull String userId, @org.jetbrains.annotations.Nullable String reason) {
		return this.guild.kick(userId, reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick(@NotNull Member member) {
		return this.guild.kick(member);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick(@NotNull String userId) {
		return this.guild.kick(userId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(@NotNull User user, int delDays,
			@org.jetbrains.annotations.Nullable String reason) {
		return this.guild.ban(user, delDays, reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(@NotNull String userId, int delDays,
			@org.jetbrains.annotations.Nullable String reason) {
		return this.guild.ban(userId, delDays, reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(@NotNull Member member, int delDays,
			@org.jetbrains.annotations.Nullable String reason) {
		return this.guild.ban(member, delDays, reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(@NotNull Member member, int delDays) {
		return this.guild.ban(member, delDays);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(@NotNull User user, int delDays) {
		return this.guild.ban(user, delDays);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> ban(@NotNull String userId, int delDays) {
		return this.guild.ban(userId, delDays);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> unban(@NotNull User user) {
		return this.guild.unban(user);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> unban(@NotNull String userId) {
		return this.guild.unban(userId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(@NotNull Member member, long amount, @NotNull TimeUnit unit) {
		return this.guild.timeoutFor(member, amount, unit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(@NotNull Member member, @NotNull Duration duration) {
		return this.guild.timeoutFor(member, duration);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutUntil(@NotNull Member member, @NotNull TemporalAccessor temporal) {
		return this.guild.timeoutUntil(member, temporal);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutForById(long userId, long amount, @NotNull TimeUnit unit) {
		return this.guild.timeoutForById(userId, amount, unit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutForById(@NotNull String userId, long amount, @NotNull TimeUnit unit) {
		return this.guild.timeoutForById(userId, amount, unit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutForById(long userId, @NotNull Duration duration) {
		return this.guild.timeoutForById(userId, duration);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutForById(@NotNull String userId, @NotNull Duration duration) {
		return this.guild.timeoutForById(userId, duration);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutUntilById(long userId, @NotNull TemporalAccessor temporal) {
		return this.guild.timeoutUntilById(userId, temporal);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutUntilById(@NotNull String userId, @NotNull TemporalAccessor temporal) {
		return this.guild.timeoutUntilById(userId, temporal);
	}

	@Override
	@Nonnull
	public AuditableRestAction<Void> removeTimeout(@NotNull Member member) {
		return this.guild.removeTimeout(member);
	}

	@Override
	@Nonnull
	public AuditableRestAction<Void> removeTimeoutById(long userId) {
		return this.guild.removeTimeoutById(userId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> removeTimeoutById(@NotNull String userId) {
		return this.guild.removeTimeoutById(userId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> deafen(@NotNull Member member, boolean deafen) {
		return this.guild.deafen(member, deafen);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> mute(@NotNull Member member, boolean mute) {
		return this.guild.mute(member, mute);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> addRoleToMember(@NotNull Member member, @NotNull Role role) {
		return this.guild.addRoleToMember(member, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> addRoleToMember(long userId, @NotNull Role role) {
		return this.guild.addRoleToMember(userId, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> addRoleToMember(@NotNull String userId, @NotNull Role role) {
		return this.guild.addRoleToMember(userId, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> removeRoleFromMember(@NotNull Member member, @NotNull Role role) {
		return this.guild.removeRoleFromMember(member, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> removeRoleFromMember(long userId, @NotNull Role role) {
		return this.guild.removeRoleFromMember(userId, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> removeRoleFromMember(@NotNull String userId, @NotNull Role role) {
		return this.guild.removeRoleFromMember(userId, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
			@org.jetbrains.annotations.Nullable Collection<Role> rolesToAdd,
			@org.jetbrains.annotations.Nullable Collection<Role> rolesToRemove) {
		return this.guild.modifyMemberRoles(member, rolesToAdd, rolesToRemove);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Role @NotNull... roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Collection<Role> roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> transferOwnership(@NotNull Member newOwner) {
		return this.guild.transferOwnership(newOwner);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<TextChannel> createTextChannel(@NotNull String name) {
		return this.guild.createTextChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<TextChannel> createTextChannel(@NotNull String name,
			@org.jetbrains.annotations.Nullable Category parent) {
		return this.guild.createTextChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<NewsChannel> createNewsChannel(@NotNull String name) {
		return this.guild.createNewsChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<NewsChannel> createNewsChannel(@NotNull String name,
			@org.jetbrains.annotations.Nullable Category parent) {
		return this.guild.createNewsChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name) {
		return this.guild.createVoiceChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name,
			@org.jetbrains.annotations.Nullable Category parent) {
		return this.guild.createVoiceChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<StageChannel> createStageChannel(@NotNull String name) {
		return this.guild.createStageChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<StageChannel> createStageChannel(@NotNull String name,
			@org.jetbrains.annotations.Nullable Category parent) {
		return this.guild.createStageChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<Category> createCategory(@NotNull String name) {
		return this.guild.createCategory(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public <T extends ICopyableChannel> ChannelAction<T> createCopyOfChannel(@NotNull T channel) {
		return this.guild.createCopyOfChannel(channel);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RoleAction createRole() {
		return this.guild.createRole();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RoleAction createCopyOfRole(@NotNull Role role) {
		return this.guild.createCopyOfRole(role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Emote> createEmote(@NotNull String name, @NotNull Icon icon,
			@NotNull Role @NotNull... roles) {
		return this.guild.createEmote(name, icon, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelOrderAction modifyCategoryPositions() {
		return this.guild.modifyCategoryPositions();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelOrderAction modifyTextChannelPositions() {
		return this.guild.modifyTextChannelPositions();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelOrderAction modifyVoiceChannelPositions() {
		return this.guild.modifyVoiceChannelPositions();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
		return this.guild.modifyTextChannelPositions(category);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
		return this.guild.modifyVoiceChannelPositions(category);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RoleOrderAction modifyRolePositions() {
		return this.guild.modifyRolePositions();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RoleOrderAction modifyRolePositions(boolean useAscendingOrder) {
		return this.guild.modifyRolePositions(useAscendingOrder);
	}

	@Override
	@Nullable
	public <T extends Channel> T getChannelById(@NotNull Class<T> type, @NotNull String id) {
		return this.guild.getChannelById(type, id);
	}

	@Override
	@Nullable
	public <T extends Channel> T getChannelById(@NotNull Class<T> type, long id) {
		return this.guild.getChannelById(type, id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(@NotNull String id) {
		return this.guild.getGuildChannelById(id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(long id) {
		return this.guild.getGuildChannelById(id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {
		return this.guild.getGuildChannelById(type, id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {
		return this.guild.getGuildChannelById(type, id);
	}

	@Override
	@Nonnull
	public List<StageChannel> getStageChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getStageChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public StageChannel getStageChannelById(@NotNull String id) {
		return this.guild.getStageChannelById(id);
	}

	@Override
	@Nullable
	public StageChannel getStageChannelById(long id) {
		return this.guild.getStageChannelById(id);
	}

	@Override
	@Nonnull
	public List<StageChannel> getStageChannels() {
		return this.guild.getStageChannels();
	}

	@Override
	@Nonnull
	public List<ThreadChannel> getThreadChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getThreadChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public ThreadChannel getThreadChannelById(@NotNull String id) {
		return this.guild.getThreadChannelById(id);
	}

	@Override
	@Nullable
	public ThreadChannel getThreadChannelById(long id) {
		return this.guild.getThreadChannelById(id);
	}

	@Override
	@Nonnull
	public List<ThreadChannel> getThreadChannels() {
		return this.guild.getThreadChannels();
	}

	@Override
	@Nonnull
	public List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getCategoriesByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public Category getCategoryById(@NotNull String id) {
		return this.guild.getCategoryById(id);
	}

	@Override
	@Nullable
	public Category getCategoryById(long id) {
		return this.guild.getCategoryById(id);
	}

	@Override
	@Nonnull
	public List<Category> getCategories() {
		return this.guild.getCategories();
	}

	@Override
	@Nonnull
	public List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getTextChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public TextChannel getTextChannelById(@NotNull String id) {
		return this.guild.getTextChannelById(id);
	}

	@Override
	@Nullable
	public TextChannel getTextChannelById(long id) {
		return this.guild.getTextChannelById(id);
	}

	@Override
	@Nonnull
	public List<TextChannel> getTextChannels() {
		return this.guild.getTextChannels();
	}

	@Override
	@Nonnull
	public List<NewsChannel> getNewsChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getNewsChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public NewsChannel getNewsChannelById(@NotNull String id) {
		return this.guild.getNewsChannelById(id);
	}

	@Override
	@Nullable
	public NewsChannel getNewsChannelById(long id) {
		return this.guild.getNewsChannelById(id);
	}

	@Override
	@Nonnull
	public List<NewsChannel> getNewsChannels() {
		return this.guild.getNewsChannels();
	}

	@Override
	@Nonnull
	public List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getVoiceChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public VoiceChannel getVoiceChannelById(@NotNull String id) {
		return this.guild.getVoiceChannelById(id);
	}

	@Override
	@Nullable
	public VoiceChannel getVoiceChannelById(long id) {
		return this.guild.getVoiceChannelById(id);
	}

	@Override
	@Nonnull
	public List<VoiceChannel> getVoiceChannels() {
		return this.guild.getVoiceChannels();
	}

	@Override
	public long getIdLong() {
		return this.guild.getIdLong();
	}
}
