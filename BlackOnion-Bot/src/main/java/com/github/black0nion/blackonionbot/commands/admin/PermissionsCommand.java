/**
 *
 */
package com.github.black0nion.blackonionbot.commands.admin;

import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 */
public class PermissionsCommand extends Command {

    public PermissionsCommand() {
	this.setCommand("permissions", "custompermissions").setHidden().setSyntax("<add | remove | list> <@User> [CustomPermission...]").setRequiredArgumentCount(2);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	message.delete().queue();
	final String mode = args[1];
	if (Utils.equalsOneIgnoreCase(mode, "add", "remove", "list")) {
	    final List<BlackUser> mentionedBlackUsers = message.getMentionedBlackUsers();
	    if (mentionedBlackUsers.isEmpty()) {
		cmde.sendPleaseUse();
		return;
	    }
	    final BlackUser user = mentionedBlackUsers.get(0);
	    if (mode.equalsIgnoreCase("list")) {
		final List<CustomPermission> perms = user.getPermissions();
		cmde.success("permissions", "permissionsof", perms.size() != 0 ? ("`[" + perms.stream().map(CustomPermission::name).collect(Collectors.joining(", ")) + "]`") : "empty", new Placeholder("user", user.getFullName()));
	    } else {
		if (mode.equalsIgnoreCase("remove")) {
		    final List<CustomPermission> removedPerms = user.removePermissions(CustomPermission.parse(Utils.subArray(args, 3)));
		    cmde.success("permissions", "removedperms", "removedpermslist", new Placeholder("perms", removedPerms.size() != 0 ? removedPerms : cmde.getTranslation("empty")), new Placeholder("user", user.getFullName()));
		} else if (mode.equalsIgnoreCase("add")) {
		    final List<CustomPermission> removedPerms = user.addPermissions(CustomPermission.parse(Utils.subArray(args, 3)));
		    cmde.success("permissions", "addedperms", "addedpermslist", new Placeholder("perms", removedPerms.size() != 0 ? removedPerms : cmde.getTranslation("empty")), new Placeholder("user", user.getFullName()));
		}
	    }
	} else {
	    cmde.sendPleaseUse();
	}
    }
}