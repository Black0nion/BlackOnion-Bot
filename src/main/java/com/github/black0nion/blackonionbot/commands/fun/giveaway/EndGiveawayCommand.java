package com.github.black0nion.blackonionbot.commands.fun.giveaway;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.giveaways.Giveaway;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

/**
 * @author _SIM_
 */
public class EndGiveawayCommand extends SlashCommand {
    private static final String MESSAGE_ID = "message_id";

    public EndGiveawayCommand() {
        super(builder(Commands.slash("end_giveaway", "Used to end a giveaway.")
                .addOption(OptionType.STRING, MESSAGE_ID, "The message id of the giveaway.")));
    }

    @Override
    public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var id = e.getOption(MESSAGE_ID, OptionMapping::getAsString);
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