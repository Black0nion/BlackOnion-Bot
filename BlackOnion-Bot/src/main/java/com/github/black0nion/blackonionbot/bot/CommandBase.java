package com.github.black0nion.blackonionbot.bot;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.misc.DontAutoRegister;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.systems.ContentModeratorSystem;
import com.github.black0nion.blackonionbot.systems.ToggleAPI;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.dashboard.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.DashboardValueType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandBase extends ListenerAdapter {
	
	public static HashMap<String[], Command> commandsArray = new HashMap<>();
	
	public static HashMap<Category, List<Command>> commandsInCategory = new HashMap<>();
	
	public static HashMap<String, Command> commands = new HashMap<>();
	
	public static EventWaiter waiter;

	public static int commandsLastTenSecs = 0;
	
	private static JSONObject commandsJSON = new JSONObject();
	
	public static void addCommands(EventWaiter newWaiter) {
		commands.clear();
		waiter = newWaiter;
		Reflections reflections = new Reflections(Command.class.getPackage().getName());
		Set<Class<? extends Command>> annotated = reflections.getSubTypesOf(Command.class);

		for (Class<?> command : annotated) {
			try {
				if (command.getDeclaredAnnotationsByType(DontAutoRegister.class).length >= 1) continue;
				addCommand((Command) command.getConstructor().newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Bot.executor.submit(() -> {
			Dashboard.init();
			for (Map.Entry<Category, List<Command>> entry : commandsInCategory.entrySet()) {
				JSONArray array = new JSONArray();
				for (Command command : entry.getValue().stream().filter(cmd -> cmd.getVisisbility() == CommandVisibility.SHOWN && cmd.isDashboardCommand()).collect(Collectors.toList())) {				
					JSONObject commandJSON = new JSONObject();
					commandJSON.put("command", command.getCommand());
					commandJSON.put("description", LanguageSystem.getTranslatedString("help" + command.getCommand()[0], LanguageSystem.getDefaultLanguage()));
					commandJSON.put("isToggleable", command.isToggleable());
					if (Dashboard.hasValues(command)) {
						JSONArray values = new JSONArray();
						for (DashboardValue value : Dashboard.getValues(command)) {
							JSONObject valueObject = new JSONObject();
							valueObject.put("databaseKey", value.getDatabaseKey());
							valueObject.put("prettyName", value.getPrettyName());
							valueObject.put("type", value.getType().name());
							if (value.getType() == DashboardValueType.MULTIPLE_CHOICE) {
								valueObject.put("possibleValues", new JSONObject(value.getMultipleChoice()));
							}
							values.put(valueObject);
						}
						commandJSON.put("values", values);
					}
					array.put(commandJSON);
				}
				commandsJSON.put(entry.getKey().name(), array);
			}
//			StringSelection stringSelection = new StringSelection(commandsJSON.toString());
//			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//			clipboard.setContents(stringSelection, null);
			System.out.println(commandsJSON);
		});
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		final User author = event.getAuthor();
		if (author.isBot()) return;
		
		final Guild guild = event.getGuild();
		final String prefix = BotInformation.getPrefix(guild);
		final TextChannel channel = event.getChannel();
		final Member member = event.getMember();
		final Message message = event.getMessage();
		final String msgContent = message.getContentRaw();
		final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + message.getId() + ")" + msgContent.replace("\n", "\\n"));
		final String[] args = msgContent.split(" ");
		
		Logger.log(LogMode.INFORMATION, LogOrigin.BOT, log);
		
		final boolean containsProfanity = ContentModeratorSystem.checkMessageForProfanity(event);
		
		if (AntiSpoilerSystem.removeSpoilers(event)) return;
		
		if (!args[0].startsWith(BotInformation.getPrefix(guild))) return;
		String str = args[0].replace(prefix, "");
		if (commands.containsKey(str)) {
			Command cmd = commands.get(str);
			FileUtils.appendToFile("commandLog", log);
			ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
			commandsLastTenSecs++;
			if (cmd.requiresBotAdmin() && !BotSecrets.isAdmin(author.getIdLong())) {
				return;
			}
			
			if (!ToggleAPI.isActivated(guild.getId(), cmd)) return;
			
			if (cmd.getRequiredPermissions() != null && !member.hasPermission(cmd.getRequiredPermissions())) {
				if (cmd.getVisisbility() != CommandVisibility.SHOWN)
					return;
				channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild)
						.addField(LanguageSystem.getTranslatedString("missingpermissions", author, guild), LanguageSystem.getTranslatedString("requiredpermissions", author, guild) + "\n" + getPermissionString(cmd.getRequiredPermissions()), false).build()).queue();
				return;
			} else if (cmd.getRequiredArgumentCount() + 1 > args.length) {
				channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild)
						.addField(LanguageSystem.getTranslatedString("wrongargumentcount", author, guild), "Syntax: " + prefix + str + (cmd.getSyntax().equals("") ? "" : " " + cmd.getSyntax()), false).build()).queue();
				return;
			}
			
			if (containsProfanity) {
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("dontexecuteprofanitycommands", "pleaseremoveprofanity", false).build()).queue();
				return;
			}
			
			Bot.executor.submit(() -> {
				cmd.execute(args, event, message, member, author, guild, channel);
			});
			return;
		}
		
		channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("commandnotfound", LanguageSystem.getTranslatedString("thecommandnotfound", author, guild).replace("%command%", args[0]), false).build()).queue();
	}
	
	@Deprecated
	public static void addCommand(Command c, String... command) {
		for (String s : command) {
			if (!commands.containsKey(s)) {
				commands.put(s, c);				
			}
		}
	}
	
	public static void addCommand(Command c) {
		if (!commandsArray.containsKey(c.getCommand())) {			
			final Category category = c.getCategory();
			final List<Command> commandsInCat = Optional.ofNullable(commandsInCategory.get(category)).orElse(new ArrayList<>());
			commandsInCat.add(c);
			commandsInCategory.put(category, commandsInCat);
			commandsArray.put(c.getCommand(), c);
			
			for (String command : c.getCommand()) {
				if (!commands.containsKey(command)) {
					commands.put(command, c);
				}
			}
		}
	}
	
	public static String getPermissionString(Permission[] permissions) {
		String output = "```";
		for (int i = 0; i  < permissions.length; i++) {
			output += "- " + permissions[i].getName() + (i == permissions.length-1 ? "```" : "\n");
		}
		return output;
	}
}
