package com.github.black0nion.blackonionbot.commands.information;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildInfoCommand extends Command {

    public GuildInfoCommand() {
	this.setCommand("guildinfo", "serverinfo");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	cmde.reply(cmde.success().setTitle("guildinfo").setThumbnail(guild.getIconUrl()).addField("name", guild.getName(), true).addField("language", guild.getLanguage().getName() + " (" + guild.getLanguage().getLanguageCode() + ")", true).addField("Owner", guild.retrieveOwner().submit().join().getUser().getAsMention(), true).addField("serverid", guild.getId(), true).addField("rolecount", String.valueOf(guild.getRoles().size()), true).addField("membercount", String.valueOf(guild.getMemberCount()), true).addField("channelcount", String.valueOf(guild.getChannels().size()), true).addField("boostlevel", guild.getBoostTier().name(), true).addField("created", BotInformation.datePattern.format(guild.getTimeCreated()), true));
    }
}