package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AutoRolesCommand extends SlashCommand {

    public AutoRolesCommand() {
	this.setData(new CommandData("autoroles", "Gives a specific role to a user on join").addOptions(new OptionData(OptionType.STRING, "type", "The action to perform", true).addChoice("Create", "create").addChoice("Delete", "delete").addChoice("List", "list"), new OptionData(OptionType.ROLE, "role", "The affected role", false))).setRequiredPermissions(Permission.MANAGE_ROLES).setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String type = e.getOptionsByType(OptionType.STRING).get(0).getAsString();
	if (type.equalsIgnoreCase("list")) {
	    final String roles = guild.getAutoRoles().stream().map(c -> guild.getRoleById(c)).filter(Objects::nonNull).map(Role::getName).collect(Collectors.joining("\n- "));
	    cmde.success("rolelist",
		    roles.equals("") ? "empty" : roles);
	} else {
	    final List<OptionMapping> roleOptions = e.getOptionsByType(OptionType.ROLE);
	    if (roleOptions.size() == 0 || roleOptions.get(0).getAsRole() == null) {
		cmde.error("invalidrole", "noroleinput");
		return;
	    }
	    if (type.equalsIgnoreCase("create")) {
		final Role role = roleOptions.get(0).getAsRole();
		final long roleID = role.getIdLong();
		final List<Long> tempList = guild.getAutoRoles();
		if (tempList.contains(roleID)) {
		    cmde.success("alreadyexisting", "thisalreadyexisting");
		    return;
		} else {
		    tempList.add(roleID);
		}
		guild.addAutoRole(roleID);
		cmde.success("autorolecreated", "autorolecreatedinfo", new Placeholder("role", role.getAsMention()));
	    } else if (type.equalsIgnoreCase("delete")) {
		final Role role = roleOptions.get(0).getAsRole();
		final long roleID = role.getIdLong();
		final List<Long> tempList = guild.getAutoRoles();
		if (!tempList.contains(roleID)) {
		    cmde.error("notfound", "thisnotfound");
		    return;
		} else {
		    tempList.remove(roleID);
		}
		guild.removeAutoRole(roleID);
		cmde.success("autorolesdeleted", "autoroledeletedinfo", new Placeholder("role", role.getAsMention()));
	    } else {
		cmde.sendPleaseUse();
	    }
	}
    }
}