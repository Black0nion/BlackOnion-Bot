package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.GenericCommand;
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
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.GuildImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BlackGuild extends GuildImpl {

  private BlackMember selfBlackMember;

  private static final LoadingCache<Guild, BlackGuild> guilds =
      CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<>() {
        @Override
        public @NotNull BlackGuild load(final @NotNull Guild guild) {
          return new BlackGuild(guild);
        }
      });

  @Deprecated
  @Reloadable("guildcache")
  public static void clearCache() {
    guilds.invalidateAll();
  }

  public static BlackGuild from(@Nullable final Guild guild) {
    if (guild == null)
      return null;
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
    final Optional<Entry<Guild, BlackGuild>> first = guilds.asMap().entrySet().stream()
        .filter(entry -> entry.getKey().getIdLong() == guildid).findFirst();
    return first.isPresent() ? first.get().getValue()
        : from(Bot.getInstance().getJda().getGuildById(guildid));
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
  @Nullable
  private List<GenericCommand> disabledCommands;
  private long suggestionsChannel;
  private List<Long> autoRoles;
  private boolean loop;
  private HashMap<String, CustomCommand> customCommands;

  private BlackGuild(@NotNull final Guild guild) {
    super(guild);

    try {
      Document config = configs.find(this.getIdentifier()).first();

      if (config == null) {
        config = new Document();
      }

      // only to debug the configs
      this.save("name", this.guild.getName());
      this.language = LanguageSystem.getLanguageFromName(config.getString("language"));
      final Language langNonNull =
          language != null ? language : LanguageSystem.getDefaultLanguage();
      this.guildType = Utils.gOD(GuildType.parse(config.getString("guildtype")), GuildType.NORMAL);
      this.prefix = Utils.gOD(config.getString("prefix"), Config.prefix);
      this.antiSpoilerType =
          Utils.gOD(AntiSpoilerType.parse(config.getString("antispoiler")), AntiSpoilerType.OFF);
      this.antiSwearType =
          Utils.gOD(AntiSwearType.parse(config.getString("antiswear")), AntiSwearType.OFF);
      this.joinMessage = Utils.gOD(config.getString("joinmessage"),
          langNonNull.getTranslationNonNull("defaultjoinmessage"));
      this.joinChannel = Utils.gOD(config.getLong("joinchannel"), -1L);
      this.leaveMessage = Utils.gOD(config.getString("leavemessage"),
          langNonNull.getTranslationNonNull("defaultleavemessage"));
      this.leaveChannel = Utils.gOD(config.getLong("leavechannel"), -1L);
      this.suggestionsChannel = Utils.gOD(config.getLong("suggestionschannel"), -1L);
      this.autoRoles = Utils.gOD(config.getList("autoroles", Long.class), new ArrayList<>());
      this.antiSwearWhitelist =
          Utils.gOD(config.getList("antiswearwhitelist", String.class), new ArrayList<>());
      this.loop = Utils.gOD(config.getBoolean("loop"), false);
      this.customCommands = new HashMap<>();
      Utils
          .gOD(
              Utils.gOD(config.getList("customcommands", Document.class), new ArrayList<Document>())
                  .stream().map(cmd -> new CustomCommand(this, cmd)).collect(Collectors.toList()),
              new ArrayList<CustomCommand>())
          .forEach(cmd -> this.customCommands.put(cmd.getCommand(), cmd));
      final List<String> disabledCommandsString = config.getList("disabledcommands", String.class);
      if (disabledCommandsString != null)
        setDisabledCommands(disabledCommandsString.toArray(String[]::new));
    } catch (final Exception e) {
      e.printStackTrace();
    }
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
    if (language != null)
      this.save("language", language.getLanguageCode());
    else
      this.clear("language");
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

  @ConfigGetter(key = "leavechannel", requiredPermissions = {Permission.MANAGE_SERVER},
      description = "Set the Channel the Message on leave should be sent to")
  public TextChannel getLeaveChannelAsChannel() {
    if (this.leaveChannel == -1)
      return null;
    return this.guild.getTextChannelById(this.leaveChannel);
  }

  @DashboardSetter(value = "utils.joinleave.leave.channel", nullable = true)
  @ConfigSetter(argumentsDescription = {"The Channel where the message should get sent to"},
      key = "leavechannel", requiredPermissions = {Permission.MANAGE_SERVER}, nullable = true)
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

  public @Nullable List<GenericCommand> getDisabledCommands() {
    return this.disabledCommands;
  }

  public void setDisabledCommands(final String[] disabledCommands) {
    this.setDisabledCommands(Arrays
        .stream(
            disabledCommands)
        .map(cmd -> Optional.ofNullable((GenericCommand) CommandBase.commands.get(cmd))
            .orElse(Optional.ofNullable(SlashCommandBase.commands.get(cmd)).map(Pair::getValue)
                .orElse(null)))
        .filter(Objects::nonNull).collect(Collectors.toList()));
  }

  public void setDisabledCommands(final List<GenericCommand> disabledCommands) {
    this.disabledCommands = disabledCommands;
    if (disabledCommands == null)
      this.clear("disabledcommands");
    else
      this.saveList("disabledcommands",
          disabledCommands.stream().map(GenericCommand::getName).collect(Collectors.toList()));
  }

  public boolean isCommandActivated(final GenericCommand cmd) {
    return this.disabledCommands == null || !this.disabledCommands.contains(cmd);
  }

  public boolean setCommandActivated(final GenericCommand cmd, final boolean activated) {
    if (!cmd.isToggleable())
      return false;
    if (this.disabledCommands == null) {
      if (activated)
        return true;
      else
        this.disabledCommands = new ArrayList<>();
    }

    if (!activated) {
      this.disabledCommands.add(cmd);
    } else {
      this.disabledCommands.remove(cmd);
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

  @SuppressWarnings("UnusedReturnValue")
  public boolean addCustomCommand(final CustomCommand cmd) {
    if (this.customCommands.containsKey(cmd.getCommand()))
      return false;
    this.customCommands.put(cmd.getCommand(), cmd);
    this.save("customcommands", this.customCommands.values().stream().map(CustomCommand::toDocument)
        .collect(Collectors.toList()));
    return true;
  }

  public void deleteCustomCommand(final String commandName) {
    this.customCommands.remove(commandName);
    this.save("customcommands", this.customCommands.values().stream().map(CustomCommand::toDocument)
        .collect(Collectors.toList()));
  }

  public String getEscapedName() {
    return Utils.escapeMarkdown(this.getName());
  }

  @Override
  protected Document getIdentifier() {
    return new Document("guildid", this.guild.getIdLong());
  }

  public static final MongoCollection<Document> configs =
      MongoDB.DATABASE.getCollection("guildsettings");

  @Override
  protected MongoCollection<Document> getCollection() {
    return configs;
  }

  @Override
  public String toString() {
    return "BlackGuild{" + "selfBlackMember=" + selfBlackMember + ", language=" + language
        + ", guildType=" + guildType + ", antiSpoilerType=" + antiSpoilerType + ", antiSwearType="
        + antiSwearType + ", antiSwearWhitelist=" + antiSwearWhitelist + ", prefix='" + prefix
        + '\'' + ", joinMessage='" + joinMessage + '\'' + ", joinChannel=" + joinChannel
        + ", leaveMessage='" + leaveMessage + '\'' + ", leaveChannel=" + leaveChannel
        + ", disabledCommands=" + disabledCommands + ", suggestionsChannel=" + suggestionsChannel
        + ", autoRoles=" + autoRoles + ", loop=" + loop + ", customCommands=" + customCommands
        + '}';
  }
}
