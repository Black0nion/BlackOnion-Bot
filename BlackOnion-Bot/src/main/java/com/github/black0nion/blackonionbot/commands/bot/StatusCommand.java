package com.github.black0nion.blackonionbot.commands.bot;

import java.time.Duration;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StatusCommand extends Command {
	
	public StatusCommand() {
		this.setCommand("status")
			.setSyntax("[online | invisible, offline | idle, afk | dnd, donotdisturb]")
			.setRequiredCustomPermissions(CustomPermission.ADMIN)
			.setHidden()
			.setRequiredArgumentCount(1);
	}
	
	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
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
			// TODO: delete after x seconds
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("statussetfail", CommandEvent.getPleaseUse(guild, author, this), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			return;
		}
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("statussetsuccess", LanguageSystem.getTranslation("newstatus", author, guild) + ": **" + status.name().toUpperCase() + "**", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		
		e.getJDA().getPresence().setStatus(status);
	}
	
	public static OnlineStatus getStatusFromFile() {
		final String statusType = ValueManager.getString("status");
		if(statusType.contains("online"))
			return OnlineStatus.ONLINE;
		else if (statusType.contains("afk"))
			return OnlineStatus.IDLE;
		else if (statusType.contains("dnd"))
			return OnlineStatus.DO_NOT_DISTURB;
		else if (statusType.contains("offline"))
			return OnlineStatus.OFFLINE;
		else {
			System.out.println("Could not read Status from File!");
			return OnlineStatus.DO_NOT_DISTURB;
		}
	}
}