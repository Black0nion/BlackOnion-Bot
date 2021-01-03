package com.github.ahitm_2020_2025.blackonionbot.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.github.ahitm_2020_2025.blackonionbot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ActivityCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.AvatarCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ClearCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.HelpCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.HypixelCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.NotifyCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.PastebinCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.PingCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ReactionRolesSetupCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ReloadCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.RenameCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ShutdownDBCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.StatsCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.StatusCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.WeatherCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.music.JoinCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.music.LeaveCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.music.PlayCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.music.SkipCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.music.StopCommand;
import com.github.ahitm_2020_2025.blackonionbot.utils.FileUtils;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandBase extends ListenerAdapter {
	
	public static HashMap<String[], Command> commands = new HashMap<>();
	
	public static void addCommands() {
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
				if (event.getMessage().getContentRaw().toLowerCase().startsWith(BotInformation.prefix + str)) {
					String message = dtf.format(now) + " | " + event.getChannel().getName() + " | " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ": " + event.getMessage().getContentRaw();
					FileUtils.appendToFile("commandLog", message);
					ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
					String[] args = event.getMessage().getContentRaw().split(" ");
					commands.get(c).execute(args, event, event.getMessage(), event.getMember(), event.getAuthor(), event.getChannel());
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
