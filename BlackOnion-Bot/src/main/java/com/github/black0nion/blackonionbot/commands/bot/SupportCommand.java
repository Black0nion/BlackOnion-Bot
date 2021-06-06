package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SupportCommand extends Command {

	public SupportCommand() {
		this.setCommand("support");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		cmde.reply(cmde.success().addField("Discord Server:", "[Invite Link](https://discord.gg/ZzGKy9RCBY)", false)
				.addField("Creators", "[SIMULATAN](https://github.com/SIMULATAN), [ManuelP](https://github.com/ManuelPuchner), [MatseCrafter_304](https://github.com/MatseCrafter-304), [blaumeise](https://github.com/blaumeise20)", false));
	}
}