package com.github.black0nion.blackonionbot.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.PrefixInfo;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearSystem;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandBase extends ListenerAdapter {
	
	public static HashMap<String[], Command> commandsArray = new HashMap<>();
	
	public static HashMap<Category, List<Command>> commandsInCategory = new HashMap<>();
	
	public static HashMap<String, Command> commands = new HashMap<>();
	
	public static EventWaiter waiter;

	public static int messagesLastTenSecs = 0;
	public static int commandsLastTenSecs = 0;
	
	private static JSONObject commandsJSON = new JSONObject();
	
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
		final Reflections reflections = new Reflections(Command.class.getPackage().getName());
		final Set<Class<? extends Command>> annotated = reflections.getSubTypesOf(Command.class);

		for (final Class<?> command : annotated)
			try {
				final Command newInstance = (Command) command.getConstructor().newInstance();
				final String[] packageName = command.getPackage().getName().split("\\.");
				final Category parsedCategory = Category.parse(packageName[packageName.length-1]);
				newInstance.setCategory(parsedCategory != null ? parsedCategory : newInstance.getCategory());
				
				if (newInstance.shouldAutoRegister())
					if (newInstance.getCommand() != null)
						addCommand(newInstance);
					else
						System.err.println(newInstance.getClass().getName() + " doesn't have a command!");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		
		Bot.executor.submit(() -> {
			Dashboard.init();
			for (final Map.Entry<Category, List<Command>> entry : commandsInCategory.entrySet()) {
				final JSONArray array = new JSONArray();
				for (final Command command : entry.getValue().stream().filter(cmd -> cmd.getVisibility() == CommandVisibility.SHOWN && cmd.isDashboardCommand()).collect(Collectors.toList())) {				
					final JSONObject commandJSON = new JSONObject();
					commandJSON.put("command", command.getCommand());
					final String translation = LanguageSystem.getDefaultLanguage().getTranslation("help" + command.getCommand()[0]);
					commandJSON.put("description", translation != null ? translation : LanguageSystem.getDefaultLanguage().getTranslationNonNull("empty"));
					commandJSON.put("isToggleable", command.isToggleable());
					if (Dashboard.hasValues(command)) {
						final JSONArray values = new JSONArray();
						for (final DashboardValue value : Dashboard.getValues(command))
							values.put(value.toJSON());
						commandJSON.put("values", values);
					}
					array.put(commandJSON);
				}
				commandsJSON.put(entry.getKey().name(), array);
			}
//			StringSelection stringSelection = new StringSelection(commandsJSON.toString());
//			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//			clipboard.setContents(stringSelection, null);
//			System.out.println(commandsJSON);
		});
	}
	
	@Override
	public void onGuildMessageUpdate(final GuildMessageUpdateEvent event) {
		AntiSwearSystem.check(BlackGuild.from(event.getGuild()), BlackMember.from(event.getMember()), BlackMessage.from(event.getMessage()), event.getChannel());
	}
	
	@Override
	public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
		messagesLastTenSecs++;
		ValueManager.save("messagesSent", ValueManager.getInt("messagesSent") + 1);
		final BlackUser author = BlackUser.from(event.getAuthor());
		if (author.isBot()) return;
		
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackMember member = BlackMember.from(event.getMember());
		final String prefix =  guild.getPrefix();
		final TextChannel channel = event.getChannel();
		final BlackMessage message = BlackMessage.from(event.getMessage());
		final String msgContent = message.getContentRaw();
		final List<Attachment> attachments = message.getAttachments();
		final String attachmentsString = (!attachments.isEmpty() ? attachments.stream().map(at -> at.getUrl()).collect(Collectors.toList()).toString() : "");
		final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + message.getId() + ")" + msgContent.replace("\n", "\\n") + attachmentsString);
		final String[] args = msgContent.split(" ");
		
		Logger.log(LogMode.INFORMATION, LogOrigin.BOT, log);
		FileUtils.appendToFile("files/logs/messagelog/" + guild.getId() + "/" + EmojiParser.parseToAliases(channel.getName()).replaceAll(":([^:\\s]*(?:::[^:\\s]*)*):", "($1)") + "_" + channel.getId() + ".log", author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + message.getId() + ")" + msgContent.replace("\n", "\\n") + attachmentsString);
		
		final boolean containsProfanity = AntiSwearSystem.check(guild, member, message, channel);
		
		final CommandEvent cmde = new CommandEvent(event, guild, message, member, author);
		
		if (AntiSpoilerSystem.removeSpoilers(cmde)) return;
		
		PrefixInfo.handle(cmde);
		
		if (!args[0].startsWith(prefix)) return;
		final String str = args[0].replace(prefix, "");
		if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) return;
		if (commands.containsKey(str)) {
			final Command cmd = commands.get(str);
			FileUtils.appendToFile("files/logs/commandUsages.log", log);
			if (cmd.getRequiredCustomPermissions() != null && !author.hasPermission(cmd.getRequiredCustomPermissions()))
				return;
			
			ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
			commandsLastTenSecs++;
			
			final Permission[] requiredBotPermissions = cmd.getRequiredBotPermissions() != null ? cmd.getRequiredBotPermissions() : new Permission[] {};
			final Permission[] requiredPermissions = cmd.getRequiredPermissions() != null ? cmd.getRequiredPermissions() : new Permission[] {};
			if (Utils.handleRights(guild, author, channel, requiredBotPermissions)) return;
			
			if (!guild.isCommandActivated(cmd)) return;
			
			if (!member.hasPermission(Utils.concatenate(requiredPermissions, requiredBotPermissions))) {
				if (cmd.getVisibility() != CommandVisibility.SHOWN)
					return;
				cmde.error("missingpermissions", cmde.getTranslation("requiredpermissions") + "\n" + Utils.getPermissionString(cmd.getRequiredPermissions()));
				return;
			} else if (Utils.handleRights(guild, author, channel, requiredBotPermissions))
				return;
			else if (cmd.isPremiumCommand() && !guild.getGuildType().higherThanOrEqual(GuildType.PREMIUM)) {
				message.reply(EmbedUtils.premiumRequired(author, guild)).queue();
				return;
			} else if (cmd.getRequiredArgumentCount() + 1 > args.length) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(cmde.getTranslation("wrongargumentcount"), CommandEvent.getPleaseUse(guild, author, cmd), false).build()).queue(msg -> {
							if (cmd.getVisibility() != CommandVisibility.SHOWN) {
								msg.delete().queueAfter(3, TimeUnit.SECONDS);
								message.delete().queueAfter(3, TimeUnit.SECONDS);
							}
						});
				return;
			}
			
			if (containsProfanity) {
				cmde.error("dontexecuteprofanitycommands", "pleaseremoveprofanity");
				return;
			}
			
			Bot.executor.submit(() -> {
				cmde.setCommand(cmd);
				cmd.execute(args, cmde, event, message, member, author, guild, channel);
			});
			return;
		}
		
		// np blaumeise
		// message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("commandnotfound", LanguageSystem.getTranslation("thecommandnotfound", author, guild).replace("%command%", args[0]), false).build()).queue();
	}
	
	@Deprecated
	public static void addCommand(final Command c, final String... command) {
		for (final String s : command)
			if (!commands.containsKey(s))
				commands.put(s, c);
	}
	
	public static void addCommand(final Command c) {
		if (!commandsArray.containsKey(c.getCommand())) {			
			final Category category = c.getCategory();
			final List<Command> commandsInCat = Optional.ofNullable(commandsInCategory.get(category)).orElse(new ArrayList<>());
			commandsInCat.add(c);
			commandsInCategory.put(category, commandsInCat);
			commandsArray.put(c.getCommand(), c);
			
			for (final String command : c.getCommand())
				if (!commands.containsKey(command))
					commands.put(command, c);
		}
	}
}