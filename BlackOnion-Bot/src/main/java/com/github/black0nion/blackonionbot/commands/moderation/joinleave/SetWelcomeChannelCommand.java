package com.github.black0nion.blackonionbot.commands.moderation.joinleave;

import java.time.Duration;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetWelcomeChannelCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "setwelcomechannel", "setwelcomechat" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		message.delete().queue();
		if (args.length >= 2 && (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("off"))) {
			guild.setJoinChannel(-1);
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("welcomechannelcleared", "welcomechannelclearedinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} else {
			guild.setJoinChannel(channel.getIdLong());
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("welcomechannelset", "welcomechannelsetinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.MANAGE_SERVER };
	}
	
	@Override
	public Permission[] getRequiredBotPermissions() {
		return new Permission[] { Permission.MESSAGE_MANAGE };
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}

	@Override
	public String getSyntax() {
		return "[clear / off]";
	}
}