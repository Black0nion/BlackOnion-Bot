package com.github.black0nion.blackonionbot.commands.admin;

import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildlistCommand extends Command {

	public GuildlistCommand() {
		this.setCommand("guildlist", "guilds").setHidden();
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		cmde.success("guildlist", "```\n- " + (e.getJDA().getGuilds().stream().map(g -> {
			final User user = g.retrieveOwner().submit().join().getUser();
			return g.getName() + "(" + g.getId() + ") | " + user.getAsTag() + "(" + user.getId() + ")";
		}).collect(Collectors.joining("\n- "))) + "```");
	}
}