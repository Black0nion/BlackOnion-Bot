package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WarnsCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String WARN_ID = "warnid";

	public WarnsCommand() {
		super(builder(Commands.slash("warns", "Used to get the warns of a user.")
				.addOption(OptionType.USER, USER, "The user to get the warns of.")
				.addOption(OptionType.NUMBER, WARN_ID, "The ID of the warn to get."))
						.setRequiredPermissions(Permission.KICK_MEMBERS));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
			BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var warnUserOption = e.getOption(USER);
		var warnUser = Objects.requireNonNull(warnUserOption).getAsUser();
		var warnMember = warnUserOption.getAsMember();

		if (warnMember != null) {
			var blackMember = BlackMember.from(guild.retrieveMemberById(warnMember.getId()).submit().join());
			if (blackMember == null) {
				cmde.send("memberisinvalid");
				return;
			}
			final List<Warn> warns = blackMember.getWarns();
			StringBuilder result = new StringBuilder("empty");
			if (!warns.isEmpty()) {
				result = new StringBuilder();
				for (final Warn warn : warns) {
					result.append("\n`- ").append(BotInformation.datePattern.format(new Date(warn.date())))
							.append(": `<@").append(warn.issuer()).append(">` > Reason: ")
							.append(Optional.ofNullable(warn.reason()).map(str -> str.replace("`", "")).orElse(null))
							.append(" (ID: ").append(warn.date()).append(")`");
				}
			}
			cmde.success("warns", result.toString());
		} else {
			var blackUser = BlackUser.from(e.getJDA().retrieveUserById(warnUser.getId()).submit().join());
			if (blackUser == null) {
				cmde.send("userisinvalid");
			}
			final List<Warn> warns = blackUser.getWarns();
			StringBuilder result = new StringBuilder("empty");
			if (!warns.isEmpty()) {
				result = new StringBuilder();
				for (final Warn warn : warns) {
					result.append("\n`- ").append(BotInformation.datePattern.format(new Date(warn.date())))
							.append(": `<@").append(warn.issuer()).append(">` > Reason: ")
							.append(Optional.ofNullable(warn.reason()).map(str -> str.replace("`", "")).orElse(null))
							.append(" (ID: ").append(warn.date()).append(")`");
				}
			}
			cmde.success("warns", result.toString());
		}
	}
}
