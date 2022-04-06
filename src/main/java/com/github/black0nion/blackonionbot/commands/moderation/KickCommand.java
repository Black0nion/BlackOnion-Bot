package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class KickCommand extends TextCommand {

	public KickCommand() {
		this.setCommand("kick", "yeet").setSyntax("<@User> [reason]").setRequiredArgumentCount(1)
				.setRequiredPermissions(Permission.KICK_MEMBERS).setRequiredBotPermissions(Permission.KICK_MEMBERS);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
			final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild,
			final TextChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() == 0) {
			cmde.error("wrongargument", "tagornameuser");
		} else {
			final BlackMember userToKick = BlackMember.from(mentionedMembers.get(0));

			assert userToKick != null;
			if (member.canInteract(userToKick)) {
				guild.kick(userToKick).queue();
				final String kickMessage = args.length >= 3
						? String.join(" ", Arrays.copyOfRange(args, 2, args.length))
						: cmde.getTranslation("yougotkicked");
				cmde.success("kick", "usergotkicked", "message", new Placeholder("msg", kickMessage));
				userToKick.getBlackUser().openPrivateChannel()
						.queue(c -> cmde.error("kick", "yougotkicked", "message", new Placeholder("msg", kickMessage)));
			} else {
				cmde.error("usertoopowerful", "loweruserthanu");
			}
		}
	}
}
