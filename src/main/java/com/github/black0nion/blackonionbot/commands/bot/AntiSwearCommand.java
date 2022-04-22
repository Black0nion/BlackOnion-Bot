package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
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

public class AntiSwearCommand extends SlashCommand {
    private static final String STATUS = "status";
    private static final String REPLACE = "replace";
    private static final String OFF = "off";
    private static final String DELETE = "delete";

    public AntiSwearCommand() {
        super(builder(Commands.slash("antiswear", "Used to enable/disable the anti-swear system.")
                .addOptions(new OptionData(OptionType.STRING, STATUS, "Used to change the status of the anti-swear system.", false)
                        .addChoice("Replace", REPLACE)
                        .addChoice("Off", OFF)
                        .addChoice("Delete", DELETE)))
                .setRequiredPermissions(Permission.ADMINISTRATOR));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        var status = e.getOption(STATUS, OptionMapping::getAsString);
        if (status != null) {
            final AntiSwearType parsedType = AntiSwearType.parse(status);
            if (parsedType != null) {
                guild.setAntiSwearType(parsedType);
                cmde.success("antiswearstatuschanged", "antiswearis", new Placeholder("status", cmde.getTranslation(parsedType.name())));
            } else {
                cmde.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", SlashCommandEvent.getPleaseUse(guild, author, this), false));
            }
        } else {
            cmde.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("antiswearstatus", author, guild).replace("%status%", LanguageSystem.getTranslation(guild.getAntiSwearType().name(), author, guild)), LanguageSystem.getTranslation("howtoantisweartoggle", author, guild).replace("%command%", "``" + SlashCommandEvent.getCommandHelp(this) + "``"), false));
        }
    }
}