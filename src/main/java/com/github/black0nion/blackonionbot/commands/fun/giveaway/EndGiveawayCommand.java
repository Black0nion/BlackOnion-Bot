package com.github.black0nion.blackonionbot.commands.fun.giveaway;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
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
public class EndGiveawayCommand extends Command {

    public EndGiveawayCommand() {
	this.setCommand("endgiveaway").setSyntax("<messageid>").setRequiredArgumentCount(1);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String id = args[1];
	if (Utils.isLong(id)) {
	    final long idLong = Long.parseLong(id);
	    final Giveaway giveaway = GiveawaySystem.getGiveaway(idLong);

	    if (giveaway == null || giveaway.getChannelId() != channel.getIdLong()) {
		cmde.error("giveawaynotfound", "giveawaynotfounddesc");
	    } else if (giveaway.getCreaterId() != author.getIdLong() && !member.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
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