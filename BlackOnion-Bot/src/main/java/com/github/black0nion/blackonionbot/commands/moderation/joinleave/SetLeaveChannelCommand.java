package com.github.black0nion.blackonionbot.commands.moderation.joinleave;

import java.time.Duration;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetLeaveChannelCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "setleavechannel", "setleavechat" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		message.delete().queue();
		if (args.length >= 2 && (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("off"))) {
			GuildManager.remove(guild, "leavechannel");
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("leavechannelcleared", "leavechannelclearedinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} else {
			GuildManager.save(guild.getId(), "leavechannel", channel.getId());
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("leavechannelset", "leavechannelsetinfo", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
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