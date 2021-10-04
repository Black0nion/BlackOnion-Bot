/**
 *
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author _SIM_
 *
 */
public class WarnCommand extends SlashCommand {

    public WarnCommand() {
	this.setData(new CommandData("warn", "Warn a Member")
		.addOption(OptionType.USER, "user", "The user to warn", true)
		.addOption(OptionType.STRING, "reason", "The reason to warn the user for", false))
	.setRequiredPermissions(Permission.KICK_MEMBERS);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (e.getOptionsByType(OptionType.USER).get(0).getAsMember() != null) {
	final BlackMember memberToWarn = BlackMember.from(e.getOptionsByType(OptionType.USER).get(0).getAsMember());
	if (member.canInteract(memberToWarn)) {
	    Warn warn;
	    String reason = cmde.getTranslation("empty");
	    if (!e.getOptionsByType(OptionType.STRING).isEmpty()) {
		reason = e.getOptionsByType(OptionType.STRING).get(0).getAsString();
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
	    cmde.error("notamember", "cantwarnnotmember");
	}
    }
}