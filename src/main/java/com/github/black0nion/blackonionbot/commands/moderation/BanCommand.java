package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BanCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String REASON = "reason";
	private static final String DEL_DAYS = "delDays";

	public BanCommand() {
		super(builder(Commands.slash("ban", "Used to ban a user from the server.")
				.addOption(OptionType.USER, USER, "The user to ban.", true)
				.addOption(OptionType.STRING, REASON, "The reason for the ban.", true)
				.addOptions(new OptionData(OptionType.INTEGER, DEL_DAYS,
						"The amount of days to delete" + " messages from the user.", true).addChoices(deleationDays)))
								.setRequiredPermissions(Permission.BAN_MEMBERS)
								.setRequiredBotPermissions(Permission.BAN_MEMBERS));
	}

	private static final List<Command.Choice> deleationDays = List.of(new Command.Choice("None", 0),
			new Command.Choice("One Day", 1), new Command.Choice("Two Days", 2), new Command.Choice("Three Days", 3),
			new Command.Choice("Four Days", 4), new Command.Choice("Five Days", 5), new Command.Choice("Six Days", 6),
			new Command.Choice("Seven Days", 7));

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
			BlackUser author, BlackGuild guild, TextChannel channel) {
		var userOptionMapping = e.getOption(USER);
		var banUser = Objects.requireNonNull(userOptionMapping, "The user is null").getAsUser();
		var banMember = userOptionMapping.getAsMember();
		var reason = e.getOption(REASON, OptionMapping::getAsString);
		var delDays = e.getOption(DEL_DAYS, OptionMapping::getAsInt);

		if (reason.length() > 512) {
			cmde.send("reasonoption");
			return;
		}

		if (banMember != null) {
			var banMemberAsUser = banMember.getUser();
			guild.retrieveBan(banMemberAsUser).queue(
					fail -> e.reply("The member " + banMemberAsUser.getAsTag() + " is already banned.").queue(),
					success -> guild.ban(banMember, delDays, reason)
							.queue(success2 -> e.reply("The member " + banMemberAsUser.getAsTag() + " has been"
									+ " banned for" + " the reason " + reason).queue()));
		} else {
			guild.retrieveBan(Objects.requireNonNull(banUser)).queue(
					fail -> e.reply("The user " + banUser.getAsTag() + " is already banned.").queue(),
					success -> guild.ban(banUser, delDays, reason).queue(success2 -> e.reply(
							"The user " + banUser.getAsTag() + " has been" + " banned for" + " the reason " + reason)
							.queue()));
		}
	}
}
