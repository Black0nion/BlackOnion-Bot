/**
 *
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author _SIM_
 *
 */
public class WarnCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String REASON = "reason";

	public WarnCommand() {
		super(builder(Commands.slash("warn", "Used to warn a user.")
				.addOption(OptionType.USER, USER, "The user to warn.", true)
				.addOption(OptionType.STRING, REASON, "The reason for the warn.", true))
						.setRequiredPermissions(Permission.KICK_MEMBERS));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e,
			@NotNull BlackMember member, @NotNull BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var warnUserOption = e.getOption(USER);
		var warnUser = Objects.requireNonNull(warnUserOption).getAsUser();
		var warnMember = warnUserOption.getAsMember();
		var reason = e.getOption(REASON, OptionMapping::getAsString);
		Warn warn;

		if (reason.length() > 512) {
			cmde.send("reasonoption");
			return;
		}

		if (warnMember != null) {
			if (member.canInteract(warnMember)) {
				warn = new Warn(guild.getIdLong(), author.getIdLong(), warnMember.getIdLong(),
						System.currentTimeMillis(), reason);

				var memberToWarn = BlackMember.from(guild.retrieveMemberById(warnMember.getId()).submit().join());
				if (memberToWarn != null) {
					memberToWarn.warn(warn);
					cmde.send("memberwarned");
				}
			}
		} else {
			warn = new Warn(guild.getIdLong(), author.getIdLong(), warnUser.getIdLong(), System.currentTimeMillis(),
					reason);
			var userToWarn = BlackUser.from(e.getJDA().retrieveUserById(warnUser.getId()).submit().join());
			userToWarn.warn(warn);
			cmde.send("userwarned");
		}
	}
}
