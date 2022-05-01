package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
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

public class RemoveTimeOutCommand extends SlashCommand {
    private static final String USER = "user";

    protected RemoveTimeOutCommand() {
        super(builder(Commands.slash("removetimeout", "Used to remove a timeout from a user.")
                .addOption(OptionType.USER, USER, "The user to remove the timeout from.", true))
                .setRequiredPermissions(Permission.MODERATE_MEMBERS));
    }

    @Override
    public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var timeOutMember = e.getOption(USER, OptionMapping::getAsMember);
        if(!timeOutMember.isTimedOut()) {
            cmde.send("isnottimedout");
            return;
        }

        timeOutMember.removeTimeout().queue();
        cmde.send("timedoutremoved");
    }
}