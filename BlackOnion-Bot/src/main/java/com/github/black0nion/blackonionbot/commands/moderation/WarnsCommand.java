package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Date;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 */
public class WarnsCommand extends Command {

    public WarnsCommand() {
	this.setCommand("warns").setSyntax("<@User | UserID>").setRequiredArgumentCount(1).setRequiredPermissions(Permission.KICK_MEMBERS);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String user = args[1];
	final BlackMember mentionedMember;
	if (Utils.isLong(user)) {
	    mentionedMember = BlackMember.from(guild.retrieveMemberById(user).submit().join());
	    if (mentionedMember == null) {
		cmde.error("usernotfound", "inputnumber");
		return;
	    }
	} else {
	    final List<Member> mentionedMembers = message.getMentionedMembers();
	    if (mentionedMembers.size() != 0) {
		if (args[1].replace("!", "").equalsIgnoreCase(mentionedMembers.get(0).getAsMention())) {
		    mentionedMember = BlackMember.from(mentionedMembers.get(0));
		} else {
		    cmde.sendPleaseUse();
		    return;
		}
	    } else {
		cmde.error("nousermentioned", "tagornameuser");
		return;
	    }
	}

	try {
	    final List<Warn> warns = mentionedMember.getWarns();
	    String result = "empty";
	    if (warns.size() != 0) {
		result = "";
		for (final Warn warn : warns) {
		    result += "\n`- " + BotInformation.DATE_PATTERN.format(new Date(warn.getDate())) + ": `<@" + warn.getIssuer() + ">` > Reason: " + warn.getReason().replace("`", "") + " (ID: " + warn.getDate() + ")`";
		}
	    }
	    cmde.success("warns", result);
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
    }
}