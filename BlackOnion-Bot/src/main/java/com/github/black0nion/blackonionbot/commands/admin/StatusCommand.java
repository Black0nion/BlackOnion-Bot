package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class StatusCommand extends SlashCommand {

    public StatusCommand() {
	this.setData(new CommandData("status", "Set the status of the bot").addOptions(new OptionData(OptionType.STRING, "type", "The status to set the bot to", true).addChoice("Online", "online").addChoice("Offline", "offline").addChoice("Away from Keyboard (AFK)", "afk").addChoice("Do not disturb", "dnd"))).setHidden();
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	OnlineStatus status = null;
	switch (e.getOption("type").getAsString()) {
	case "online":
	    ValueManager.save("status", "online");
	    status = OnlineStatus.ONLINE;
	    break;
	case "offline":
	    ValueManager.save("status", "offline");
	    status = OnlineStatus.OFFLINE;
	    break;
	case "afk":
	    ValueManager.save("status", "afk");
	    status = OnlineStatus.IDLE;
	    break;
	case "dnd":
	    ValueManager.save("status", "dnd");
	    status = OnlineStatus.DO_NOT_DISTURB;
	    break;
	default:
	    // shouldn't happen, if it does, punch @simulatan
	    cmde.errorPrivate("simulatan is dumb", "bruh what lol");
	    return;
	}
	cmde.successPrivate("statussetsuccess", cmde.getTranslation("newstatus", new Placeholder("status", status.name().toUpperCase())));

	e.getJDA().getPresence().setStatus(status);
    }

    public static OnlineStatus getStatusFromFile() {
	final String statusType = ValueManager.getString("status");
	if (statusType.contains("online")) return OnlineStatus.ONLINE;
	else if (statusType.contains("afk")) return OnlineStatus.IDLE;
	else if (statusType.contains("dnd")) return OnlineStatus.DO_NOT_DISTURB;
	else if (statusType.contains("offline")) return OnlineStatus.OFFLINE;
	else {
	    System.out.println("Could not read Status from File!");
	    return OnlineStatus.DO_NOT_DISTURB;
	}
    }
}