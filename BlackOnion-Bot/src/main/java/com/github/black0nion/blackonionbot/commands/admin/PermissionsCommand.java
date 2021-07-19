/**
 *
 */
package com.github.black0nion.blackonionbot.commands.admin;

import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author _SIM_
 */
public class PermissionsCommand extends SlashCommand {

    public PermissionsCommand() {
	this.setData(new CommandData("permissions", "Sets the permissions for a specific user").addOptions(new OptionData(OptionType.STRING, "type", "The sub command", true).addChoice("Add", "add").addChoice("Remove", "remove").addChoice("List", "list"), new OptionData(OptionType.USER, "user", "The user to perform the action on", true), new OptionData(OptionType.STRING, "permissions", "The permissions to add / remove (required for add and remove)"))).setHidden();
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String mode = e.getOptionsByName("mode").get(0).getAsString();
	if (Utils.equalsOneIgnoreCase(mode, "add", "remove", "list")) {
	    final BlackUser user = BlackUser.from(e.getOptionsByType(OptionType.USER).get(0).getAsUser());
	    if (mode.equalsIgnoreCase("list")) {
		final List<CustomPermission> perms = user.getPermissions();
		cmde.success("permissions", "permissionsof", perms.size() != 0 ? ("`[" + perms.stream().map(CustomPermission::name).collect(Collectors.joining(", ")) + "]`") : "empty", new Placeholder("user", user.getFullName()));
	    } else {
		if (e.getOptionsByName("permissions").size() == 0) {
		    cmde.sendPleaseUsePrivate();
		    return;
		}
		final String inputRaw = e.getOptionsByName("permissions").get(0).getAsString();
		if (inputRaw.equals("") || inputRaw.length() == 0) {
		    cmde.sendPleaseUsePrivate();
		    return;
		}
		final String[] input = inputRaw.split(" ");
		if (mode.equalsIgnoreCase("remove")) {
		    final List<CustomPermission> removedPerms = user.removePermissions(CustomPermission.parse(input));
		    cmde.successPrivate("permissions", "removedperms", "removedpermslist", new Placeholder("perms", removedPerms.size() != 0 ? removedPerms : cmde.getTranslation("empty")), new Placeholder("user", user.getFullName()));
		} else if (mode.equalsIgnoreCase("add")) {
		    final List<CustomPermission> removedPerms = user.addPermissions(CustomPermission.parse(input));
		    cmde.successPrivate("permissions", "addedperms", "addedpermslist", new Placeholder("perms", removedPerms.size() != 0 ? removedPerms : cmde.getTranslation("empty")), new Placeholder("user", user.getFullName()));
		}
	    }
	} else {
	    cmde.sendPleaseUsePrivate();
	}
    }
}