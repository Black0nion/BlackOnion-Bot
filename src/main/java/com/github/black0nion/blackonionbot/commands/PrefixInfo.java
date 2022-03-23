package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class PrefixInfo {

	public static void handle(final CommandEvent cmde) {
		final Message message = cmde.getMessage();
		final BlackUser author = cmde.getUser();
		if (author.isBot() || message.getMentionedUsers().size() == 0) return;
		final BlackGuild guild = cmde.getGuild();
		final BlackMember member = cmde.getMember();

		final String msgContent = message.getContentRaw();
		if (msgContent.matches("<@!?" + BotInformation.SELF_USER_ID + "+>.*")) {
			final String[] args = msgContent.split(" ");
			// @BlackOnion-Bot prefix ?
			if (args.length >= 3) {
				if (args[1].equalsIgnoreCase("prefix")) {
					if (member.hasPermission(Permission.MANAGE_SERVER)) {
						final String prefix = args[2];
						if (prefix.length() < 10) {
							guild.setPrefix(prefix);
							cmde.success("prefixchanged",  "myprefixis", new Placeholder("prefix", prefix));
						} else {
							cmde.error("toolong", "undertenchars");
						}
					} else {
						cmde.error("missingpermissions", "requiredpermissions", new Placeholder("permission", Utils.getPermissionString(Permission.ADMINISTRATOR)));
					}
					return;
				}
			}
			cmde.success("myprefixis", "changeprefix", new Placeholder("prefix", guild.getPrefix()), new Placeholder("command", "<@!" + BotInformation.SELF_USER_ID + "> prefix <prefix>"));
		}
	}
}