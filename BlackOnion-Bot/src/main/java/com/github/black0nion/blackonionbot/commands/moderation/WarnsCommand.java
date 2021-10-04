package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Date;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.Warn;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author _SIM_
 */
public class WarnsCommand extends SlashCommand {

    public WarnsCommand() {
	this.setData(new CommandData("warns", "Shows the warns for a specific user").addOption(OptionType.USER, "user", "The user to show the warns of", true)).setRequiredPermissions(Permission.KICK_MEMBERS);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    final Member meme = e.getOptionsByType(OptionType.USER).get(0).getAsMember();
	    if (meme != null) {
		final List<Warn> warns = BlackMember.from(member).getWarns();
		String result = "empty";
		if (warns.size() != 0) {
		    result = "";
		    for (final Warn warn : warns) {
			result += "\n`- " + BotInformation.datePattern.format(new Date(warn.getDate())) + ": `<@" + warn.getIssuer() + ">` > Reason: " + warn.getReason().replace("`", "") + " (ID: " + warn.getDate() + ")`";
		    }
		}
		cmde.success("warns", result);
	    } else {
		cmde.error("notamember", "canonlyseewarnsofthisserver");
	    }
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
    }
}