package com.github.black0nion.blackonionbot.systems;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageLogSystem extends ListenerAdapter {
	public static ArrayList<String> logChannels = new ArrayList<>();
	
	File logsFile;
	
	public static void init() {
		try {
			File logFile = new File("files/logChannels.ahitm");
			logFile.createNewFile();
			Path path = Paths.get("files/logChannels.ahitm");
			List<String> lines;
			lines = Files.lines(path).collect(Collectors.toList());
			logChannels.addAll(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMessageReceived(final MessageReceivedEvent event) {
		ValueManager.save("messagesSent", ValueManager.getInt("messagesSent") + 1);
		if (event.getAuthor().isBot() || !logChannels.contains(event.getChannel().getId()))
			return;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		new File("files/logs/channels/").mkdir();
		FileUtils.appendToFile("logs/channels/" + event.getChannel().getName(), dtf.format(now) + " | " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ": " + event.getMessage().getContentRaw());
	}
}
