package com.github.black0nion.blackonionbot.blackobjects;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.utils.config.Config;
import net.dv8tion.jda.api.entities.*;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.ConfigGetter;
import com.github.black0nion.blackonionbot.misc.ConfigSetter;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.systems.CustomCommand;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType;
import com.github.black0nion.blackonionbot.systems.dashboard.DashboardGetter;
import com.github.black0nion.blackonionbot.systems.dashboard.DashboardSetter;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
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
	private BlackMember selfBlackMember;

	public static final MongoCollection<Document> configs = MongoDB.botDatabase.getCollection("guildsettings");

	private static final LoadingCache<Guild, BlackGuild> guilds = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<Guild, BlackGuild>() {
		@Override
		public BlackGuild load(final @NotNull Guild guild) {
			return new BlackGuild(guild);
		}
	});

	@Deprecated
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
	public static BlackGuild from(@NotNull final long guildid) {
		final Optional<Entry<Guild, BlackGuild>> first = guilds.asMap().entrySet().stream().filter(entry -> entry.getKey().getIdLong() == guildid).findFirst();
		return first.isPresent() ? first.get().getValue() : from(Bot.jda.getGuildById(guildid));
	}

	private Language language;
	private GuildType guildType;
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
	private boolean loop;
	private HashMap<String, CustomCommand> customCommands;

	private BlackGuild(@NotNull final Guild guild) {

		this.guild = guild;

		try {
			Document config = configs.find(this.getIdentifier()).first();

			if (config == null) {
				config = new Document();
			}

			// only to debug the configs
			this.save("name", guild.getName());
			this.language = this.gOD(LanguageSystem.getLanguageFromName(config.getString("language")), LanguageSystem.defaultLocale);
			this.guildType = this.gOD(GuildType.parse(config.getString("guildtype")), GuildType.NORMAL);
			this.prefix = this.gOD(config.getString("prefix"), Config.discord.DEFAULT_PREFIX);
			this.antiSpoilerType = this.gOD(AntiSpoilerType.parse(config.getString("antispoiler")), AntiSpoilerType.OFF);
			this.antiSwearType = this.gOD(AntiSwearType.parse(config.getString("antiswear")), AntiSwearType.OFF);
			this.joinMessage = this.gOD(config.getString("joinmessage"), this.language.getTranslationNonNull("defaultjoinmessage"));
			this.joinChannel = this.gOD(config.getLong("joinchannel"), -1L);
			this.leaveMessage = this.gOD(config.getString("leavemessage"), this.language.getTranslationNonNull("defaultleavemessage"));
			this.leaveChannel = this.gOD(config.getLong("leavechannel"), -1L);
			this.suggestionsChannel = this.gOD(config.getLong("suggestionschannel"), -1L);
			this.autoRoles = this.gOD(config.getList("autoroles", Long.class), new ArrayList<>());
			this.antiSwearWhitelist = this.gOD(config.getList("antiswearwhitelist", String.class), new ArrayList<>());
			this.loop = this.gOD(config.getBoolean("loop"), false);
			this.customCommands = new HashMap<>();
			this.gOD(this.gOD(config.getList("customcommands", Document.class), new ArrayList<Document>()).stream().map(cmd -> new CustomCommand(this, cmd)).collect(Collectors.toList()), new ArrayList<CustomCommand>()).forEach(cmd -> this.customCommands.put(cmd.getCommand(), cmd));
			final List<String> disabledCommandsString = config.getList("disabledcommands", String.class);
			if (!(disabledCommandsString == null || disabledCommandsString.isEmpty())) {
				this.disabledCommands = disabledCommandsString.stream().map(cmd -> CommandBase.commands.get(cmd)).collect(Collectors.toList());
			} else {
				this.disabledCommands = new ArrayList<>();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public Language getLanguage() {
		return this.language;
	}

	@DashboardGetter("setup.language")
	public String getLanguageString() {
		return this.language.getLanguageCode();
	}

	@DashboardSetter("setup.language")
	public void setLanguage(final String language) {
		this.setLanguage(LanguageSystem.getLanguageFromName(language));
	}

	public void setLanguage(final Language language) {
		if (language == null) return;
		this.language = language;
		this.save("language", language.getLanguageCode());
	}

	@DashboardGetter("setup.guildtype")
	public GuildType getGuildType() {
		return this.guildType;
	}

	public void setGuildType(final GuildType type) {
		this.guildType = type;
		this.save("guildtype", type.name());
	}

	public boolean isPremium() {
		return this.getGuildType().higherThanOrEqual(GuildType.PREMIUM);
	}

	@DashboardGetter("general.prefix")
	public String getPrefix() {
		return this.prefix;
	}

	@DashboardSetter("general.prefix")
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
		this.save("prefix", prefix);
	}

	@DashboardGetter("utils.joinleave.join.message")
	public String getJoinMessage() {
		return this.joinMessage;
	}

	@DashboardSetter("utils.joinleave.join.message")
	public void setJoinMessage(final String newMessage) {
		this.joinMessage = newMessage;
		this.save("joinmessage", this.joinMessage);
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
		if (joinChannel == -1) {
			this.clear("joinchannel");
		} else {
			this.save("joinchannel", joinChannel);
		}
	}

	@DashboardGetter("utils.joinleave.leave.message")
	public String getLeaveMessage() {
		return this.leaveMessage;
	}

	@DashboardSetter("utils.joinleave.leave.message")
	public void setLeaveMessage(final String leaveMessage) {
		this.leaveMessage = leaveMessage;
		this.save("leavemessage", leaveMessage);
	}

	@DashboardGetter(value = "utils.joinleave.leave.channel", nullable = true)
	public long getLeaveChannel() {
		return this.leaveChannel;
	}

	@ConfigGetter(key = "leavechannel", requiredPermissions = {Permission.MANAGE_SERVER}, description = "Set the Channel the Message on leave should be sent to")
	public TextChannel getLeaveChannelAsChannel() {
		if (this.leaveChannel == -1) return null;
		return this.guild.getTextChannelById(this.leaveChannel);
	}

	@DashboardSetter(value = "utils.joinleave.leave.channel", nullable = true)
	@ConfigSetter(argumentsDescription = {"The Channel where the message should get sent to"}, key = "leavechannel", requiredPermissions = {Permission.MANAGE_SERVER}, nullable = true)
	public void setLeaveChannel(final TextChannel channel) {
		if (channel == null) {
			this.setLeaveChannel(-1);
		} else {
			this.setLeaveChannel(channel.getIdLong());
		}
	}

	public void setLeaveChannel(final long leaveChannel) {
		this.leaveChannel = leaveChannel;
		if (leaveChannel == -1) {
			this.clear("leavechannel");
		} else {
			this.save("leavechannel", leaveChannel);
		}
	}

	public List<Command> getDisabledCommands() {
		return this.disabledCommands;
	}

	public void setDisabledCommands(final String[] disabledCommands) {
		this.setDisabledCommands(Arrays.stream(disabledCommands).map(cmd -> CommandBase.commands.get(cmd)).collect(Collectors.toList()));
	}

	public void setDisabledCommands(final Command[] disabledCommands) {
		this.setDisabledCommands(Arrays.asList(disabledCommands));
	}

	public void setDisabledCommands(final List<Command> disabledCommands) {
		this.disabledCommands = disabledCommands;
		this.saveList("disabledcommands", disabledCommands.stream().map(cmd -> cmd.getCommand()[0]).collect(Collectors.toList()));
	}

	public boolean isCommandActivated(final Command cmd) {
		return !this.disabledCommands.contains(cmd);
	}

	public boolean setCommandActivated(final Command cmd, final boolean activated) {
		if (!cmd.isToggleable()) return false;
		if (this.disabledCommands.contains(cmd) && activated) {
			this.disabledCommands.remove(cmd);
		} else if (activated && !this.disabledCommands.contains(cmd)) {
			this.disabledCommands.add(cmd);
		}
		this.setDisabledCommands(this.disabledCommands);
		return true;
	}

	public AntiSpoilerType getAntiSpoilerType() {
		return this.antiSpoilerType;
	}

	public void setAntiSpoilerType(final AntiSpoilerType antiSpoilerType) {
		this.antiSpoilerType = antiSpoilerType;
		this.save("antispoiler", antiSpoilerType.name());
	}

	public AntiSwearType getAntiSwearType() {
		return this.antiSwearType;
	}

	public void setAntiSwearType(final AntiSwearType antiSwearType) {
		this.antiSwearType = antiSwearType;
		this.save("antiswear", antiSwearType.name());
	}

	public List<String> getAntiSwearWhitelist() {
		return this.antiSwearWhitelist;
	}

	public void setAntiSwearWhitelist(final List<String> antiSwearWhitelist) {
		this.antiSwearWhitelist = antiSwearWhitelist;
		this.saveAntiSwearWhitelist();
	}

	public void addToAntiSwearWhitelist(final String toAdd) {
		this.antiSwearWhitelist.add(toAdd);
		this.saveAntiSwearWhitelist();
	}

	public void removeFromAntiSwearWhitelist(final String toRemove) {
		this.antiSwearWhitelist.remove(toRemove);
		this.saveAntiSwearWhitelist();
	}

	public void saveAntiSwearWhitelist() {
		this.saveList("antiswearwhitelist", this.antiSwearWhitelist);
	}

	public long getSuggestionsChannel() {
		return this.suggestionsChannel;
	}

	public void setSuggestionsChannel(final TextChannel channel) {
		this.setSuggestionsChannel(channel.getIdLong());
	}

	public void setSuggestionsChannel(final long suggestionsChannel) {
		this.suggestionsChannel = suggestionsChannel;
		this.save("suggestionschannel", suggestionsChannel);
	}

	public BlackMember getSelfBlackMember() {
		if (this.selfBlackMember == null) {
			this.selfBlackMember = BlackMember.from(this.guild.getSelfMember(), this);
		}
		return this.selfBlackMember;
	}

	public void removeAutoRole(final long roleId) {
		this.autoRoles.remove(roleId);
		this.saveAutoRoles();
	}

	public void addAutoRole(final long roleId) {
		if (!this.autoRoles.contains(roleId)) {
			this.autoRoles.add(roleId);
		}
		this.saveAutoRoles();
	}

	public void setAutoRoles(final List<Long> autoRoles) {
		this.autoRoles = autoRoles;
		this.saveAutoRoles();
	}

	public void saveAutoRoles() {
		this.saveList("autoroles", this.autoRoles);
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
		this.save("loop", loop);
	}

	/**
	 * @return the customCommands
	 */
	public HashMap<String, CustomCommand> getCustomCommands() {
		return this.customCommands;
	}

	public boolean addCustomCommand(final CustomCommand cmd) {
		if (this.customCommands.containsKey(cmd.getCommand())) return false;
		this.customCommands.put(cmd.getCommand(), cmd);
		this.save("customcommands", this.customCommands.values().stream().map(CustomCommand::toDocument).collect(Collectors.toList()));
		return true;
	}

	public void deleteCustomCommand(final String commandName) {
		this.customCommands.remove(commandName);
		this.save("customcommands", this.customCommands.values().stream().map(CustomCommand::toDocument).collect(Collectors.toList()));
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

	@NotNull
	@Override
	public RestAction<net.dv8tion.jda.api.interactions.commands.Command> retrieveCommandById(long id) {
		return this.guild.retrieveCommandById(id);
	}

	@NotNull
	@Override
	public CommandCreateAction upsertCommand(@NotNull String name, @NotNull String description) {
		return this.guild.upsertCommand(name, description);
	}

	@NotNull
	@Override
	public CommandEditAction editCommandById(long id) {
		return this.guild.editCommandById(id);
	}

	@NotNull
	@Override
	public RestAction<Void> deleteCommandById(long commandId) {
		return this.guild.deleteCommandById(commandId);
	}

	@NotNull
	@Override
	public RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(long commandId) {
		return this.guild.retrieveCommandPrivilegesById(commandId);
	}

	@NotNull
	@Override
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id, @NotNull CommandPrivilege... privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@NotNull
	@Override
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id, @NotNull Collection<? extends CommandPrivilege> privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@NotNull
	@Override
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id, @NotNull CommandPrivilege... privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@NotNull
	@Override
	public RestAction<EnumSet<Region>> retrieveRegions() {
		return this.guild.retrieveRegions();
	}

	@NotNull
	@Override
	public MemberAction addMember(@NotNull String accessToken, @NotNull User user) {
		return this.guild.addMember(accessToken, user);
	}

	@NotNull
	@Override
	public MemberAction addMember(@NotNull String accessToken, long userId) {
		return this.guild.addMember(accessToken, userId);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public String getIconUrl() {
		return this.guild.getIconUrl();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public String getSplashUrl() {
		return this.guild.getSplashUrl();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public String getVanityUrl() {
		return this.guild.getVanityUrl();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public String getBannerUrl() {
		return this.guild.getBannerUrl();
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

	@NotNull
	@Override
	public String getOwnerId() {
		return this.guild.getOwnerId();
	}

	@NotNull
	@Deprecated
	@Override
	public Region getRegion() {
		return this.guild.getRegion();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Member getMemberById(@NotNull String userId) {
		return this.guild.getMemberById(userId);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Member getMemberById(long userId) {
		return this.guild.getMemberById(userId);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Member getMemberByTag(@NotNull String tag) {
		return this.guild.getMemberByTag(tag);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {
		return this.guild.getMemberByTag(username, discriminator);
	}

	@NotNull
	@Override
	public List<Member> getMembers() {
		return this.guild.getMembers();
	}

	@NotNull
	@Override
	public List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getMembersByName(name, ignoreCase);
	}

	@NotNull
	@Override
	public List<Member> getMembersByNickname(@org.jetbrains.annotations.Nullable String nickname, boolean ignoreCase) {
		return this.guild.getMembersByNickname(nickname, ignoreCase);
	}

	@NotNull
	@Override
	public List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getMembersByEffectiveName(name, ignoreCase);
	}

	@NotNull
	@Override
	public List<Member> getMembersWithRoles(@NotNull Role... roles) {
		return this.guild.getMembersWithRoles(roles);
	}

	@NotNull
	@Override
	public List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {
		return this.guild.getMembersWithRoles(roles);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public GuildChannel getGuildChannelById(@NotNull String id) {
		return this.guild.getGuildChannelById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public GuildChannel getGuildChannelById(long id) {
		return this.guild.getGuildChannelById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {
		return this.guild.getGuildChannelById(type, id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {
		return this.guild.getGuildChannelById(type, id);
	}

	@NotNull
	@Override
	public List<StageChannel> getStageChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getStageChannelsByName(name, ignoreCase);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public StageChannel getStageChannelById(@NotNull String id) {
		return this.guild.getStageChannelById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public StageChannel getStageChannelById(long id) {
		return this.guild.getStageChannelById(id);
	}

	@NotNull
	@Override
	public List<StageChannel> getStageChannels() {
		return this.guild.getStageChannels();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Category getCategoryById(@NotNull String id) {
		return this.guild.getCategoryById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Category getCategoryById(long id) {
		return this.guild.getCategoryById(id);
	}

	@NotNull
	@Override
	public List<Category> getCategories() {
		return this.guild.getCategories();
	}

	@NotNull
	@Override
	public List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getCategoriesByName(name, ignoreCase);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public StoreChannel getStoreChannelById(@NotNull String id) {
		return this.guild.getStoreChannelById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public StoreChannel getStoreChannelById(long id) {
		return this.guild.getStoreChannelById(id);
	}

	@NotNull
	@Override
	public List<StoreChannel> getStoreChannels() {
		return this.guild.getStoreChannels();
	}

	@NotNull
	@Override
	public List<StoreChannel> getStoreChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getStoreChannelsByName(name, ignoreCase);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public TextChannel getTextChannelById(@NotNull String id) {
		return this.guild.getTextChannelById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public TextChannel getTextChannelById(long id) {
		return this.guild.getTextChannelById(id);
	}

	@NotNull
	@Override
	public List<TextChannel> getTextChannels() {
		return this.guild.getTextChannels();
	}

	@NotNull
	@Override
	public List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getTextChannelsByName(name, ignoreCase);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public VoiceChannel getVoiceChannelById(@NotNull String id) {
		return this.guild.getVoiceChannelById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public VoiceChannel getVoiceChannelById(long id) {
		return this.guild.getVoiceChannelById(id);
	}

	@NotNull
	@Override
	public List<VoiceChannel> getVoiceChannels() {
		return this.guild.getVoiceChannels();
	}

	@NotNull
	@Override
	public List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getVoiceChannelsByName(name, ignoreCase);
	}

	@NotNull
	@Override
	public List<GuildChannel> getChannels() {
		return this.guild.getChannels();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Role getRoleById(@NotNull String id) {
		return this.guild.getRoleById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Role getRoleById(long id) {
		return this.guild.getRoleById(id);
	}

	@NotNull
	@Override
	public List<Role> getRoles() {
		return this.guild.getRoles();
	}

	@NotNull
	@Override
	public List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getRolesByName(name, ignoreCase);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Role getRoleByBot(long userId) {
		return this.guild.getRoleByBot(userId);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Role getRoleByBot(@NotNull String userId) {
		return this.guild.getRoleByBot(userId);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Role getRoleByBot(@NotNull User user) {
		return this.guild.getRoleByBot(user);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Role getBotRole() {
		return this.guild.getBotRole();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Role getBoostRole() {
		return this.guild.getBoostRole();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Emote getEmoteById(@NotNull String id) {
		return this.guild.getEmoteById(id);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Emote getEmoteById(long id) {
		return this.guild.getEmoteById(id);
	}

	@NotNull
	@Override
	public List<Emote> getEmotes() {
		return this.guild.getEmotes();
	}

	@NotNull
	@Override
	public List<Emote> getEmotesByName(@NotNull String name, boolean ignoreCase) {
		return this.guild.getEmotesByName(name, ignoreCase);
	}

	@NotNull
	@Override
	public RestAction<ListedEmote> retrieveEmoteById(long id) {
		return this.guild.retrieveEmoteById(id);
	}

	@NotNull
	@Override
	public RestAction<ListedEmote> retrieveEmote(@NotNull Emote emote) {
		return this.guild.retrieveEmote(emote);
	}

	@NotNull
	@Override
	public RestAction<Ban> retrieveBanById(long userId) {
		return this.guild.retrieveBanById(userId);
	}

	@NotNull
	@Override
	public RestAction<Ban> retrieveBan(@NotNull User bannedUser) {
		return this.guild.retrieveBan(bannedUser);
	}

	@NotNull
	@Override
	public Task<List<Member>> loadMembers() {
		return this.guild.loadMembers();
	}

	@NotNull
	@Override
	public Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {
		return this.guild.findMembers(filter);
	}

	@NotNull
	@Override
	public Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {
		return this.guild.findMembersWithRoles(roles);
	}

	@NotNull
	@Override
	public Task<List<Member>> findMembersWithRoles(@NotNull Role... roles) {
		return this.guild.findMembersWithRoles(roles);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveMember(@NotNull User user) {
		return this.guild.retrieveMember(user);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveMemberById(@NotNull String id) {
		return this.guild.retrieveMemberById(id);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveMemberById(long id) {
		return this.guild.retrieveMemberById(id);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveOwner() {
		return this.guild.retrieveOwner();
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveMember(@NotNull User user, boolean update) {
		return this.guild.retrieveMember(user, update);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveMemberById(@NotNull String id, boolean update) {
		return this.guild.retrieveMemberById(id, update);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveOwner(boolean update) {
		return this.guild.retrieveOwner(update);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembers(@NotNull Collection<User> users) {
		return this.guild.retrieveMembers(users);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByIds(@NotNull String... ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByIds(@NotNull long... ids) {
		return this.guild.retrieveMembersByIds(ids);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembers(boolean includePresence, @NotNull Collection<User> users) {
		return this.guild.retrieveMembers(includePresence, users);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull Collection<Long> ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull String... ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@NotNull
	@Override
	public RestAction<Void> kickVoiceMember(@NotNull Member member) {
		return this.guild.kickVoiceMember(member);
	}

	@NotNull
	@Override
	public AuditableRestAction<Integer> prune(int days, @NotNull Role... roles) {
		return this.guild.prune(days, roles);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> kick(@NotNull Member member) {
		return this.guild.kick(member);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> kick(@NotNull String userId) {
		return this.guild.kick(userId);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(@NotNull Member member, int delDays, @org.jetbrains.annotations.Nullable String reason) {
		return this.guild.ban(member, delDays, reason);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(@NotNull Member member, int delDays) {
		return this.guild.ban(member, delDays);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(@NotNull User user, int delDays) {
		return this.guild.ban(user, delDays);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(@NotNull String userId, int delDays) {
		return this.guild.ban(userId, delDays);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> unban(@NotNull User user) {
		return this.guild.unban(user);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> addRoleToMember(long userId, @NotNull Role role) {
		return this.guild.addRoleToMember(userId, role);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> addRoleToMember(@NotNull String userId, @NotNull Role role) {
		return this.guild.addRoleToMember(userId, role);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> removeRoleFromMember(long userId, @NotNull Role role) {
		return this.guild.removeRoleFromMember(userId, role);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> removeRoleFromMember(@NotNull String userId, @NotNull Role role) {
		return this.guild.removeRoleFromMember(userId, role);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Role... roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@NotNull
	@Override
	public ChannelAction<TextChannel> createTextChannel(@NotNull String name) {
		return this.guild.createTextChannel(name);
	}

	@NotNull
	@Override
	public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name) {
		return this.guild.createVoiceChannel(name);
	}

	@NotNull
	@Override
	public ChannelAction<StageChannel> createStageChannel(@NotNull String name) {
		return this.guild.createStageChannel(name);
	}

	@NotNull
	@Override
	public <T extends GuildChannel> ChannelAction<T> createCopyOfChannel(@NotNull T channel) {
		return this.guild.createCopyOfChannel(channel);
	}

	@NotNull
	@Override
	public RoleAction createCopyOfRole(@NotNull Role role) {
		return this.guild.createCopyOfRole(role);
	}

	@NotNull
	@Override
	public RoleOrderAction modifyRolePositions() {
		return this.guild.modifyRolePositions();
	}

	@NotNull
	@Override
	public RestAction<List<net.dv8tion.jda.api.interactions.commands.Command>> retrieveCommands() {
		return this.guild.retrieveCommands();
	}

	@NotNull
	@Override
	public RestAction<net.dv8tion.jda.api.interactions.commands.Command> retrieveCommandById(@NotNull String id) {
		return this.guild.retrieveCommandById(id);
	}

	@NotNull
	@Override
	public CommandCreateAction upsertCommand(@NotNull CommandData command) {
		return this.guild.upsertCommand(command);
	}

	@NotNull
	@Override
	public CommandListUpdateAction updateCommands() {
		return this.guild.updateCommands();
	}

	@NotNull
	@Override
	public CommandEditAction editCommandById(@NotNull String id) {
		return this.guild.editCommandById(id);
	}

	@NotNull
	@Override
	public RestAction<Void> deleteCommandById(@NotNull String commandId) {
		return this.guild.deleteCommandById(commandId);
	}

	@NotNull
	@Override
	public RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(@NotNull String commandId) {
		return this.guild.retrieveCommandPrivilegesById(commandId);
	}

	@NotNull
	@Override
	public RestAction<Map<String, List<CommandPrivilege>>> retrieveCommandPrivileges() {
		return this.guild.retrieveCommandPrivileges();
	}

	@NotNull
	@Override
	public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id, @NotNull Collection<? extends CommandPrivilege> privileges) {
		return this.guild.updateCommandPrivilegesById(id, privileges);
	}

	@NotNull
	@Override
	public RestAction<Map<String, List<CommandPrivilege>>> updateCommandPrivileges(@NotNull Map<String, Collection<? extends CommandPrivilege>> privileges) {
		return this.guild.updateCommandPrivileges(privileges);
	}

	@NotNull
	@Override
	public RestAction<EnumSet<Region>> retrieveRegions(boolean includeDeprecated) {
		return this.guild.retrieveRegions(includeDeprecated);
	}

	@NotNull
	@Override
	public MemberAction addMember(@NotNull String accessToken, @NotNull String userId) {
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

	@NotNull
	@Override
	public String getName() {
		return this.guild.getName();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public String getIconId() {
		return this.guild.getIconId();
	}

	@NotNull
	@Override
	public Set<String> getFeatures() {
		return this.guild.getFeatures();
	}

	@org.jetbrains.annotations.Nullable
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

	@org.jetbrains.annotations.Nullable
	@Override
	public String getVanityCode() {
		return this.guild.getVanityCode();
	}

	@NotNull
	@Override
	public RestAction<VanityInvite> retrieveVanityInvite() {
		return this.guild.retrieveVanityInvite();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public String getDescription() {
		return this.guild.getDescription();
	}

	@NotNull
	@Override
	public Locale getLocale() {
		return this.guild.getLocale();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public String getBannerId() {
		return this.guild.getBannerId();
	}

	@NotNull
	@Override
	public BoostTier getBoostTier() {
		return this.guild.getBoostTier();
	}

	@Override
	public int getBoostCount() {
		return this.guild.getBoostCount();
	}

	@NotNull
	@Override
	public List<Member> getBoosters() {
		return this.guild.getBoosters();
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

	@org.jetbrains.annotations.Nullable
	@Override
	public VoiceChannel getAfkChannel() {
		return this.guild.getAfkChannel();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public TextChannel getSystemChannel() {
		return this.guild.getSystemChannel();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public TextChannel getRulesChannel() {
		return this.guild.getRulesChannel();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public TextChannel getCommunityUpdatesChannel() {
		return this.guild.getCommunityUpdatesChannel();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Member getOwner() {
		return this.guild.getOwner();
	}

	@Override
	public long getOwnerIdLong() {
		return this.guild.getOwnerIdLong();
	}

	@NotNull
	@Override
	public Timeout getAfkTimeout() {
		return this.guild.getAfkTimeout();
	}

	@NotNull
	@Deprecated
	@Override
	public String getRegionRaw() {
		return this.guild.getRegionRaw();
	}

	@Override
	public boolean isMember(@NotNull User user) {
		return this.guild.isMember(user);
	}

	@NotNull
	@Override
	public Member getSelfMember() {
		return this.guild.getSelfMember();
	}

	@NotNull
	@Override
	public NSFWLevel getNSFWLevel() {
		return this.guild.getNSFWLevel();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Member getMember(@NotNull User user) {
		return this.guild.getMember(user);
	}

	@NotNull
	@Override
	public MemberCacheView getMemberCache() {
		return this.guild.getMemberCache();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<Category> getCategoryCache() {
		return this.guild.getCategoryCache();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache() {
		return this.guild.getStoreChannelCache();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
		return this.guild.getTextChannelCache();
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
		return this.guild.getVoiceChannelCache();
	}

	@NotNull
	@Override
	public List<GuildChannel> getChannels(boolean includeHidden) {
		return this.guild.getChannels(includeHidden);
	}

	@NotNull
	@Override
	public SortedSnowflakeCacheView<Role> getRoleCache() {
		return this.guild.getRoleCache();
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
	public RestAction<ListedEmote> retrieveEmoteById(@NotNull String id) {
		return this.guild.retrieveEmoteById(id);
	}

	@NotNull
	@Override
	public RestAction<List<Ban>> retrieveBanList() {
		return this.guild.retrieveBanList();
	}

	@NotNull
	@Override
	public RestAction<Ban> retrieveBanById(@NotNull String userId) {
		return this.guild.retrieveBanById(userId);
	}

	@NotNull
	@Override
	public RestAction<Integer> retrievePrunableMemberCount(int days) {
		return this.guild.retrievePrunableMemberCount(days);
	}

	@NotNull
	@Override
	public Role getPublicRole() {
		return this.guild.getPublicRole();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public TextChannel getDefaultChannel() {
		return this.guild.getDefaultChannel();
	}

	@NotNull
	@Override
	public GuildManager getManager() {
		return this.guild.getManager();
	}

	@NotNull
	@Override
	public AuditLogPaginationAction retrieveAuditLogs() {
		return this.guild.retrieveAuditLogs();
	}

	@NotNull
	@Override
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
	public RestAction<Void> delete(@org.jetbrains.annotations.Nullable String mfaCode) {
		return this.guild.delete(mfaCode);
	}

	@NotNull
	@Override
	public AudioManager getAudioManager() {
		return this.guild.getAudioManager();
	}

	@NotNull
	@Override
	public Task<Void> requestToSpeak() {
		return this.guild.requestToSpeak();
	}

	@NotNull
	@Override
	public Task<Void> cancelRequestToSpeak() {
		return this.guild.cancelRequestToSpeak();
	}

	@NotNull
	@Override
	public JDA getJDA() {
		return this.guild.getJDA();
	}

	@NotNull
	@Override
	public RestAction<List<Invite>> retrieveInvites() {
		return this.guild.retrieveInvites();
	}

	@NotNull
	@Override
	public RestAction<List<Template>> retrieveTemplates() {
		return this.guild.retrieveTemplates();
	}

	@NotNull
	@Override
	public RestAction<Template> createTemplate(@NotNull String name, @org.jetbrains.annotations.Nullable String description) {
		return this.guild.createTemplate(name, description);
	}

	@NotNull
	@Override
	public RestAction<List<Webhook>> retrieveWebhooks() {
		return this.guild.retrieveWebhooks();
	}

	@NotNull
	@Override
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

	@NotNull
	@Deprecated
	@Override
	public CompletableFuture<Void> retrieveMembers() {
		return this.guild.retrieveMembers();
	}

	@NotNull
	@Override
	public Task<Void> loadMembers(@NotNull Consumer<Member> callback) {
		return this.guild.loadMembers(callback);
	}

	@NotNull
	@Override
	public RestAction<Member> retrieveMemberById(long id, boolean update) {
		return this.guild.retrieveMemberById(id, update);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull long... ids) {
		return this.guild.retrieveMembersByIds(includePresence, ids);
	}

	@NotNull
	@Override
	public Task<List<Member>> retrieveMembersByPrefix(@NotNull String prefix, int limit) {
		return this.guild.retrieveMembersByPrefix(prefix, limit);
	}

	@NotNull
	@Override
	public RestAction<Void> moveVoiceMember(@NotNull Member member, @org.jetbrains.annotations.Nullable VoiceChannel voiceChannel) {
		return this.guild.moveVoiceMember(member, voiceChannel);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> modifyNickname(@NotNull Member member, @org.jetbrains.annotations.Nullable String nickname) {
		return this.guild.modifyNickname(member, nickname);
	}

	@NotNull
	@Override
	public AuditableRestAction<Integer> prune(int days, boolean wait, @NotNull Role... roles) {
		return this.guild.prune(days, wait, roles);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> kick(@NotNull Member member, @org.jetbrains.annotations.Nullable String reason) {
		return this.guild.kick(member, reason);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> kick(@NotNull String userId, @org.jetbrains.annotations.Nullable String reason) {
		return this.guild.kick(userId, reason);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(@NotNull User user, int delDays, @org.jetbrains.annotations.Nullable String reason) {
		return this.guild.ban(user, delDays, reason);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> ban(@NotNull String userId, int delDays, @org.jetbrains.annotations.Nullable String reason) {
		return this.guild.ban(userId, delDays, reason);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> unban(@NotNull String userId) {
		return this.guild.unban(userId);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> deafen(@NotNull Member member, boolean deafen) {
		return this.guild.deafen(member, deafen);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> mute(@NotNull Member member, boolean mute) {
		return this.guild.mute(member, mute);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> addRoleToMember(@NotNull Member member, @NotNull Role role) {
		return this.guild.addRoleToMember(member, role);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> removeRoleFromMember(@NotNull Member member, @NotNull Role role) {
		return this.guild.removeRoleFromMember(member, role);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @org.jetbrains.annotations.Nullable Collection<Role> rolesToAdd, @org.jetbrains.annotations.Nullable Collection<Role> rolesToRemove) {
		return this.guild.modifyMemberRoles(member, rolesToAdd, rolesToRemove);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Collection<Role> roles) {
		return this.guild.modifyMemberRoles(member, roles);
	}

	@NotNull
	@Override
	public AuditableRestAction<Void> transferOwnership(@NotNull Member newOwner) {
		return this.guild.transferOwnership(newOwner);
	}

	@NotNull
	@Override
	public ChannelAction<TextChannel> createTextChannel(@NotNull String name, @org.jetbrains.annotations.Nullable Category parent) {
		return this.guild.createTextChannel(name, parent);
	}

	@NotNull
	@Override
	public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name, @org.jetbrains.annotations.Nullable Category parent) {
		return this.guild.createVoiceChannel(name, parent);
	}

	@NotNull
	@Override
	public ChannelAction<StageChannel> createStageChannel(@NotNull String name, @org.jetbrains.annotations.Nullable Category parent) {
		return this.guild.createStageChannel(name, parent);
	}

	@NotNull
	@Override
	public ChannelAction<Category> createCategory(@NotNull String name) {
		return this.guild.createCategory(name);
	}

	@NotNull
	@Override
	public RoleAction createRole() {
		return this.guild.createRole();
	}

	@NotNull
	@Override
	public AuditableRestAction<Emote> createEmote(@NotNull String name, @NotNull Icon icon, @NotNull Role... roles) {
		return this.guild.createEmote(name, icon, roles);
	}

	@NotNull
	@Override
	public ChannelOrderAction modifyCategoryPositions() {
		return this.guild.modifyCategoryPositions();
	}

	@NotNull
	@Override
	public ChannelOrderAction modifyTextChannelPositions() {
		return this.guild.modifyTextChannelPositions();
	}

	@NotNull
	@Override
	public ChannelOrderAction modifyVoiceChannelPositions() {
		return this.guild.modifyVoiceChannelPositions();
	}

	@NotNull
	@Override
	public CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
		return this.guild.modifyVoiceChannelPositions(category);
	}

	@NotNull
	@Override
	public CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
		return this.guild.modifyVoiceChannelPositions(category);
	}

	@NotNull
	@Override
	public RoleOrderAction modifyRolePositions(boolean useAscendingOrder) {
		return this.guild.modifyRolePositions(useAscendingOrder);
	}

	@NotNull
	@Override
	public String getId() {
		return this.guild.getId();
	}

	@Override
	public long getIdLong() {
		return this.guild.getIdLong();
	}

	@NotNull
	@Override
	public OffsetDateTime getTimeCreated() {
		return this.guild.getTimeCreated();
	}

	@Override
	public String toString() {
		return "BlackGuild [guild=" + this.guild + ", selfBlackMember=" + this.selfBlackMember + ", language=" + this.language + ", guildType=" + this.guildType + ", antiSpoilerType=" + this.antiSpoilerType + ", antiSwearType=" + this.antiSwearType + ", antiSwearWhitelist=" + this.antiSwearWhitelist + ", prefix=" + this.prefix + ", joinMessage=" + this.joinMessage + ", joinChannel=" + this.joinChannel + ", leaveMessage=" + this.leaveMessage + ", leaveChannel=" + this.leaveChannel + ", disabledCommands=" + this.disabledCommands + ", suggestionsChannel=" + this.suggestionsChannel + ", autoRoles=" + this.autoRoles + "]";
	}
}