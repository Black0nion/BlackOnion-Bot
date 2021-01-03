package com.github.ahitm_2020_2025.blackonionbot.commands.impl;

import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisisbility;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatusCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (!member.hasPermission(Permission.MANAGE_SERVER)) {
			channel.sendMessage("Du darfst das nicht!").queue();
		} else {
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
			}
			e.getJDA().getPresence().setStatus(status);
			e.getChannel().sendMessage("Der Status wurde auf **" + status + "** gesetzt!\nACHTUNG: Es könnte eine Zeit dauern, bis der Status von Discord übernommen wurde!").queue();
		}
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
	public CommandVisisbility getVisisbility() {
		return CommandVisisbility.HIDDEN;
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
}
