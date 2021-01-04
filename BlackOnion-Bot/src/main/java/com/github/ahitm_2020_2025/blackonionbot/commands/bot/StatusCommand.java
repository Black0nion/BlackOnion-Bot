package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatusCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		message.delete().queue();
		OnlineStatus status = getStatusFromFile();
		switch (args[1]) {
		case "online":
			ValueManager.save("status", "online");
			status = OnlineStatus.ONLINE;
			break;
		case "invisible":
		case "offline":
			ValueManager.save("status", "offline");
			status = OnlineStatus.OFFLINE;
			break;
		case "idle":
		case "afk":
			ValueManager.save("status", "afk");
			status = OnlineStatus.IDLE;
			break;
		case "dnd":
		case "donotdisturb":
			ValueManager.save("status", "dnd");
			status = OnlineStatus.DO_NOT_DISTURB;
			break;
		default:
			e.getChannel().sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Status setzen fehlgeschlagen!", "Bitte benutze [``online``], [``invisible``, ``offline``], [``idle``, ``afk``] oder [``dnd``, ``donotdisturb``]", false).build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
			return;
		}
		e.getJDA().getPresence().setStatus(status);
		e.getChannel().sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).addField("Erfolgreich Status gesetzt!", "Neuer Status: **" + status + "**", false).build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
	}

	@Override
	public String getDescription() {
		return "Setzt den Status des Bots";
	}

	@Override
	public String getSyntax() {
		return "[online | invisible, offline | idle, afk | dnd, donotdisturb]";
	}

	@Override
	public boolean requiresBotAdmin() {
		return true;
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	public static OnlineStatus getStatusFromFile() {
		String statusType = ValueManager.getString("status");
		if(statusType.contains("online")) {
			return OnlineStatus.ONLINE;
		} else if (statusType.contains("afk")) {
			return OnlineStatus.IDLE;
		} else if (statusType.contains("dnd")) {
			return OnlineStatus.DO_NOT_DISTURB;
		} else if (statusType.contains("offline")) {
			return OnlineStatus.OFFLINE;
		} else {
			System.out.println("Could not read Status from File!");
			return OnlineStatus.DO_NOT_DISTURB;
		}
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"status"};
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
}
