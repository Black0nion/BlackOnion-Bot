package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.PrefixInfo;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.admin.BanUsageCommand;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.systems.CustomCommand;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearSystem;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.mongodb.client.model.Filters;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandBase extends ListenerAdapter {

  public static final HashMap<String[], TextCommand> commandsArray = new HashMap<>();

  public static final HashMap<Category, List<TextCommand>> commandsInCategory = new HashMap<>();

  public static final HashMap<String, TextCommand> commands = new HashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(CommandBase.class);

  public static void addCommands() {
    commands.clear();
    commandsInCategory.clear();
    commandsArray.clear();
    final Reflections reflections = new Reflections(TextCommand.class.getPackage().getName());
    final Set<Class<? extends TextCommand>> annotated =
        reflections.getSubTypesOf(TextCommand.class);

    for (final Class<?> command : annotated) {
      try {
        final TextCommand newInstance = (TextCommand) command.getConstructor().newInstance();
        if (newInstance.getCategory() == null) {
          final String[] packageName = command.getPackage().getName().split("\\.");
          final Category parsedCategory = Category.parse(packageName[packageName.length - 1]);
          newInstance.setCategory(parsedCategory != null ? parsedCategory : Category.OTHER);
        }
        newInstance.setCommand(Arrays.stream(newInstance.getCommand()).filter(Objects::nonNull)
            .map(String::toLowerCase).toArray(String[]::new));

        if (newInstance.getCommand() != null) {
          addCommand(newInstance);
        } else {
          System.err.println(newInstance.getClass().getName() + " doesn't have a command!");
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onMessageUpdate(final MessageUpdateEvent event) {
    AntiSwearSystem.check(BlackGuild.from(event.getGuild()), BlackMember.from(event.getMember()),
        event.getMessage(), event.getTextChannel());
  }

  @Override
  public void onMessageReceived(final MessageReceivedEvent event) {
    if (event.getAuthor().isBot() || event.getChannelType() != ChannelType.TEXT)
      return;
    final BlackUser author = BlackUser.from(event.getAuthor());

    final BlackGuild guild = BlackGuild.from(event.getGuild());
    final BlackMember member = BlackMember.from(event.getMember());
    final String prefix = guild.getPrefix();
    final TextChannel channel = event.getTextChannel();

    StatisticsManager.MESSAGES_SENT
        .labels(guild.getId(), guild.getName(), channel.getId(), channel.getName()).inc();
    StatisticsManager.TOTAL_MESSAGES_SENT.inc();

    final Message message = event.getMessage();
    final String msgContent = message.getContentRaw();
    final List<Attachment> attachments = message.getAttachments();
    final String attachmentsString =
        (!attachments.isEmpty() ? attachments.stream().map(Attachment::getUrl).toList().toString()
            : "");
    final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > "
        + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#"
        + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + message.getId() + ")"
        + msgContent.replace("\n", "\\n") + attachmentsString);
    final String[] args = msgContent.split(" ");

    boolean locked =
        BanUsageCommand.collection.find(Filters.or(Filters.eq("guildid", guild.getIdLong()),
            Filters.eq("userid", author.getIdLong()))).first() != null;
    if (Config.run_mode == RunMode.DEV) {
      if (locked)
        logger.warn(log);
      else
        logger.info(log);
      FileUtils.appendToFile(
          "files/logs/messagelog/" + guild.getId() + "/"
              + EmojiParser.parseToAliases(channel.getName())
                  .replaceAll(":([^:\\s]*(?:::[^:\\s]*)*):", "($1)").replace(":", "_")
              + "_" + channel.getId() + ".log",
          author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:"
              + message.getId() + ")" + msgContent.replace("\n", "\\n") + attachmentsString);
    }

    if (locked)
      return;

    final boolean containsProfanity = AntiSwearSystem.check(guild, member, message, channel);

    final CommandEvent cmde = new CommandEvent(event, guild, message, member, author);

    if (AntiSpoilerSystem.removeSpoilers(cmde))
      return;

    PrefixInfo.handle(cmde);

    if (!args[0].startsWith(prefix)
        && !args[0].equalsIgnoreCase(event.getJDA().getSelfUser().getAsMention()))
      return;
    final String str = args[0].replace(prefix, "").toLowerCase();
    if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_SEND))
      return;
    if (commands.containsKey(str)) {
      final TextCommand cmd = commands.get(str);
      FileUtils.appendToFile("files/logs/commandUsages.log", log);
      if (cmd.getRequiredCustomPermissions() != null
          && !author.hasPermission(cmd.getRequiredCustomPermissions()))
        return;

      StatisticsManager.COMMANDS_EXECUTED.labels("text", cmd.getName(), guild.getId(),
          guild.getName(), channel.getId(), channel.getName()).inc();
      StatisticsManager.TOTAL_COMMANDS_EXECUTED.inc();

      final Permission[] requiredBotPermissions =
          cmd.getRequiredBotPermissions() != null ? cmd.getRequiredBotPermissions()
              : new Permission[] {};
      final Permission[] requiredPermissions =
          cmd.getRequiredPermissions() != null ? cmd.getRequiredPermissions() : new Permission[] {};
      if (Utils.handleRights(guild, author, channel, requiredBotPermissions))
        return;

      if (!guild.isCommandActivated(cmd))
        return;

      assert member != null;
      if (!member.hasPermission(requiredPermissions)) {
        if (!cmd.isVisible(author))
          return;
        cmde.error("missingpermissions", cmde.getTranslation("requiredpermissions") + "\n"
            + Utils.getPermissionString(cmd.getRequiredPermissions()));
        return;
      } else if (Utils.handleRights(guild, author, channel, requiredBotPermissions))
        return;
      else if (cmd.isPremiumCommand()
          && !guild.getGuildType().higherThanOrEqual(GuildType.PREMIUM)) {
        message.replyEmbeds(EmbedUtils.premiumRequired(author, guild)).queue();
        return;
      } else if (cmd.getRequiredArgumentCount() + 1 > args.length) {
        message
            .replyEmbeds(
                EmbedUtils.getErrorEmbed(author, guild)
                    .addField(cmde.getTranslation("wrongargumentcount"),
                        CommandEvent.getPleaseUse(guild, author, cmd), false)
                    .build())
            .queue(msg -> {
              final CustomPermission[] customPermission = cmd.getRequiredCustomPermissions();
              if (customPermission != null && customPermission.length != 0) {
                msg.delete().queueAfter(3, TimeUnit.SECONDS);
                message.delete().queue();
              }
            });
        return;
      }

      if (containsProfanity) {
        cmde.error("dontexecuteprofanitycommands", "pleaseremoveprofanity");
        return;
      }

      Bot.getInstance().getExecutor().submit(() -> {
        cmde.setCommand(cmd);
        cmd.execute(args, cmde, event, message, member, author, guild, channel);
      });

      return;
    }

    // no command found
    final CustomCommand cmd = guild.getCustomCommands().get(str);
    if (cmd != null) {
      cmd.handle(event);
    }
  }

  @Deprecated
  public static void addCommand(final TextCommand c, final String... command) {
    for (final String s : command)
      if (!commands.containsKey(s)) {
        commands.put(s, c);
      }
  }

  public static void addCommand(final TextCommand c) {
    if (!commandsArray.containsKey(c.getCommand())) {
      final Category category = c.getCategory();
      final List<TextCommand> commandsInCat =
          Optional.ofNullable(commandsInCategory.get(category)).orElse(new ArrayList<>());
      commandsInCat.add(c);
      commandsInCategory.put(category, commandsInCat);
      commandsArray.put(c.getCommand(), c);

      for (final String command : c.getCommand())
        if (!commands.containsKey(command)) {
          commands.put(command, c);
        }
    }
  }
}
