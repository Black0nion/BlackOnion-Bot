package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.Instant;
import java.util.stream.Collectors;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;

public class StatsCommand extends SlashCommand {

    public StatsCommand() {
        this.setData(new CommandData("stats", "Shows stats about the bot"));
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        try {
            final long diff = System.currentTimeMillis() - Bot.startTime;

            cmde.reply(
                cmde.success()
                    .setTitle("Bot Stats")
                    .setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
                    .addField("prefix", "``" + guild.getPrefix() + "``", true)
                    .addField("runmode", Bot.runMode.name().toUpperCase(), true)
                    .addField("os", osName, true)
                    .addField("cpuname", cpuName, true)
                    .addField("cpucores", String.valueOf(osBean.getAvailableProcessors()), true)
                    .addField("cpuspeed", cpuMhz, true)
                    .addField("lines", String.valueOf(BotInformation.line_count), true)
                    .addField("files", String.valueOf(BotInformation.file_count), true)
                    .addField("commandsexecuted", String.valueOf(ValueManager.getInt("commandsExecuted")), true)
                    .addField("messagessent", String.valueOf(ValueManager.getInt("messagesSent")), true)
                    .addField("commands", String.valueOf(CommandBase.commandsArray.size()), true)
                    .addField("ping", e.getJDA().getGatewayPing() + "ms", true)
                    .addField("usercount", String.valueOf(e.getJDA().getGuilds().stream().map(Guild::getMemberCount).collect(Collectors.summingInt(Integer::intValue))), true)
                    .addField("guildcount", String.valueOf(e.getJDA().getGuildCache().size()), true)
                    .addField("uptime", Utils.parseDate(diff), true)
                    .addField("version", BotInformation.version, true)
                    .setThumbnail("https://image.sv-studios.net/15d06c22eb6b0b8dfbdeda94a56c878d15.png")
                    .setTimestamp(Instant.now())
            );
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}