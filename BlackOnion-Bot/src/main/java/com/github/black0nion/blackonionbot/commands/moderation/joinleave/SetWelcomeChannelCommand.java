package com.github.black0nion.blackonionbot.commands.moderation.joinleave;

import java.time.Duration;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetWelcomeChannelCommand extends Command {
	
	public SetWelcomeChannelCommand() {
		this.setCommand("setwelcomechannel", "setwelcomechat")
			.setSyntax("[clear / off]")
			.setRequiredBotPermissions(Permission.MESSAGE_MANAGE)
			.setRequiredPermissions(Permission.MANAGE_SERVER);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		message.delete().queue();
		if (args.length >= 2 && (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("off"))) {
			guild.setJoinChannel(-1);
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("welcomechannelcleared", "welcomechannelclearedinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} else {
			guild.setJoinChannel(channel.getIdLong());
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("welcomechannelset", "welcomechannelsetinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		}
	}
}