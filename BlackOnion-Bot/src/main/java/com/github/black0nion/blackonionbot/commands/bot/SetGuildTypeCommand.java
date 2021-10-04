package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author _SIM_
 */
public class SetGuildTypeCommand extends SlashCommand {

    public SetGuildTypeCommand() {
        this.setData(new CommandData("setguildtype", "Set the type of a guild")
            .addOptions(
                    new OptionData(OptionType.STRING, "guild", "The guild id"),
                    new OptionData(OptionType.INTEGER, "type", "The new type of the guild", true)
                    .addChoices(Arrays.stream(GuildType.values())
                            .map(g -> new Command.Choice(g.name().toLowerCase(), g.getCode()))
                            .collect(Collectors.toList()))))
        .setHidden();
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        if (!Utils.isLong(e.getOption("guild").getAsString())) {
            cmde.error("notanumber", "inputnumber");
        } else {
            final Guild mentionedGuild = e.getJDA().getGuildById(Long.parseLong(e.getOptionsByType(OptionType.STRING).get(0).getAsString()));
            final BlackGuild mentionedBlackGuild = BlackGuild.from(mentionedGuild);
            if (mentionedBlackGuild != null) {
                final GuildType parsedGuildType = GuildType.parse((int) e.getOption("type").getAsLong());
                if (parsedGuildType != null) {
                    mentionedBlackGuild.setGuildType(parsedGuildType);
                    cmde.success("guildtypeset", "guildtypesetto", new Placeholder("guild", mentionedBlackGuild.getName() + " (G:" + mentionedBlackGuild.getId() + ")"), new Placeholder("guildtype", parsedGuildType.name()));
                } else {
                    String validGuildTypes = "";
                    for (final GuildType type : GuildType.values()) {
                        validGuildTypes += "\n- " + type.name();
                    }
                    cmde.error("guildtypenotfound", "validguildtypes", new Placeholder("guildtypes", "```" + validGuildTypes + "```"));
                }
            } else {
                cmde.error("notfound", "thisguildnotfound");
            }
        }
    }
}
