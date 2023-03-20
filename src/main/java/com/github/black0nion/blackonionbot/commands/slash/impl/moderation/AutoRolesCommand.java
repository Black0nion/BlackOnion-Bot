package com.github.black0nion.blackonionbot.commands.slash.impl.moderation;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.ListSetting;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoRolesCommand extends SlashCommand {
	private static final String CREATE_COMMAND = "create";
	private static final String REMOVE_COMMAND = "remove";
	private static final String LIST_COMMAND = "list";
	private static final String CREATE_ROLE = "role";
	private static final String REMOVE_ROLE = "role";

	public AutoRolesCommand() {
		super(builder(Commands.slash("autoroles", "Used to manage the auto roles of the server.").addSubcommands(
				new SubcommandData(CREATE_COMMAND, "Used to create a new auto role.").addOption(OptionType.ROLE,
					CREATE_ROLE, "The role to add.", true),
				new SubcommandData(REMOVE_COMMAND, "Used to remove an auto role.").addOption(OptionType.ROLE,
					REMOVE_ROLE, "The role to remove.", true),
				new SubcommandData(LIST_COMMAND, "Used to list all auto roles."))
			).setRequiredPermissions(Permission.MANAGE_ROLES)
			.setRequiredBotPermissions(Permission.MESSAGE_MANAGE));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, Member member, User author, @NotNull BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		switch (cmde.getSubcommandName()) {
			case CREATE_COMMAND -> setCreateCommand(cmde, e, guild, guildSettings);
			case REMOVE_COMMAND -> setRemoveCommand(cmde, e, guild, guildSettings);
			case LIST_COMMAND -> cmde.success("autorolelist", "%roles%", new Placeholder("roles", guildSettings.getAutoRoles().getValue().stream()
				.map(guild::getRoleById)
				.filter(Objects::nonNull)
				.map(IMentionable::getAsMention)
				.collect(Collectors.joining("\n"))));
			default -> cmde.send("invalidsubcommand");
		}
	}

	public void setCreateCommand(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackGuild guild, GuildSettings guildSettings) {
		var role = e.getOption(CREATE_ROLE, OptionMapping::getAsRole);
		var roleId = Objects.requireNonNull(role).getIdLong();
		final ListSetting<Long, Set<Long>> autoRoles = guildSettings.getAutoRoles();
		if (autoRoles.contains(roleId)) {
			cmde.success("alreadyexisting", "thisalreadyexisting");
			return;
		}

		autoRoles.add(roleId);
		cmde.success("autorolecreated", "autorolecreatedinfo", new Placeholder("role", role.getAsMention()));
	}

	public void setRemoveCommand(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackGuild guild, GuildSettings guildSettings) {
		var role = e.getOption(REMOVE_ROLE, OptionMapping::getAsRole);
		var roleId = Objects.requireNonNull(role).getIdLong();
		final ListSetting<Long, Set<Long>> autoRoles = guildSettings.getAutoRoles();

		if (!autoRoles.contains(roleId)) {
			cmde.error("notfound", "thisnotfound");
			return;
		}

		autoRoles.remove(roleId);
		cmde.success("autoroledeleted", "autoroledeletedinfo", new Placeholder("role", role.getAsMention()));
	}
}
