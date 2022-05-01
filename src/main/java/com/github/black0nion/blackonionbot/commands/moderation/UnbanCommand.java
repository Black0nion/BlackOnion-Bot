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

import java.util.Objects;

public class UnbanCommand extends SlashCommand {
    private static final String USER = "user";
    private static final String REASON = "reason";

    public UnbanCommand() {
        super(builder(Commands.slash("unban", "Used to unban a user")
                .addOption(OptionType.USER, USER, "The user to unban", true)
                .addOption(OptionType.STRING, REASON, "The reason for the unban", true))
                .setRequiredPermissions(Permission.BAN_MEMBERS)
                .setRequiredBotPermissions(Permission.BAN_MEMBERS));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
                        BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        var user = e.getOption(USER, OptionMapping::getAsUser);
        var reason = e.getOption(REASON, OptionMapping::getAsString);

        if (reason.length() > 512) {
            cmde.send("reasonoption");
            return;
        }

        guild.retrieveBan(Objects.requireNonNull(user))
                .queue(success -> guild.unban(user).reason(reason).queue(
                                v -> e.reply("I have unbanned the user " + user.getAsMention() + " for " + reason).queue(),
                                fail -> e.reply("I could not unban the user" + " " + user.getAsMention()).queue()),
                        fail -> e.reply("I could not find the user " + user.getAsMention() + " to unban").queue());
    }
}