package com.github.black0nion.blackonionbot.commands.fun.giveaway;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.giveaways.Giveaway;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author _SIM_
 */
public class EndGiveawayCommand extends SlashCommand {

    public EndGiveawayCommand() {
	this.setCommand("endgiveaway").setSyntax("<messageid>").setRequiredArgumentCount(1);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String id = args[1];
	if (Utils.isLong(id)) {
	    final long idLong = Long.parseLong(id);
	    final Giveaway giveaway = GiveawaySystem.getGiveaway(idLong);

	    if (giveaway == null || giveaway.channelId() != channel.getIdLong()) {
		cmde.error("giveawaynotfound", "giveawaynotfounddesc");
	    } else if (giveaway.createrId() != author.getIdLong() && !member.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
		cmde.error("nogiveawayendrights", "mustbeadminorgiveawaycreater");
	    } else {
		channel.retrieveMessageById(idLong).queue(msg -> {
		    if (msg == null) {
			cmde.exception();
		    } else {
			GiveawaySystem.endGiveaway(giveaway, msg, guild);
			cmde.success("giveawayended", "giveawaygotended");
		    }
		});
	    }
	} else {
	    cmde.error("notanumber", "invalidmessageid");
	}
    }
}