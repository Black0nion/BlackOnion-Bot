package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class WarnCommand extends SlashCommand {
    private static final String USER = "user";
    private static final String REASON = "reason";

    public WarnCommand() {
        super(builder(Commands.slash("warn", "Used to warn an user.")
                .addOption(OptionType.USER, USER, "The user to warn.", true)
                .addOption(OptionType.STRING, REASON, "The reason for the warn.", false))
                .setRequiredPermissions(Permission.KICK_MEMBERS));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackMember member, @NotNull BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        var warnMember = e.getOption(USER, OptionMapping::getAsMember);
        var reason = e.getOption(REASON, OptionMapping::getAsString);
        Warn warn;

        if (reason != null && reason.length() > 512) {
            cmde.send("reasonoption");
            return;
        }

        if (warnMember != null) {
            if (member.getIdLong() != warnMember.getIdLong() && member.canInteract(warnMember)) {
                warn = new Warn(author.getIdLong(), warnMember.getIdLong(), guild.getIdLong(), System.currentTimeMillis() - Warn.START_TIME_STAMP, reason);

                var memberToWarn = BlackMember.from(warnMember);
                if (memberToWarn != null) {
                    memberToWarn.warn(warn);
                    cmde.send("memberwarned");
                }
            } else {
                cmde.send("loweruserthanu");
            }
        } else {
            cmde.send("notamember");
        }
    }
}
