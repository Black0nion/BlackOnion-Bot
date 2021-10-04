/**
 *
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author _SIM_
 */
public class SuggestCommand extends SlashCommand {

    public SuggestCommand() {
        this.setData(new CommandData("suggest", "Suggest a idea to the server staff").addOption(OptionType.STRING, "suggestion", "The suggestion to submit", true));
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        final long suggestionsChannelId = guild.getSuggestionsChannel();
        if (suggestionsChannelId == -1) {
            cmde.error("suggestionsoff", "nosuggestionfeature");
            return;
        } else {
            final TextChannel suggestionsChannel = guild.getTextChannelById(suggestionsChannelId);
            if (suggestionsChannel == null) {
                cmde.error("suggestionschannelnotfound", "pleaseresetsuggestionschannel");
                return;
            } else if (!(guild.getSelfMember().hasPermission(suggestionsChannel, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION))) {
                channel.sendMessageEmbeds(Utils.noRights(guild, guild.getSelfBlackMember().getBlackUser(), Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION)).queue();
            } else {
                // all good, we can send the suggestion
                suggestionsChannel.sendMessageEmbeds(cmde.success().setTitle("suggestion").setDescription(e.getOptionsByType(OptionType.STRING).get(0).getAsString()).build()).queue(msg -> {
                    msg.addReaction("U+1F44D").queue();
                    msg.addReaction("U+1F44E").queue();
                });
                cmde.success("suggestiongotsent", "suggestionisin", new Placeholder("channel", suggestionsChannel.getAsMention()));
            }
        }
    }
}