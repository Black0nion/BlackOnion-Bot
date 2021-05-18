package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildInfoCommand implements Command {
	@Override
	public String[] getCommand() {
		return new String[] { "guildinfo", "serverinfo" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild)
				.setTitle("guildinfo")
				.setThumbnail(guild.getIconUrl())
				.addField("name", guild.getName(), true)
				.addField("Owner", guild.retrieveOwner().submit().join().getUser().getAsMention(), true)
				.addField("serverid", guild.getId(), true)
				.addField("rolecount", String.valueOf(guild.getRoles().size()), true)
				.addField("membercount", String.valueOf(guild.getMemberCount()), true)
				.addField("channelcount", String.valueOf(guild.getChannels().size()), true)
				.addField("boostlevel", guild.getBoostTier().name(), true)
				.addField("created", guild.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), true)
				.build()).queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.INFORMATION;
	}
}