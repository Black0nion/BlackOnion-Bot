package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class ClearUntilCommand extends SlashCommand {

    public ClearUntilCommand() {
        super(builder(Commands.slash("clearuntil", "Clear all messages until a certain one")

                .setRequiredBotPermissions(Permission.MESSAGE_MANAGE)
                .setRequiredPermissions(Permission.MESSAGE_MANAGE).setEphemeral());
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
                        BlackUser author, BlackGuild guild, @NotNull TextChannel channel) {

    }
}