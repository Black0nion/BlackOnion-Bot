package com.github.black0nion.blackonionbot.commands.fun.giveaway;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.giveaways.Giveaway;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author _SIM_
 */
public class EndGiveawayCommand extends SlashCommand {

    public EndGiveawayCommand() {
	this.setData(new CommandData("endgiveaway", "Ends a giveaway early").addOption(OptionType.INTEGER, "giveawayid", "The ID of the Giveaway", true)).setRequiredPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final long id = e.getOptionsByType(OptionType.STRING).get(0).getAsLong();
	final Giveaway giveaway = GiveawaySystem.getGiveaway(id);

	if (giveaway == null || giveaway.getChannelId() != channel.getIdLong()) {
	    cmde.errorPrivate("giveawaynotfound", "giveawaynotfounddesc");
	} else if (giveaway.getCreaterId() != author.getIdLong() && !member.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
	    cmde.errorPrivate("nogiveawayendrights", "mustbeadminorgiveawaycreater");
	} else {
	    channel.retrieveMessageById(id).queue(msg -> {
		if (msg == null) {
		    cmde.exception();
		} else {
		    GiveawaySystem.endGiveaway(giveaway, msg, guild);
		    cmde.successPrivate("giveawayended", "giveawaygotended");
		}
	    });
	}
    }
}