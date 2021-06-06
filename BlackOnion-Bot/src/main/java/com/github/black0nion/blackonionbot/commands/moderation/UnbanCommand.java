package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UnbanCommand extends Command {
	
	public UnbanCommand() {
		this.setCommand("unban", "unyeet")
			.setSyntax("<@User>")
			.setRequiredArgumentCount(1)
			.setRequiredPermissions(Permission.BAN_MEMBERS)
			.setRequiredBotPermissions(Permission.BAN_MEMBERS);
	}
	
	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final List<BlackMember> mentionedMembers = message.getMentionedBlackMembers();
		if (mentionedMembers.size() != 0) {
			final BlackUser bannedUser = mentionedMembers.get(0).getBlackUser();
			guild.retrieveBan(bannedUser).queue(ban -> {
				cmde.success("uban", "userunbanned", "bannedfor", new Placeholder("reason", "**" + ban.getReason() + "**"));
			});
			guild.unban(bannedUser).queue();
		} else
			try {
				if (!Utils.isLong(args[1])) {
					cmde.sendPleaseUse();
					return;
				}
				
				guild.retrieveBanById(args[1]).queue(ban -> {
					final String reason = ban.getReason();
					guild.unban(ban.getUser()).queue();
					cmde.success("unban", "userunbanned", "bannedfor", new Placeholder("reason", "**" + reason + "**"));
				}, fail -> {
					cmde.error("usernotfound", "tagornameuser");
				});
			} catch (final Exception ignored) {}
	}
}