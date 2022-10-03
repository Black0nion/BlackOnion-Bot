package com.github.black0nion.blackonionbot.wrappers.jda.impls;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.PrivilegeConfig;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.managers.GuildStickerManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.OffsetDateTime;
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
    public RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
		return this.guild.retrieveCommands(withLocalizations);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Command> retrieveCommandById(@Nonnull String id) {
		return this.guild.retrieveCommandById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Command> retrieveCommandById(long id) {
		return this.guild.retrieveCommandById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Command> upsertCommand(@Nonnull CommandData command) {
		return this.guild.upsertCommand(command);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CommandCreateAction upsertCommand(@Nonnull String name, @Nonnull String description) {
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
	public CommandEditAction editCommandById(@Nonnull String id) {
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
	public RestAction<Void> deleteCommandById(@Nonnull String commandId) {
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
	public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(@Nonnull String targetId) {
		return this.guild.retrieveIntegrationPrivilegesById(targetId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(long targetId) {
		return this.guild.retrieveIntegrationPrivilegesById(targetId);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<PrivilegeConfig> retrieveCommandPrivileges() {
		return this.guild.retrieveCommandPrivileges();
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
	public MemberAction addMember(@Nonnull String accessToken, @Nonnull UserSnowflake user) {
		return this.guild.addMember(accessToken, user);
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
	@Nullable
	public ImageProxy getIcon() {
		return this.guild.getIcon();
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
	public ImageProxy getSplash() {
		return this.guild.getSplash();
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
	public DiscordLocale getLocale() {
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
	@Nullable
	public ImageProxy getBanner() {
		return this.guild.getBanner();
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
	public int getMaxEmojis() {
		return this.guild.getMaxEmojis();
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
	public boolean isMember(@Nonnull UserSnowflake user) {
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
	public Member getMember(@Nonnull UserSnowflake user) {
		return this.guild.getMember(user);
	}

	@Override
	@Nullable
	public Member getMemberById(@Nonnull String userId) {
		return this.guild.getMemberById(userId);
	}

	@Override
	@Nullable
	public Member getMemberById(long userId) {
		return this.guild.getMemberById(userId);
	}

	@Override
	@Nullable
	public Member getMemberByTag(@Nonnull String tag) {
		return this.guild.getMemberByTag(tag);
	}

	@Override
	@Nullable
	public Member getMemberByTag(@Nonnull String username, @Nonnull String discriminator) {
		return this.guild.getMemberByTag(username, discriminator);
	}

	@Override
	@Nonnull
	public List<Member> getMembers() {
		return this.guild.getMembers();
	}

	@Override
	@Nonnull
	public List<Member> getMembersByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getMembersByName(name, ignoreCase);
	}

	@Override
	@Nonnull
	public List<Member> getMembersByNickname(@Nullable String nickname, boolean ignoreCase) {
		return this.guild.getMembersByNickname(nickname, ignoreCase);
	}

	@Override
	@Nonnull
	public List<Member> getMembersByEffectiveName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getMembersByEffectiveName(name, ignoreCase);
	}

	@Override
	@Nonnull
	public List<Member> getMembersWithRoles(@Nonnull Role... roles) {
		return this.guild.getMembersWithRoles(roles);
	}

	@Override
	@Nonnull
	public List<Member> getMembersWithRoles(@Nonnull Collection<Role> roles) {
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
	public Role getRoleById(@Nonnull String id) {
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
	public List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getRolesByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public Role getRoleByBot(long userId) {
		return this.guild.getRoleByBot(userId);
	}

	@Override
	@Nullable
	public Role getRoleByBot(@Nonnull String userId) {
		return this.guild.getRoleByBot(userId);
	}

	@Override
	@Nullable
	public Role getRoleByBot(@Nonnull User user) {
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
	public RichCustomEmoji getEmojiById(@Nonnull String id) {
		return this.guild.getEmojiById(id);
	}

	@Override
	@Nullable
	public RichCustomEmoji getEmojiById(long id) {
		return this.guild.getEmojiById(id);
	}

	@Override
	@Nonnull
	public List<RichCustomEmoji> getEmojis() {
		return this.guild.getEmojis();
	}

	@Override
	@Nonnull
	public List<RichCustomEmoji> getEmojisByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getEmojisByName(name, ignoreCase);
	}

	@Override
	@Nonnull
	public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
		return this.guild.getEmojiCache();
	}

	@Override
	@Nullable
	public GuildSticker getStickerById(@Nonnull String id) {
		return this.guild.getStickerById(id);
	}

	@Override
	@Nullable
	public GuildSticker getStickerById(long id) {
		return this.guild.getStickerById(id);
	}

	@Override
	@Nonnull
	public List<GuildSticker> getStickers() {
		return this.guild.getStickers();
	}

	@Override
	@Nonnull
	public List<GuildSticker> getStickersByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getStickersByName(name, ignoreCase);
	}

	@Override
	@Nonnull
	public SnowflakeCacheView<GuildSticker> getStickerCache() {
		return this.guild.getStickerCache();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<RichCustomEmoji>> retrieveEmojis() {
		return this.guild.retrieveEmojis();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<RichCustomEmoji> retrieveEmojiById(@Nonnull String id) {
		return this.guild.retrieveEmojiById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<RichCustomEmoji> retrieveEmojiById(long id) {
		return this.guild.retrieveEmojiById(id);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<RichCustomEmoji> retrieveEmoji(@Nonnull CustomEmoji emoji) {
		return this.guild.retrieveEmoji(emoji);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<GuildSticker>> retrieveStickers() {
		return this.guild.retrieveStickers();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<GuildSticker> retrieveSticker(@Nonnull StickerSnowflake sticker) {
		return this.guild.retrieveSticker(sticker);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public GuildStickerManager editSticker(@Nonnull StickerSnowflake sticker) {
		return this.guild.editSticker(sticker);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public BanPaginationAction retrieveBanList() {
		return this.guild.retrieveBanList();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Ban> retrieveBan(@Nonnull UserSnowflake user) {
		return this.guild.retrieveBan(user);
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
	public DefaultGuildChannelUnion getDefaultChannel() {
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
	public RestAction<Void> delete(@Nullable String mfaCode) {
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
	public RestAction<Template> createTemplate(@Nonnull String name, @Nullable String description) {
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
	public Task<List<Member>> findMembers(@Nonnull Predicate<? super Member> filter) {
		return this.guild.findMembers(filter);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> findMembersWithRoles(@Nonnull Collection<Role> roles) {
		return this.guild.findMembersWithRoles(roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> findMembersWithRoles(@Nonnull Role... roles) {
		return this.guild.findMembersWithRoles(roles);
	}

	@Override
	@Nonnull
	public Task<Void> loadMembers(@Nonnull Consumer<Member> callback) {
		return this.guild.loadMembers(callback);
	}

	@Override
	@Nonnull
	public CacheRestAction<Member> retrieveMember(@Nonnull UserSnowflake user) {
		return this.guild.retrieveMember(user);
	}

	@Override
	@Nonnull
	public CacheRestAction<Member> retrieveMemberById(@Nonnull String id) {
		return this.guild.retrieveMemberById(id);
	}

	@Override
	@Nonnull
	public CacheRestAction<Member> retrieveMemberById(long id) {
		return this.guild.retrieveMemberById(id);
	}

	@Override
	@Nonnull
	public CacheRestAction<Member> retrieveOwner() {
		return this.guild.retrieveOwner();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembers(@Nonnull Collection<? extends UserSnowflake> users) {
		return this.guild.retrieveMembers(users);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(@Nonnull Collection<Long> ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(@Nonnull String... ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(@Nonnull long... ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembers(boolean includePresence, @Nonnull Collection<? extends UserSnowflake> users) {
		return this.guild.retrieveMembers(includePresence, users);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull Collection<Long> ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull String... ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull long... ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public Task<List<Member>> retrieveMembersByPrefix(@Nonnull String prefix, int limit) {
		return this.guild.retrieveMembersByPrefix(prefix, limit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<List<ThreadChannel>> retrieveActiveThreads() {
		return this.guild.retrieveActiveThreads();
	}

	@NotNull
	@Override
	public RestAction<Void> moveVoiceMember(@NotNull Member member, @Nullable AudioChannel audioChannel) {
		return this.guild.moveVoiceMember(member, audioChannel);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public RestAction<Void> kickVoiceMember(@Nonnull Member member) {
		return this.guild.kickVoiceMember(member);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyNickname(@Nonnull Member member, @Nullable String nickname) {
		return this.guild.modifyNickname(member, nickname);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Integer> prune(int days, @Nonnull Role... roles) {
		return this.guild.prune(days, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Integer> prune(int days, boolean wait, @Nonnull Role... roles) {
		return this.guild.prune(days, wait, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	@Deprecated
	public AuditableRestAction<Void> kick(@Nonnull UserSnowflake user, @Nullable String reason) {
		return this.guild.kick(user, reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick(@Nonnull UserSnowflake user) {
		return this.guild.kick(user);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(@NotNull UserSnowflake user, int deletionTimeframe, @NotNull TimeUnit unit) {
		return this.guild.ban(user, deletionTimeframe, unit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> unban(@Nonnull UserSnowflake user) {
		return this.guild.unban(user);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(@Nonnull UserSnowflake user, long amount, @Nonnull TimeUnit unit) {
		return this.guild.timeoutFor(user, amount, unit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(@Nonnull UserSnowflake user, @Nonnull Duration duration) {
		return this.guild.timeoutFor(user, duration);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutUntil(@Nonnull UserSnowflake user, @Nonnull TemporalAccessor temporal) {
		return this.guild.timeoutUntil(user, temporal);
	}

	@Override
	@Nonnull
	public AuditableRestAction<Void> removeTimeout(@Nonnull UserSnowflake user) {
		return this.guild.removeTimeout(user);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> deafen(@Nonnull UserSnowflake user, boolean deafen) {
		return this.guild.deafen(user, deafen);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> mute(@Nonnull UserSnowflake user, boolean mute) {
		return this.guild.mute(user, mute);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> addRoleToMember(@Nonnull UserSnowflake user, @Nonnull Role role) {
		return this.guild.addRoleToMember(user, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> removeRoleFromMember(@Nonnull UserSnowflake user, @Nonnull Role role) {
		return this.guild.removeRoleFromMember(user, role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nullable Collection<Role> rolesToAdd, @Nullable Collection<Role> rolesToRemove) {
		return this.guild.modifyMemberRoles(member, rolesToAdd, rolesToRemove);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Role... roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Collection<Role> roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> transferOwnership(@Nonnull Member newOwner) {
		return this.guild.transferOwnership(newOwner);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<TextChannel> createTextChannel(@Nonnull String name) {
		return this.guild.createTextChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<TextChannel> createTextChannel(@Nonnull String name, @Nullable Category parent) {
		return this.guild.createTextChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<NewsChannel> createNewsChannel(@Nonnull String name) {
		return this.guild.createNewsChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<NewsChannel> createNewsChannel(@Nonnull String name, @Nullable Category parent) {
		return this.guild.createNewsChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name) {
		return this.guild.createVoiceChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name, @Nullable Category parent) {
		return this.guild.createVoiceChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<StageChannel> createStageChannel(@Nonnull String name) {
		return this.guild.createStageChannel(name);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<StageChannel> createStageChannel(@Nonnull String name, @Nullable Category parent) {
		return this.guild.createStageChannel(name, parent);
	}

	@NotNull
	@Override
	public ChannelAction<ForumChannel> createForumChannel(@NotNull String name, @Nullable Category parent) {
		return this.guild.createForumChannel(name, parent);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public ChannelAction<Category> createCategory(@Nonnull String name) {
		return this.guild.createCategory(name);
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
	public RoleAction createCopyOfRole(@Nonnull Role role) {
		return this.guild.createCopyOfRole(role);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<RichCustomEmoji> createEmoji(@Nonnull String name, @Nonnull Icon icon, @Nonnull Role... roles) {
		return this.guild.createEmoji(name, icon, roles);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<GuildSticker> createSticker(@Nonnull String name, @Nonnull String description, @Nonnull FileUpload file, @Nonnull Collection<String> tags) {
		return this.guild.createSticker(name, description, file, tags);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<GuildSticker> createSticker(@Nonnull String name, @Nonnull String description, @Nonnull FileUpload file, @Nonnull String tag, @Nonnull String... tags) {
		return this.guild.createSticker(name, description, file, tag, tags);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> deleteSticker(@Nonnull StickerSnowflake id) {
		return this.guild.deleteSticker(id);
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
	public CategoryOrderAction modifyTextChannelPositions(@Nonnull Category category) {
		return this.guild.modifyTextChannelPositions(category);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public CategoryOrderAction modifyVoiceChannelPositions(@Nonnull Category category) {
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
	public <T extends Channel> T getChannelById(@Nonnull Class<T> type, @Nonnull String id) {
		return this.guild.getChannelById(type, id);
	}

	@Override
	@Nullable
	public <T extends Channel> T getChannelById(@Nonnull Class<T> type, long id) {
		return this.guild.getChannelById(type, id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(@Nonnull String id) {
		return this.guild.getGuildChannelById(id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(long id) {
		return this.guild.getGuildChannelById(id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(@Nonnull ChannelType type, @Nonnull String id) {
		return this.guild.getGuildChannelById(type, id);
	}

	@Override
	@Nullable
	public GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
		return this.guild.getGuildChannelById(type, id);
	}

	@Override
	@Nonnull
	public List<StageChannel> getStageChannelsByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getStageChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public StageChannel getStageChannelById(@Nonnull String id) {
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
	public List<ThreadChannel> getThreadChannelsByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getThreadChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public ThreadChannel getThreadChannelById(@Nonnull String id) {
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
	public List<Category> getCategoriesByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getCategoriesByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public Category getCategoryById(@Nonnull String id) {
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
	public List<TextChannel> getTextChannelsByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getTextChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public TextChannel getTextChannelById(@Nonnull String id) {
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
	public List<NewsChannel> getNewsChannelsByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getNewsChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public NewsChannel getNewsChannelById(@Nonnull String id) {
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
	public List<VoiceChannel> getVoiceChannelsByName(@Nonnull String name, boolean ignoreCase) {
		return this.guild.getVoiceChannelsByName(name, ignoreCase);
	}

	@Override
	@Nullable
	public VoiceChannel getVoiceChannelById(@Nonnull String id) {
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
	@Nonnull
	public String getId() {
		return this.guild.getId();
	}

	@Override
	public long getIdLong() {
		return this.guild.getIdLong();
	}

	@Override
	@Nonnull
	public OffsetDateTime getTimeCreated() {
		return this.guild.getTimeCreated();
	}

	@Override
	@Nonnull
	public SortedSnowflakeCacheView<ForumChannel> getForumChannelCache() {
		return guild.getForumChannelCache();
	}
}
