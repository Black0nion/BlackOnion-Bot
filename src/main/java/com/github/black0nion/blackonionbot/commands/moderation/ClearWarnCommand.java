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

import java.util.List;
import java.util.Objects;

/**
 * @author _SIM_
 */
public class ClearWarnCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String WARN_ID = "warnid";
	public ClearWarnCommand() {
		super(builder(Commands.slash("clearwarn", "Used to clear a warn.")
				.addOption(OptionType.USER, USER, "The user to clear the warn from.", true)
				.addOption(OptionType.NUMBER, WARN_ID, "The id of the warn to clear.", true))
						.setRequiredPermissions(Permission.KICK_MEMBERS));
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
			BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var warnUserOption = e.getOption(USER);
		var warnUser = Objects.requireNonNull(warnUserOption).getAsUser();
		var warnMember = warnUserOption.getAsMember();
		var warnId = e.getOption(WARN_ID, OptionMapping::getAsLong);

		if (warnMember != null) {
			var blackMember = BlackMember.from(guild.retrieveMemberById(warnMember.getId()).submit().join());
			if (blackMember == null) {
				cmde.send("memberisinvalid");
				return;
			}
			final List<Warn> memberWarns = blackMember.getWarns();
			for (final Warn warn : memberWarns) {
				if (warn.date() == warnId) {
					blackMember.deleteWarn(warn);
					cmde.success("warndeleted", "warndeleted");
					return;
				} else {
					cmde.error("warnidisinvalid", "warnidisinvalid");
				}
			}
		} else {
			var blackUser = BlackUser.from(e.getJDA().retrieveUserById(warnUser.getId()).submit().join());
			final List<Warn> userWarns = blackUser.getWarns();
			for (final Warn warn : userWarns) {
				if (warn.date() == warnId) {
					blackUser.deleteWarn(warn);
					cmde.success("warndeleted", "warndeleted");
					return;
				} else {
					cmde.error("warnidisinvalid", "warnidisinvalid");
				}
			}
		}
	}
}
