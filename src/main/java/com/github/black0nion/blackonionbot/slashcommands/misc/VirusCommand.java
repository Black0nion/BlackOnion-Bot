package com.github.black0nion.blackonionbot.slashcommands.misc;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class VirusCommand extends SlashCommand {
    private static final String URL = "url";
    private static final String URL_COMMAND = "url";
    private static final String ATTACHMENT = "attachment";
    private static final String ATTACHMENT_COMMAND = "attachment";

    public VirusCommand() {
        super(builder(Commands.slash("virus", "Check your file on VirusTotal")
                .addSubcommands(
                        new SubcommandData(URL_COMMAND, "Check a ur")
                                .addOption(OptionType.STRING, URL, "URL to check", true),
                        new SubcommandData(ATTACHMENT_COMMAND, "Check an attachment")
                                .addOption(OptionType.ATTACHMENT, ATTACHMENT, "Attachment to check", true)
                )
        ));
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        switch (e.getSubcommandName()) {
            case URL_COMMAND -> urlCommand(cmde, e, member, author, guild, channel);
            case ATTACHMENT_COMMAND -> attachmentCommand(cmde, e, member, author, guild, channel);
            default -> cmde.sendPleaseUse();
        }
    }

    private void urlCommand(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        if (e.getOption(URL) == null) {
            cmde.sendPleaseUse();
            return;
        }
        cmde.reply(cmde.success().setTitle("VirusTotal", "https://www.virustotal.com/gui/home/upload").addField("virustotalfieldtitle", "virustotalinfo", false));
    }

    private void attachmentCommand(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        if (e.getOption(ATTACHMENT) == null) {
            cmde.sendPleaseUse();
            return;
        }
        cmde.reply(cmde.success().setTitle("VirusTotal", "https://www.virustotal.com/gui/home/upload").addField("virustotalfieldtitle", "virustotalinfo", false));
    }
}