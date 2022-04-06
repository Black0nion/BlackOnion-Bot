package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.util.List;

public class AvatarCommand extends TextCommand {

	public AvatarCommand() {
		this.setCommand("avatar", "profilepicture", "pb", "pfp").setSyntax("<@User / UserID>");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
			final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild,
			final TextChannel channel) {
		BlackUser mentionedUser = author;
		if (args.length > 1) {
			final String user = String.join(" ", Utils.removeFirstArg((args)));
			final List<User> mentionedBlackUsers = message.getMentionedUsers();
			if (!mentionedBlackUsers.isEmpty()) {
				mentionedUser = BlackUser.from(mentionedBlackUsers.get(0));
			} else {
				if (!Utils.isLong(user)) {
					cmde.sendPleaseUse();
					return;
				}

				e.getJDA().retrieveUserById(user.trim()).queue(uzer -> print(cmde, author, BlackUser.from(uzer)),
						failure -> cmde.exception());
				return;
			}
		}

		print(cmde, author, mentionedUser);
	}

	private static void print(final CommandEvent cmde, final BlackUser author, final BlackUser mentionedUser) {
		cmde.reply(cmde.success()
				.setTitle(cmde.getTranslation("pfpof") + " " + mentionedUser.getEscapedEffectiveName(),
						mentionedUser.getEffectiveAvatarUrl())
				.setImage(mentionedUser.getEffectiveAvatarUrl() + "?size=2048")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.setTimestamp(Instant.now()));
	}
}
