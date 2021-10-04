/**
 *
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.Warn;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author _SIM_
 */
public class ClearWarnCommand extends SlashCommand {

    public ClearWarnCommand() {
	this.setData(new CommandData("clearwarn", "Clears a warn from a user")
		.addOption(OptionType.USER, "user", "The user to clear the warn of", true)
		.addOption(OptionType.INTEGER, "warnid", "The ID of the warn to remove"))
	.setRequiredPermissions(Permission.KICK_MEMBERS);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    if (e.getOptionsByType(OptionType.USER).get(0).getAsMember() == null) {
		cmde.error("notamember", "cantclearwarnofnotmember");
		return;
	    }
	    final BlackMember mentionedMember = BlackMember.from(e.getOptionsByType(OptionType.USER).get(0).getAsMember());
	    final long warnId = e.getOptionsByType(OptionType.INTEGER).get(0).getAsLong();
	    final List<Warn> warns = mentionedMember.getWarns();
	    for (final Warn warn : warns) {
		if (warn.getDate() == warnId) {
		    mentionedMember.deleteWarn(warn);
		    cmde.success("entrydeleted", "warndeleted");
		    return;
		}
	    }
	    cmde.error("notfound", "warnnotfound");
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
    }
}