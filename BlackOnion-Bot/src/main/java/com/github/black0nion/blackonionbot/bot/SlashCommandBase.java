package com.github.black0nion.blackonionbot.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearSystem;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.logging.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SlashCommandBase extends ListenerAdapter {

    public static HashMap<Category, List<SlashCommand>> commandsInCategory = new HashMap<>();

    public static HashMap<String, Pair<Long, SlashCommand>> commands = new HashMap<>();

    public static EventWaiter waiter;

    /**
     * Don't call on init!
     */
    @Reloadable("commands")
    public static void addCommands() {
	addCommands(waiter);
    }

    public static void addCommands(final EventWaiter newWaiter) {
	commands.clear();
	commandsInCategory.clear();
	waiter = newWaiter;
	final Reflections reflections = new Reflections(SlashCommand.class.getPackage().getName());
	final Set<Class<? extends SlashCommand>> annotated = reflections.getSubTypesOf(SlashCommand.class);

	for (final Class<?> command : annotated) {
	    try {
		final SlashCommand newInstance = (SlashCommand) command.getConstructor().newInstance();
		final String[] packageName = command.getPackage().getName().split("\\.");
		final Category parsedCategory = Category.parse(packageName[packageName.length - 1]);
		newInstance.setCategory(parsedCategory != null ? parsedCategory : newInstance.getCategory());

		if (newInstance.shouldAutoRegister()) if (newInstance.getData() != null) {
		    addCommand(newInstance);
		} else {
		    System.err.println(newInstance.getClass().getName() + " doesn't have a command!");
		}
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
	// TODO: uncomment
	// Bot.jda.updateCommands().addCommands(commands.values().stream().map(Pair::getValue).map(SlashCommand::getData).collect(Collectors.toList())).queue();

	Bot.executor.submit(() -> {
	    Dashboard.init();
	    // StringSelection stringSelection = new
	    // StringSelection(commandsJSON.toString());
	    // Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    // clipboard.setContents(stringSelection, null);
	    // System.out.println(commandsJSON);
	});
    }

    @Override
    public void onGuildMessageUpdate(final GuildMessageUpdateEvent event) {
	AntiSwearSystem.check(BlackGuild.from(event.getGuild()), BlackMember.from(event.getMember()), BlackMessage.from(event.getMessage()), event.getChannel());
    }

    @Override
    public void onSlashCommand(final net.dv8tion.jda.api.events.interaction.SlashCommandEvent event) {
	StatisticsManager.commandExecuted();
	ValueManager.save("messagesSent", ValueManager.getInt("messagesSent") + 1);
	final BlackUser author = BlackUser.from(event.getUser());
	if (author.isBot()) return;

	final BlackGuild guild = BlackGuild.from(event.getGuild());
	final BlackMember member = BlackMember.from(event.getMember());
	final TextChannel channel = event.getTextChannel();
	final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + event.getId() + ")" + event.getOptions().stream().map(OptionMapping::toString).collect(Collectors.joining(" ")).replace("\n", "\\n"));

	Logger.log(LogMode.INFORMATION, LogOrigin.DISCORD, log);
	FileUtils.appendToFile("files/logs/messagelog/" + guild.getId() + "/" + EmojiParser.parseToAliases(channel.getName()).replaceAll(":([^:\\s]*(?:::[^:\\s]*)*):", "($1)").replace(":", "_") + "_" + channel.getId() + ".log", log);

	final SlashCommandExecutedEvent cmde = new SlashCommandExecutedEvent(event, guild, member, author);

	if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) return;
	if (commands.containsKey(event.getName())) {
	    final SlashCommand cmd = commands.get(event.getName()).getValue();
	    FileUtils.appendToFile("files/logs/commandUsages.log", log);
	    if (cmd.getRequiredCustomPermissions() != null && !author.hasPermission(cmd.getRequiredCustomPermissions())) {
		cmde.errorPrivate("missingpermissions", cmde.getTranslation("requiredcustompermissions") + "\n" + Utils.getPermissionString(cmd.getRequiredCustomPermissions()));
		return;
	    }

	    ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
	    StatisticsManager.commandExecuted();

	    final Permission[] requiredBotPermissions = cmd.getRequiredBotPermissions() != null ? cmd.getRequiredBotPermissions() : new Permission[] {};
	    final Permission[] requiredPermissions = cmd.getRequiredPermissions() != null ? cmd.getRequiredPermissions() : new Permission[] {};
	    if (Utils.handleRights(guild, author, channel, requiredBotPermissions)) return;

	    if (!member.hasPermission(Utils.concatenate(requiredPermissions, requiredBotPermissions))) {
		if (!cmd.isVisible(author)) return;
		cmde.error("missingpermissions", cmde.getTranslation("requiredpermissions") + "\n" + Utils.getPermissionString(cmd.getRequiredPermissions()));
		return;
	    } else if (Utils.handleRights(guild, author, channel, requiredBotPermissions)) return;
	    else if (cmd.isPremiumCommand() && !guild.getGuildType().higherThanOrEqual(GuildType.PREMIUM)) {
		event.replyEmbeds(EmbedUtils.premiumRequired(author, guild)).queue();
		return;
	    }

	    Bot.executor.submit(() -> {
		cmde.setCommand(cmd);
		cmd.execute(cmde, event, member, author, guild, channel);
	    });
	    return;
	}

	// np blaumeise
	// message.reply(EmbedUtils.getErrorEmbed(author,
	// guild).addField("commandnotfound",
	// LanguageSystem.getTranslation("thecommandnotfound", author,
	// guild).replace("%command%", args[0]), false).build()).queue();
    }

    public static void addCommand(final SlashCommand c) {
	final String commandName = c.getData().getName();
	if (!commands.containsKey(commandName)) {
	    final Category category = c.getCategory();
	    final List<SlashCommand> commandsInCat = Optional.ofNullable(commandsInCategory.get(category)).orElse(new ArrayList<>());
	    commandsInCat.add(c);
	    commandsInCategory.put(category, commandsInCat);

	    commands.put(commandName, new Pair<>(null, c));
	}
    }
}