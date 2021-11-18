package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollCommand extends Command {
	
	public PollCommand() {
		this.setCommand("poll", "survey")
			.setSyntax("yes / no question")
			.setRequiredArgumentCount(1);
	}

	@Override
	public String[] getCommand() {
		return new String[] { "poll", "survey" };
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		cmde.success("poll", String.join(" ", Utils.removeFirstArg(args)), "polltutorial", msg -> {
			msg.addReaction("tick:822036832422068225").queue();
			msg.addReaction("cross:822036805117018132").queue();
		});
	}
}