package com.github.black0nion.blackonionbot.commands.slash.impl.admin;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionsCommand extends SlashCommand {

	private static final String PERMISSION = "permission";
	private static final OptionData USER_OPTION = new OptionData(OptionType.USER, "user", "The user to modify the permissions of", true);

	public PermissionsCommand() {
		super(builder(
			Commands.slash("permissions", "Set the CustomPermissions of a user").addSubcommands(
				new SubcommandData("add", "Add permissions to the user").addOptions(
					USER_OPTION,
					new OptionData(OptionType.STRING, PERMISSION, "The permission to remove", true)
						.addChoices(Arrays.stream(CustomPermission.values()).map(perm -> new Command.Choice(perm.name(), perm.name())).toList())
				), new SubcommandData("remove", "Remove permissions from the user").addOptions(
					USER_OPTION,
					new OptionData(OptionType.STRING, PERMISSION, "The permission to remove", true)
						.addChoices(Utils.add(
							Arrays.stream(CustomPermission.values()).map(perm -> new Command.Choice(perm.name(), perm.name())).collect(Collectors.toList()),
							new Command.Choice("All permissions", "all")))),
				new SubcommandData("list", "List the permissions of the user").addOption(OptionType.USER, "user", "The user to list the permissions of", true)
			)
		).setAdminGuild());
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		final String mode = cmde.getSubcommandName();

		if (Utils.equalsOneIgnoreCase(mode, "add", "remove", "list")) {
			final User user = Objects.requireNonNull(e.getOption("user", OptionMapping::getAsUser));
			if (user.isBot()) {
				cmde.send("invaliduser");
				return;
			}
			if (mode.equalsIgnoreCase("list")) {
				final Set<CustomPermission> perms = userSettings.getPermissions().getValue();
				cmde.send("permissionsof",
					new Placeholder("user", Utils.escapeMarkdown(user.getAsTag())),
					new Placeholder("perms", !perms.isEmpty() ? perms.stream()
						.map(CustomPermission::name)
						.map(Utils::list)
						.collect(Collectors.joining("\n")) : "empty"
					)
				);
				return;
			}
			String permissionString = e.getOption(PERMISSION, OptionMapping::getAsString);
			CustomPermission permission = CustomPermission.parse(permissionString);
			if (mode.equalsIgnoreCase("remove")) {
				if (permission == null && !Objects.equals(permissionString, "all"))
					throw new NullPointerException("CustomPermission is null!");
				if (permission != null)
					userSettings.getPermissions().remove(permission);
				else
					userSettings.getPermissions().reset();
				cmde.send("permissionsremoved", new Placeholder("perms", permissionString), new Placeholder("user", Utils.escapeMarkdown(user.getAsTag())));
			} else if (mode.equalsIgnoreCase("add")) {
				if (permission == null) throw new NullPointerException("CustomPermission is null!");
				userSettings.getPermissions().add(permission);
				cmde.send("permissionsadded", new Placeholder("perms", permissionString), new Placeholder("user", Utils.escapeMarkdown(user.getAsTag())));
			}
		} else {
			cmde.sendPleaseUse();
		}
	}
}
