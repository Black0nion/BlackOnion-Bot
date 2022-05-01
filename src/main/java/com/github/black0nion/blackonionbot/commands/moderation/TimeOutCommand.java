package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * This command was taken from <a href="https://github.com/YusufsDiscordbot">Yusuf's Discord Bot</a>
 */
public class TimeOutCommand extends SlashCommand {
	private static final String USER_OPTION = "user";
	private static final String REASON_OPTION = "reason";
	private static final String TIME_OUT_NAME = "time_out";

	// Units
	private static final String MINUTES = "minutes";
	private static final String HOURS = "hours";
	private static final String DAYS = "days";
	private static final String WEEKS = "weeks";
	// max time
	private static final int MAX_TIMEOUT_DURATION_MIN = 40320; // 28 days
	private static final int MAX_TIMEOUT_DURATION_HOUR = 672; // 28 days
	private static final int MAX_TIMEOUT_DURATION_DAY = 28; // 28 days
	private static final int MAX_TIMEOUT_DURATION_WEEK = 4; // 28 days

	public TimeOutCommand() {
		super(builder(Commands.slash(TIME_OUT_NAME, "Used to timeout a user")
			.addOption(OptionType.USER, USER_OPTION, "The user who you want to time out", true)
			.addOption(OptionType.STRING, REASON_OPTION, "Why the user should be timed out", true)
			.addOptions(
				new OptionData(OptionType.INTEGER, MINUTES, "The length of the time out in minutes", false).setRequiredRange(1, MAX_TIMEOUT_DURATION_MIN))
			.addOptions(
				new OptionData(OptionType.INTEGER, HOURS, "The length of the time out in hours", false).setRequiredRange(1, MAX_TIMEOUT_DURATION_HOUR))
			.addOptions(
				new OptionData(OptionType.INTEGER, DAYS, "The length of the time out in days", false).setRequiredRange(1, MAX_TIMEOUT_DURATION_DAY))
			.addOptions(
				new OptionData(OptionType.INTEGER, WEEKS, "The length of the time out in weeks", false).setRequiredRange(1, MAX_TIMEOUT_DURATION_WEEK)))
			.setRequiredPermissions(Permission.MODERATE_MEMBERS));
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var timeOutMember = e.getOption(USER_OPTION, OptionMapping::getAsMember);
		if (timeOutMember == null) {
			cmde.send("notamember");
			return;
		}
		var reason = e.getOption(REASON_OPTION, OptionMapping::getAsString);
		var min = e.getOption(MINUTES, OptionMapping::getAsLong);
		var hour = e.getOption(HOURS, OptionMapping::getAsLong);
		var day = e.getOption(DAYS, OptionMapping::getAsLong);
		var week = e.getOption(WEEKS, OptionMapping::getAsLong);

		if (timeOutMember.isTimedOut()) {
			cmde.send("istimedout");
			return;
		}

		final var error = cmde.getTranslation("toolongduration", new Placeholder("duration", MAX_TIMEOUT_DURATION_MIN));

		if (min != null && hour != null && day != null && week != null) {
			var hourToMin = hour * 60;
			var dayToMin = day * 24 * 60;
			var weekToMin = week * 7 * 24 * 60;
			var totalDuration = Duration.ofMinutes(hourToMin + dayToMin + weekToMin + min);
			if (totalDuration.toMinutes() > MAX_TIMEOUT_DURATION_MIN) {
				cmde.send(error);
			} else {
				doTimeout(timeOutMember, totalDuration, reason, cmde);
			}
		} else if (min != null && hour != null && day != null && week == null) {
			var hourToMin = hour * 60;
			var dayToMin = day * 24 * 60;
			var totalDuration = Duration.ofMinutes(hourToMin + dayToMin + min);
			if (totalDuration.toMinutes() > MAX_TIMEOUT_DURATION_MIN) {
				cmde.send(error);
			} else {
				doTimeout(timeOutMember, totalDuration, reason, cmde);
			}
		} else if (min != null && hour != null && day == null && week == null) {
			var hourToMin = hour * 60;
			var totalDuration = Duration.ofMinutes(hourToMin + min);
			if (totalDuration.toMinutes() > MAX_TIMEOUT_DURATION_MIN) {
				cmde.send(error);
			} else {
				doTimeout(timeOutMember, totalDuration, reason, cmde);
			}
		} else if (min != null && hour == null && day == null && week == null) {
			var totalDuration = Duration.ofMinutes(min);
			if (totalDuration.toMinutes() > MAX_TIMEOUT_DURATION_MIN) {
				cmde.send(error);
			} else {
				doTimeout(timeOutMember, totalDuration, reason, cmde);
			}
		} else if (min == null && hour != null && day == null && week == null) {
			var totalDuration = Duration.ofMinutes(1440);
			doTimeout(timeOutMember, totalDuration, reason, cmde);
		}
	}

	private static void doTimeout(Member timeOutMember, Duration totalDuration, String reason, SlashCommandEvent cmde) {
		timeOutMember.timeoutFor(totalDuration).reason(reason).queue();
		cmde.send("timedout",
			new Placeholder("member", timeOutMember.getUser().getAsMention()),
			new Placeholder("duration", Utils.formatDuration(totalDuration)),
			new Placeholder(REASON_OPTION, reason));
	}
}