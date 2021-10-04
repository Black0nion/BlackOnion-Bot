package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SupportCommand extends SlashCommand {

	public SupportCommand() {
		this.setData(new CommandData("support", "Find out how to get support"));
	}

	@Override
	public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		cmde.reply(cmde.success().addField("Discord Server:", "[Invite Link](https://discord.gg/ZzGKy9RCBY)", false)
				.addField("Creators", "[SIMULATAN](https://github.com/SIMULATAN), [ManuelP](https://github.com/ManuelPuchner), [MatseCrafter_304](https://github.com/MatseCrafter-304), [blaumeise](https://github.com/blaumeise20)", false));
	}
}