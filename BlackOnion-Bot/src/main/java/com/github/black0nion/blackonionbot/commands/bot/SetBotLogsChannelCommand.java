package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SetBotLogsChannelCommand extends SlashCommand {

    public SetBotLogsChannelCommand() {
        this.setData(new CommandData("setbotlogschannel", "Set the channel where the botlogs should get sent to.")).setHidden();
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        BotInformation.botLogsChannel = channel.getIdLong();
        guild.save("botlogschannel", channel.getIdLong());
        cmde.success("savedbotlogschannel", "thisisbotlogschannel");
    }
}