package com.github.black0nion.blackonionbot.slashcommands.bot;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.ChainableAtomicReference;
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

import java.util.Map;

public class ToggleCommand extends SlashCommand {

    public ToggleCommand() {
        super(builder(Commands.slash("toggle", "Toggle commands")
                .addOptions(
                        new OptionData(OptionType.STRING, "command", "Command to toggle", true, true),
                        new OptionData(OptionType.BOOLEAN, "on", "The new status", false)
                )
        )
                .autocomplete("command", SlashCommandBase.commands.keySet())
                .setRequiredPermissions(Permission.MANAGE_SERVER).notToggleable());
    }

    public void updateAutoComplete() {
        ChainableAtomicReference<SlashCommand> currentCommand = new ChainableAtomicReference<>();
        this.updateAutoComplete("command", SlashCommandBase.commands.entrySet().stream()
                .filter(e ->
                        ((currentCommand.setAndGet(e.getValue().getValue())).getRequiredCustomPermissions() == null
                                || currentCommand.get().getRequiredCustomPermissions().length == 0)
                                && currentCommand.get().isToggleable())
                .map(Map.Entry::getKey)
                .toList());
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, @NotNull BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        final SlashCommand command = SlashCommandBase.commands.get(e.getOption("command", OptionMapping::getAsString)).getValue();
        if (command == null || command.isHidden(author)) {
            cmde.send("commandnotfound");
            return;
        }

        Boolean newStatus = e.getOption("on", OptionMapping::getAsBoolean);
        if (newStatus == null) {
            cmde.send("commandstatus", new Placeholder("cmd", command.getName()), new Placeholder("status", cmde.getTranslation(guild.isCommandActivated(command) ? "on" : "off")));
        } else {
            if (guild.setCommandActivated(command, newStatus)) {
                final String commandName = command.getName().toUpperCase();
                cmde.success("commandtoggled", "commandisnow", new Placeholder("command", commandName), new Placeholder("status", cmde.getTranslation(newStatus ? "on" : "off")));
            } else {
                cmde.error("commandcantbetoggled", "thiscommandcantbetoggled");
            }
        }
    }
}