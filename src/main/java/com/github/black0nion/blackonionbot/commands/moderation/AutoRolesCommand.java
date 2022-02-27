package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AutoRolesCommand extends Command {

	public AutoRolesCommand() {
		this.setCommand("autoroles", "autorole")
				.setSyntax("<create | remove / delete> <@role | roleid>")
				.setRequiredArgumentCount(2)
				.setRequiredPermissions(Permission.MANAGE_ROLES)
				.setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final List<String> argsList = Arrays.asList(args);
		if (argsList.contains("@everyone") || argsList.contains("@here")) {
			cmde.error("invalidrole", "iseveryone");
			return;
		}

		final List<Role> roles = message.getMentionedRoles();
		Long roleID = null;
		Role role = null;
		if (roles.size() != 0) {
			if (roles.get(0).getAsMention().equals(args[2])) {
				role = roles.get(0);
				roleID = roles.get(0).getIdLong();
			} else {
				cmde.sendPleaseUse();
				return;
			}
		} else {
			try {
				role = guild.getRoleById(args[2]);
				if (role != null) roleID = Long.parseLong(args[2]);
			} catch (final NumberFormatException ignored) {
			}
			if (roleID == null) {
				cmde.sendPleaseUse();
				return;
			}
		}

		final List<Long> tempList = guild.getAutoRoles();

		if (args[1].equalsIgnoreCase("create")) {
			if (tempList.contains(roleID)) {
				cmde.success("alreadyexisting", "thisalreadyexisting");
				return;
			} else tempList.add(roleID);
			guild.addAutoRole(roleID);
			cmde.success("autorolecreated", "autorolecreatedinfo", new Placeholder("role", role.getAsMention()));
		} else if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete")) {
			if (!tempList.contains(roleID)) {
				cmde.error("notfound", "thisnotfound");
				return;
			} else tempList.remove(roleID);
			guild.removeAutoRole(roleID);
			cmde.success("autorolesdeleted", "autoroledeletedinfo", new Placeholder("role", role.getAsMention()));
		} else cmde.sendPleaseUse();
	}
}