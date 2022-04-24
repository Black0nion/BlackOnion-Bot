/**
 *
 */
package com.github.black0nion.blackonionbot.slashcommands.moderation;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class SuggestCommand extends SlashCommand {
    private static final String SUGGESTION = "suggestion";

    public SuggestCommand() {
        super(builder(Commands.slash("suggest", "Used to send a suggestion.").addOption(OptionType.STRING, SUGGESTION,
                "The suggestion to send", true)));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
                        BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        var suggestion = e.getOption(SUGGESTION, OptionMapping::getAsString);
        final long suggestionsChannelId = guild.getSuggestionsChannel();

        if (suggestionsChannelId == -1) {
            cmde.send("invalidsuggestionschannel");
        } else {
            final TextChannel suggestionsChannel = guild.getTextChannelById(suggestionsChannelId);
            if (suggestionsChannel == null) {
                cmde.send("invalidsuggestionschannel");
            } else if (!(guild.getSelfMember().hasPermission(suggestionsChannel, Permission.MESSAGE_SEND,
                    Permission.MESSAGE_ADD_REACTION))) {
                e.replyEmbeds(Utils.noRights(guild, guild.getSelfBlackMember().getBlackUser(), Permission.MESSAGE_SEND,
                        Permission.MESSAGE_ADD_REACTION)).setEphemeral(true).queue();
            } else {
                // all good, we can send the suggestion
                suggestionsChannel.sendMessageEmbeds(
                                cmde.success().setTitle("suggestion").setDescription(String.join(" ", suggestion)).build())
                        .queue(msg -> {
                            msg.addReaction("U+1F44D").queue();
                            msg.addReaction("U+1F44E").queue();
                        });
                cmde.send("suggestionsucess");
            }
        }
    }
}