package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
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

public class AntiSpoilerCommand extends SlashCommand {
    private static final String STATUS = "status";
    private static final String REPLACE = "replace";
    private static final String DELETE = "delete";
    private static final String OFF = "off";

    public AntiSpoilerCommand() {
        super(builder(Commands.slash("antispoiler", "Used to deleted/disable/replace the anti-spoiler system.")
                .addOptions(new OptionData(OptionType.STRING, STATUS, "The status", false)
                        .addChoice("Replace", REPLACE)
                        .addChoice("Delete", DELETE)
                        .addChoice("Off", OFF)))
                .setRequiredPermissions(Permission.MESSAGE_MANAGE));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        var status = e.getOption(STATUS, OptionMapping::getAsString);

        final AntiSpoilerType parsedType = AntiSpoilerType.parse(status);
        if (status != null) {
            if (parsedType != null) {
                guild.setAntiSpoilerType(parsedType);
                cmde.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("remove"))), false));
            } else {
                cmde.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", SlashCommandEvent.getPleaseUse(guild, author, this), false));
            }
        } else {
            cmde.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("antispoilerstatus", author, guild).replace("%status%", LanguageSystem.getTranslation(guild.getAntiSpoilerType().name(), author, guild)), LanguageSystem.getTranslation("howtoantispoilertoggle", author, guild).replace("%command%", "``" + SlashCommandEvent.getCommandHelp(this) + "``"), false));
        }
    }
}