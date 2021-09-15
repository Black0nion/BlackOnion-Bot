/**
 *
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
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
public class ClearWarnCommand extends Command {

    public ClearWarnCommand() {
	this.setCommand("clearwarn", "clearwarns").setSyntax("<@User> <warnid>").setRequiredArgumentCount(2).setRequiredPermissions(Permission.KICK_MEMBERS);
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
	    if (Utils.isLong(args[2])) {
		final long warnId = Long.parseLong(args[2]);
		final List<Warn> warns = mentionedMember.getWarns();
		for (final Warn warn : warns) {
		    if (warn.getDate() == warnId) {
			mentionedMember.deleteWarn(warn);
			cmde.success("entrydeleted", "warndeleted");
			return;
		    }
		}
		cmde.error("notfound", "warnnotfound");
	    } else {
		cmde.sendPleaseUse();
	    }
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
    }
}