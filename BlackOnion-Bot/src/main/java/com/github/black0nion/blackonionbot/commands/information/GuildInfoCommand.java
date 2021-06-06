package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildInfoCommand extends Command {
	
	public GuildInfoCommand() {
		this.setCommand("guildinfo", "serverinfo");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		cmde.reply(cmde.success()
			.setTitle("guildinfo")
			.setThumbnail(guild.getIconUrl())
			.addField("name", guild.getName(), true)
			.addField("language", guild.getLanguage().getName() + " (" + guild.getLanguage().getLanguageCode() + ")", true)
			.addField("Owner", guild.retrieveOwner().submit().join().getUser().getAsMention(), true)
			.addField("serverid", guild.getId(), true)
			.addField("rolecount", String.valueOf(guild.getRoles().size()), true)
			.addField("membercount", String.valueOf(guild.getMemberCount()), true)
			.addField("channelcount", String.valueOf(guild.getChannels().size()), true)
			.addField("boostlevel", guild.getBoostTier().name(), true)
			.addField("created", guild.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), true));
	}
}