package com.github.black0nion.blackonionbot.slashcommands.bot;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JoinLeaveMessageCommand extends SlashCommand {
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";

    public JoinLeaveMessageCommand() {
        super(builder(Commands.slash("joinleavemessage", "Used to set the join/leave messages for the bot.")
                .addOptions(new OptionData(OptionType.STRING, STATUS, "Whether to set the join/leave message for the join or leave status.")
                        .addChoice("Join", "join")
                        .addChoice("Leave", "leave"))
                .addOptions(new OptionData(OptionType.STRING, MESSAGE, "The message to set.")))
                .setRequiredPermissions(Permission.ADMINISTRATOR));
    }


    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        var status = Objects.requireNonNull(e.getOption(STATUS, OptionMapping::getAsString));
        var message = e.getOption(MESSAGE, OptionMapping::getAsString);
        if (status.equals("join")) {
            guild.setJoinMessage(message);
            cmde.success("setjoinmessage", "joinmessagesestto", new Placeholder("msg", "`" + message + "`"));
        } else if (status.equals("leave")) {
            guild.setLeaveMessage(message);
            cmde.success("setleavemessage", "leavemessagesestto", new Placeholder("msg", "`" + message + "`"));
        } else
            cmde.sendPleaseUse();
    }
}