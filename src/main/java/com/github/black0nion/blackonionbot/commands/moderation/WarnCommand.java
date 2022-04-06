/**
 *
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * @author _SIM_
 *
 */
public class WarnCommand extends TextCommand {

	public WarnCommand() {
		this.setCommand("warn").setRequiredPermissions(Permission.KICK_MEMBERS).setSyntax("<@User> [reason]")
				.setRequiredArgumentCount(1);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
			final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild,
			final TextChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() != 0) {
			final BlackMember memberToWarn = BlackMember.from(mentionedMembers.get(0));
			assert memberToWarn != null;
			if (args[1].replace("!", "").equalsIgnoreCase(memberToWarn.getAsMention())) {
				if (member.canInteract(memberToWarn)) {
					Warn warn;
					String reason = cmde.getTranslation("empty");
					if (args.length > 2) {
						reason = String.join(" ", Utils.removeFirstArg(Utils.removeFirstArg(args)));
						warn = new Warn(guild.getIdLong(), author.getIdLong(), memberToWarn.getIdLong(),
								System.currentTimeMillis(), reason);
					} else {
						warn = new Warn(guild.getIdLong(), author.getIdLong(), memberToWarn.getIdLong(),
								System.currentTimeMillis());
					}

					memberToWarn.warn(warn);
					cmde.success("userwarned", "usergotwarned", new Placeholder("user", memberToWarn.getAsMention()),
							new Placeholder("reason", reason));
				} else {
					cmde.error("usertoopowerful", "loweruserthanu");
				}
			} else {
				cmde.sendPleaseUse();
			}
		} else {
			cmde.error("nousermentioned", "tagornameuser");
		}
	}
}
