package com.github.black0nion.blackonionbot.slashcommands.bot;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class SetBotLogsChannelCommand extends SlashCommand {

    public SetBotLogsChannelCommand() {
        super(builder("setbotlogschannel", "Used to set the channel where bot logs are sent.")
                .setRequiredCustomPermissions(CustomPermission.ADMIN));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, @NotNull TextChannel channel) {
        BotInformation.logsChannel = channel.getIdLong();
        guild.save("botlogschannel", channel.getIdLong());
        cmde.success("savedbotlogschannel", "thisisbotlogschannel");
    }
}
