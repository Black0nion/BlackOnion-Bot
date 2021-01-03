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
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.NNNCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.NotifyCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.PastebinCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.PingCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ReactionRolesSetupCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ReloadCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.RenameCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.ShutdownDBCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.StatsCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.impl.StatusCommand;
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
		addCommand(new ActivityCommand(), "activity");
		addCommand(new AvatarCommand(), "avatar", "pb");
		addCommand(new ClearCommand(), "clear");
		addCommand(new HelpCommand(), "help");
		addCommand(new NotifyCommand(), "notify");
		addCommand(new PingCommand(), "ping", "poing");
		addCommand(new ReloadCommand(), "reload", "rl");
		addCommand(new StatusCommand(), "status");
		addCommand(new JoinCommand(), "join");
		addCommand(new PlayCommand(), "play");
		addCommand(new StopCommand(), "stop");
		addCommand(new SkipCommand(), "skip");
		addCommand(new LeaveCommand(), "leave");
		addCommand(new NNNCommand(), "nnn", "nonutnovember");
		addCommand(new ShutdownDBCommand(), "shutdowndb", "dbshutdown");
		addCommand(new ReactionRolesSetupCommand(), "rr", "reactionrole");
		addCommand(new PastebinCommand(), "pastebin");
		addCommand(new HypixelCommand(), "hypixel");
		addCommand(new RenameCommand(), "rename", "rn");
		addCommand(new StatsCommand(), "stats");
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
}
