package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AutoRolesCommand extends SlashCommand {
	private static final String CREATE_COMMAND = "create";
	private static final String REMOVE_COMMAND = "remove";
	private static final String CREATE_ROLE = "role";
	private static final String REMOVE_ROLE = "role";

	public AutoRolesCommand() {
		super(builder(Commands.slash("autoroles", "Used to manage the auto roles of the server.").addSubcommands(
			new SubcommandData(CREATE_COMMAND, "Used to create a new auto role.").addOption(OptionType.ROLE,
				CREATE_ROLE, "The role to add.", true),
			new SubcommandData(REMOVE_COMMAND, "Used to remove an auto role.").addOption(OptionType.ROLE,
				REMOVE_ROLE, "The role to remove.", true))).setRequiredPermissions(Permission.MANAGE_ROLES)
			.setRequiredBotPermissions(Permission.MESSAGE_MANAGE));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		switch (Objects.requireNonNull(e.getSubcommandName())) {
			case CREATE_COMMAND -> setCreateCommand(cmde, e, guild);
			case REMOVE_COMMAND -> setRemoveCommand(cmde, e, guild);
			default -> cmde.send("invalidsubcommand");
		}
	}

	public void setCreateCommand(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackGuild guild) {
		var role = e.getOption(CREATE_ROLE, OptionMapping::getAsRole);
		var roleId = Objects.requireNonNull(role).getIdLong();
		final List<Long> tempList = guild.getAutoRoles();
		if (tempList.contains(roleId)) {
			cmde.success("alreadyexisting", "thisalreadyexisting");
			return;
		} else
			tempList.add(roleId);
		guild.addAutoRole(roleId);
		cmde.success("autorolecreated", "autorolecreatedinfo", new Placeholder("role", role.getAsMention()));
	}

	public void setRemoveCommand(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackGuild guild) {
		var role = e.getOption(REMOVE_ROLE, OptionMapping::getAsRole);
		var roleId = Objects.requireNonNull(role).getIdLong();
		final List<Long> tempList = guild.getAutoRoles();

		if (!tempList.contains(roleId)) {
			cmde.error("notfound", "thisnotfound");
			return;
		} else
			tempList.remove(roleId);
		guild.removeAutoRole(roleId);
		cmde.success("autorolesdeleted", "autoroledeletedinfo", new Placeholder("role", role.getAsMention()));
	}
}