package com.github.black0nion.blackonionbot.commands.moderation.joinleave;

import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetWelcomeChannelCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "setwelcomechannel", "setwelcomechat" };
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		message.delete().queue();
		GuildManager.saveString(guild.getId(), "welcomechannel", channel.getId());
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("welcomechannelset", "welcomechannelsetinfo", false).build()).submit().join().delete().queueAfter(5, TimeUnit.SECONDS);
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.MANAGE_SERVER };
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}

}
