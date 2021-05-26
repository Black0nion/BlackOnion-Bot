package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class KickCommand extends Command {
	
	public KickCommand() {
		this.setCommand("kick", "yeet")
			.setSyntax("<@User> [reason]")
			.setRequiredArgumentCount(1)
			.setRequiredPermissions(Permission.KICK_MEMBERS)
			.setRequiredBotPermissions(Permission.KICK_MEMBERS);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final List<BlackMember> mentionedMembers = message.getMentionedBlackMembers();
		if (mentionedMembers.size() == 0) {
			cmde.error("wrongargument", "tagornameuser");
			return;
		} else {
			guild.kick(mentionedMembers.get(0)).queue();
			final String kickMessage = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : cmde.getTranslation("yougotkicked");
			cmde.success("kick", "usergotkicked", "message", new Placeholder("msg", kickMessage));
			mentionedMembers.get(0).getBlackUser().openPrivateChannel().queue(c -> {
				cmde.error("kick", "yougotkicked", "message", new Placeholder("msg", kickMessage));
			});
		}
	}
}