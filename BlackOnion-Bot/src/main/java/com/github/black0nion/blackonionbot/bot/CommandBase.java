package com.github.black0nion.blackonionbot.bot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.github.black0nion.blackonionbot.commands.bot.*;
import com.github.black0nion.blackonionbot.commands.music.*;
import com.github.black0nion.blackonionbot.commands.old.*;
import com.github.black0nion.blackonionbot.commands.fun.*;
import com.github.black0nion.blackonionbot.commands.misc.*;
import com.github.black0nion.blackonionbot.commands.moderation.*;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandBase extends ListenerAdapter {
	
	public static HashMap<String[], Command> commands = new HashMap<>();
	
	private static String prefix = BotInformation.prefix;
	
	public static EventWaiter waiter;
	
	public static void addCommands(EventWaiter newWaiter) {
		waiter = newWaiter;
		addCommand(new ActivityCommand());
		addCommand(new AvatarCommand());
		addCommand(new ClearCommand());
		addCommand(new HelpCommand());
		addCommand(new NotifyCommand());
		addCommand(new PingCommand());
		addCommand(new ReloadCommand());
		addCommand(new StatusCommand());
		addCommand(new JoinCommand());
		addCommand(new PlayCommand());
		addCommand(new StopCommand());
		addCommand(new SkipCommand());
		addCommand(new LeaveCommand());
		addCommand(new ShutdownDBCommand());
		addCommand(new ReactionRolesSetupCommand());
		addCommand(new PastebinCommand());
		addCommand(new HypixelCommand());
		addCommand(new RenameCommand());
		addCommand(new StatsCommand());
		addCommand(new WeatherCommand());
		addCommand(new InstagramCommand());
		addCommand(new AdminHelpCommand());
		addCommand(new TestCommand());
		addCommand(new ConnectFourCommand(waiter));
		addCommand(new SupportCommand());
		addCommand(new LanguageCommand());
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		System.out.println("Message received: " + event.getMessage().getContentRaw());
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		
		for (String[] c : commands.keySet()) {
			for (String str : c) {
				if (event.getMessage().getContentRaw().toLowerCase().split(" ")[0].equalsIgnoreCase(prefix + str)) {
					String message = dtf.format(now) + " | " + event.getChannel().getName() + " | " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ": " + event.getMessage().getContentRaw();
					FileUtils.appendToFile("commandLog", message);
					ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
					String[] args = event.getMessage().getContentRaw().split(" ");
					Command cmd = commands.get(c);
					if (cmd.dmCommand() && event.isFromType(ChannelType.PRIVATE)) {
						event.getChannel().sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor()).setDescription("This command can't be accessed through private chat! Use it on a server!").build()).queue();
						continue;
					}
					if (cmd.requiresBotAdmin() && !BotSecrets.isAdmin(event.getAuthor().getIdLong())) {
						continue;
					} else if (cmd.getRequiredPermissions() != null && cmd.getVisisbility() == CommandVisibility.SHOWN && !event.getMember().hasPermission(cmd.getRequiredPermissions())) {
						event.getChannel().sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor())
								.addField("Missing Permissions!", "Required Permissions: " + cmd.getRequiredPermissions(), false).build()).queue();
						continue;
					} else if (cmd.getRequiredArgumentCount() + 1 > args.length) {
						event.getChannel().sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor())
								.addField("Wrong argument count!", "Syntax: " + prefix + str + (cmd.getSyntax().equals("") ? "" : " " + cmd.getSyntax()), false).build()).queue();
						continue;
					}
					cmd.execute(args, event, event.getMessage(), event.getMember(), event.getAuthor(), event.getGuild(), event.getChannel());
				}
			}
		}
	}
	
	public static void addCommand(Command c, String... command) {
		if (!commands.containsKey(command))
			commands.put(command, c);
	}
	
	public static void addCommand(Command c) {
		if (!commands.containsKey(c.getCommand()))
			commands.put(c.getCommand(), c);
	}
}