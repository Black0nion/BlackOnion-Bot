package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class UnbanCommand extends TextCommand {

	public UnbanCommand() {
		this.setCommand("unban", "unyeet").setSyntax("<@User>").setRequiredArgumentCount(1).setRequiredPermissions(Permission.BAN_MEMBERS).setRequiredBotPermissions(Permission.BAN_MEMBERS);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() != 0) {
			final BlackUser bannedUser = BlackUser.from(mentionedMembers.get(0));
			guild.retrieveBan(bannedUser).queue(ban -> cmde.success("unban", "userunbanned", "bannedfor", new Placeholder("reason", "**" + ban.getReason() + "**")));
			guild.unban(bannedUser).queue();
		} else {
			try {
				if (!Utils.isLong(args[1])) {
					cmde.sendPleaseUse();
					return;
				}

				guild.retrieveBanById(args[1]).queue(ban -> {
					final String reason = ban.getReason();
					guild.unban(ban.getUser()).queue();
					cmde.success("unban", "userunbanned", "bannedfor", new Placeholder("reason", "**" + reason + "**"));
				}, fail -> cmde.error("usernotfound", "tagornameuser"));
			} catch (final Exception ignored) {}
		}
	}
}