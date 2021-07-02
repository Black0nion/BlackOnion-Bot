package com.github.black0nion.blackonionbot.commands.moderation.joinleave;

import java.time.Duration;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetLeaveChannelCommand extends Command {

    public SetLeaveChannelCommand() {
	this.setCommand("setleavechannel", "setleavechat").setSyntax("[clear / off]").setRequiredBotPermissions(Permission.MESSAGE_MANAGE).setRequiredPermissions(Permission.MANAGE_SERVER);
    }

    @Override
    public String[] getCommand() {
	return new String[] { "setleavechannel", "setleavechat" };
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	message.delete().queue();
	if (args.length >= 2 && (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("off"))) {
	    guild.setLeaveChannel(-1);
	    message.reply(cmde.success().addField("leavechannelcleared", "leavechannelclearedinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
	} else {
	    guild.setLeaveChannel(channel.getIdLong());
	    message.reply(cmde.success().addField("leavechannelset", "leavechannelsetinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
	}
    }
}