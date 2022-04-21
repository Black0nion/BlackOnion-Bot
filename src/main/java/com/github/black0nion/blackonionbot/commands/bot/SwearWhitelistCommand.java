package com.github.black0nion.blackonionbot.commands.bot;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SwearWhitelistCommand extends SlashCommand {
	private static final String ADD_CHANNEL = "add_channel";
	private static final String ADD_ROLE = "add_role";
	private static final String ADD_PERMISSION = "add_permission";
	private static final String REMOVE_ROLE = "remove_role";
	private static final String REMOVE_CHANNEL = "remove_channel";
	private static final String REMOVE_PERMISSION = "remove_permission";
	private static final String ROLE = "role";
	private static final String CHANNEL = "channel";
	public SwearWhitelistCommand() {
		super(builder(Commands.slash("swear_whitelist", "the swear whitelist command" )
				.addSubcommands(new SubcommandData(ADD_ROLE, "adds a role to the swear whitelist")
						.addOption(OptionType.ROLE, ROLE, "the role to add to the swear whitelist", true),
						new SubcommandData(REMOVE_ROLE, "removes a role from the swear whitelist")
								.addOption(OptionType.ROLE, ROLE, "the role to remove from the swear whitelist", true),
						new SubcommandData(ADD_CHANNEL, "adds a channel to the swear whitelist")
								.addOption(OptionType.CHANNEL, CHANNEL, "the channel to add to the swear whitelist", true),
						new SubcommandData(REMOVE_CHANNEL, "removes a channel from the swear whitelist")
								.addOption(OptionType.CHANNEL, CHANNEL, "the channel to remove from the swear whitelist", true),
						new SubcommandData(ADD_PERMISSION, "adds a permission to the swear whitelist")
								.addOption(OptionType.STRING, ADD_PERMISSION, "the permission to add to the swear whitelist", true),
						new SubcommandData(REMOVE_PERMISSION, "removes a permission from the swear whitelist")
								.addOption(OptionType.STRING, REMOVE_PERMISSION, "the permission to remove from the swear whitelist", true)
				)).setRequiredPermissions(Permission.ADMINISTRATOR));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		switch(Objects.requireNonNull(e.getSubcommandName())) {
			case ADD_ROLE -> addRole(cmde, e, member, author, guild, channel);
			case REMOVE_ROLE -> removeRole(cmde, e, member, author, guild, channel);
			case ADD_PERMISSION -> addPermission(cmde, e, member, author, guild, channel);
			case REMOVE_PERMISSION -> removePermission(cmde, e, member, author, guild, channel);
			case ADD_CHANNEL -> addChannel(cmde, e, member, author, guild, channel);
			case REMOVE_CHANNEL -> removeChannel(cmde, e, member, author, guild, channel);
			default -> cmde.sendPleaseUse();
		}
	}

	private void addRole(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var addRole = e.getOption(ROLE, OptionMapping::getAsRole);

		final List<String> mentionedStuff = new ArrayList<>();
		mentionedStuff.add(addRole.getAsMention());
		List<String> newWhitelist = guild.getAntiSwearWhitelist();
		if (newWhitelist == null) {
			newWhitelist = new ArrayList<>();
		}
		final List<String> temp = new ArrayList<>(newWhitelist);
		temp.retainAll(mentionedStuff);
		newWhitelist.removeAll(temp);
		newWhitelist.addAll(mentionedStuff);
		guild.setAntiSwearWhitelist(newWhitelist);
		cmde.success("whitelistupdated", cmde.getTranslation("addedtowhitelist", new Placeholder("add", mentionedStuff.toString())));
	}

	private void removeRole(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var removeRole = e.getOption(ROLE, OptionMapping::getAsRole);

		final List<String> mentionedStuff = new ArrayList<>();
		mentionedStuff.add(removeRole.getAsMention());
		List<String> newWhitelist = guild.getAntiSwearWhitelist();
		if (newWhitelist == null) {
			newWhitelist = new ArrayList<>();
		}
		newWhitelist.removeAll(mentionedStuff);
		guild.setAntiSwearWhitelist(newWhitelist);
		cmde.success("whitelistupdated", cmde.getTranslation("removedfromwhitelist", new Placeholder("removed", mentionedStuff.toString())));
	}

	private void addPermission(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var perm = e.getOption(ADD_PERMISSION, OptionMapping::getAsString);

		final List<String> mentionedStuff = new ArrayList<>();
		mentionedStuff.add(perm);
		List<String> newWhitelist = guild.getAntiSwearWhitelist();
		if (newWhitelist == null) {
			newWhitelist = new ArrayList<>();
		}
		final List<String> temp = new ArrayList<>(newWhitelist);
		temp.retainAll(mentionedStuff);
		newWhitelist.removeAll(temp);
		newWhitelist.addAll(mentionedStuff);
		guild.setAntiSwearWhitelist(newWhitelist);
		cmde.success("whitelistupdated", cmde.getTranslation("addedtowhitelist", new Placeholder("add", mentionedStuff.toString())));
	}

	private void removePermission(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var perm = e.getOption(REMOVE_PERMISSION, OptionMapping::getAsString);

		final List<String> mentionedStuff = new ArrayList<>();
		mentionedStuff.add(perm);
		List<String> newWhitelist = guild.getAntiSwearWhitelist();
		if (newWhitelist == null) {
			newWhitelist = new ArrayList<>();
		}
		newWhitelist.removeAll(mentionedStuff);
		guild.setAntiSwearWhitelist(newWhitelist);
		cmde.success("whitelistupdated", cmde.getTranslation("removedfromwhitelist", new Placeholder("removed", mentionedStuff.toString())));
	}

	private void addChannel(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var addChannel = e.getOption(ADD_CHANNEL, OptionMapping::getAsTextChannel);

		final List<String> mentionedStuff = new ArrayList<>();
		mentionedStuff.add(addChannel.getAsMention());
		List<String> newWhitelist = guild.getAntiSwearWhitelist();
		if (newWhitelist == null) {
			newWhitelist = new ArrayList<>();
		}
		final List<String> temp = new ArrayList<>(newWhitelist);
		temp.retainAll(mentionedStuff);
		newWhitelist.removeAll(temp);
		newWhitelist.addAll(mentionedStuff);
		guild.setAntiSwearWhitelist(newWhitelist);
		cmde.success("whitelistupdated", cmde.getTranslation("addedtowhitelist", new Placeholder("add", mentionedStuff.toString())));
	}

	private void removeChannel(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var removeChannel = e.getOption(REMOVE_CHANNEL, OptionMapping::getAsTextChannel);

		final List<String> mentionedStuff = new ArrayList<>();
		mentionedStuff.add(removeChannel.getAsMention());
		List<String> newWhitelist = guild.getAntiSwearWhitelist();
		if (newWhitelist == null) {
			newWhitelist = new ArrayList<>();
		}
		newWhitelist.removeAll(mentionedStuff);
		guild.setAntiSwearWhitelist(newWhitelist);
		cmde.success("whitelistupdated", cmde.getTranslation("removedfromwhitelist", new Placeholder("removed", mentionedStuff.toString())));
	}
}