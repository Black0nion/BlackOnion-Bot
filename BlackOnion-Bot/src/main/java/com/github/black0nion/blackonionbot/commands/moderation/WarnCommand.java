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
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 *
 */
public class WarnCommand extends Command {

    public WarnCommand() {
	this.setCommand("warn").setRequiredPermissions(Permission.KICK_MEMBERS).setSyntax("<@User> [reason]").setRequiredArgumentCount(1);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final List<Member> mentionedMembers = message.getMentionedMembers();
	if (mentionedMembers.size() != 0) {
	    final BlackMember memberToWarn = BlackMember.from(mentionedMembers.get(0));
	    if (args[1].replace("!", "").equalsIgnoreCase(memberToWarn.getAsMention())) {
		if (member.canInteract(memberToWarn)) {
		    Warn warn;
		    String reason = cmde.getTranslation("empty");
		    if (args.length > 2) {
			reason = String.join(" ", Utils.removeFirstArg(Utils.removeFirstArg(args)));
			warn = new Warn(guild.getIdLong(), author.getIdLong(), memberToWarn.getIdLong(), System.currentTimeMillis(), reason);
		    } else {
			warn = new Warn(guild.getIdLong(), author.getIdLong(), memberToWarn.getIdLong(), System.currentTimeMillis());
		    }

		    memberToWarn.warn(warn);
		    cmde.success("userwarned", "usergotwarned", new Placeholder("user", memberToWarn.getAsMention()), new Placeholder("reason", reason));
		} else {
		    cmde.error("usertoopowerful", "loweruserthanu");
		}
	    } else {
		cmde.sendPleaseUse();
		return;
	    }
	} else {
	    cmde.error("nousermentioned", "tagornameuser");
	    return;
	}
    }
}