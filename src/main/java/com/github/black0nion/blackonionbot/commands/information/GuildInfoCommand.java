package com.github.black0nion.blackonionbot.commands.information;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class GuildInfoCommand extends SlashCommand {

    public GuildInfoCommand() {
        super(builder(Commands.slash("guildinfo", "Provides information about the current guild.")));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        cmde.reply(cmde.success().setTitle("guildinfo")
                .setThumbnail(guild.getIconUrl())
                .addField("name", guild.getEscapedName(), true)
                .addField("language", guild.getLanguage() != null ? (guild.getLanguage().getName() + " (" + guild.getLanguage().getLanguageCode() + ")") : "none", true)
                .addField("owner", guild.retrieveOwner().submit().join().getUser().getAsMention(), true)
                .addField("serverid", guild.getId(), true)
                .addField("rolecount", String.valueOf(guild.getRoles().size()), true)
                .addField("membercount", String.valueOf(guild.getMemberCount()), true)
                .addField("channelcount", String.valueOf(guild.getChannels().size()), true)
                .addField("boostlevel", guild.getBoostTier().name(), true)
                .addField("created", guild.getTimeCreated().format(BotInformation.dateTimeFormatter), true));
    }
}