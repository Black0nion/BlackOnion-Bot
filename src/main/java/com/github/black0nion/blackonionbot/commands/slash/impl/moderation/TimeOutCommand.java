package com.github.black0nion.blackonionbot.commands.slash.impl.moderation;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.misc.exception.TooLongException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.AwaitDone;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * This command was taken from <a href="https://github.com/YusufsDiscordbot">Yusuf's Discord Bot</a>
 */
public class TimeOutCommand extends SlashCommand {
	private static final String USER_OPTION = "userid";
	private static final String REASON_OPTION = "reason";
	private static final String TIME_OUT_NAME = "timeout";

	// max time
	static final int MAX_TIMEOUT_DURATION_MIN = 40320; // 28 days

	public TimeOutCommand() {
		super(builder(Commands.slash(TIME_OUT_NAME, "Used to timeout a userid")
			.addSubcommands(
				new SubcommandData("add", "Used to add a timeout to a userid")
					.addOption(OptionType.USER, USER_OPTION, "The user who you want to time out", true)
					.addOption(OptionType.STRING, REASON_OPTION, "Why the user should be timed out", false)
					.addOptions(Utils.getDurationOptions("time out")),
				new SubcommandData("remove", "Used to remove a timeout from an user")
					.addOption(OptionType.USER, USER_OPTION, "The user you want to remove the timeout from", true)
					.addOption(OptionType.STRING, REASON_OPTION, "Why the timeout should be removed from the user", false))
			)
			.setRequiredPermissions(Permission.MODERATE_MEMBERS));
	}

	public static Duration parseDuration(GenericCommandInteractionEvent e) {
		var min = e.getOption(Utils.MINUTES, OptionMapping::getAsLong);
		var hour = e.getOption(Utils.HOURS, OptionMapping::getAsLong);
		var day = e.getOption(Utils.DAYS, OptionMapping::getAsLong);
		var week = e.getOption(Utils.WEEKS, OptionMapping::getAsLong);

		Duration dur = Duration.ofMinutes(
			(min != null ? min : 0) +
				(hour != null ? hour * 60 : 0) +
				(day != null ? day * 60 * 24 : 0) +
				(week != null ? week * 60 * 24 * 7 : 0)
		);
		if (dur.toMinutes() > MAX_TIMEOUT_DURATION_MIN) {
			throw new IllegalArgumentException(new TooLongException());
		}
		if (dur.toMinutes() <= 0) {
			throw new IllegalArgumentException("Duration must be greater than 0");
		}
		return dur;
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, UserSettings userSettings) {
		switch (cmde.getSubcommandName()) {
			case "add" -> addTimeout(cmde, e);
			case "remove" -> removeTimeout(cmde, e);
			default -> cmde.sendPleaseUse();
		}
	}

	private static void addTimeout(SlashCommandEvent cmde, SlashCommandInteractionEvent e) {
		var timeOutMember = e.getOption(USER_OPTION, OptionMapping::getAsMember);
		if (timeOutMember == null) {
			cmde.send("notamember");
			return;
		}
		@Nullable
		var reason = e.getOption(REASON_OPTION, OptionMapping::getAsString);

		if (timeOutMember.isTimedOut()) {
			cmde.send("istimedout");
			return;
		}

		try {
			Duration duration = parseDuration(e);
			doTimeout(cmde, timeOutMember, duration, reason);
		} catch (IllegalArgumentException ex) {
			if (ex.getCause() instanceof TooLongException) {
				cmde.send("toolongduration", new Placeholder("duration", MAX_TIMEOUT_DURATION_MIN));
			} else {
				cmde.send("invalidduration");
			}
		}
	}

	private static void doTimeout(SlashCommandEvent cmde, Member timeOutMember, Duration totalDuration, String reason) {
		timeOutMember.timeoutFor(totalDuration).reason("[" + cmde.getUser().getId() + "]" + (reason != null ? " " + reason : "")).queue(ignored -> {
			final AwaitDone<InteractionHook> await = new AwaitDone<>();
			final String message = cmde.getTranslation(reason != null ? "timedout" : "timedoutnoreason",
				new Placeholder("member", timeOutMember.getUser().getAsMention()),
				new Placeholder("duration", Utils.formatDuration(totalDuration)),
				new Placeholder("reason", reason));

			cmde.send(message, await::done);
			timeOutMember.getUser().openPrivateChannel()
				.flatMap(channel -> channel.sendMessage(cmde.getTranslation("timedoutu", new Placeholder("guild", cmde.getGuild().getEscapedName()), new Placeholder("reason", reason))))
				.queue(null, Utils.getCantSendHandler(await, message, cmde));
		}, cmde::exception);
	}

	private static void removeTimeout(SlashCommandEvent cmde, SlashCommandInteractionEvent e) {
		var timeOutMember = e.getOption(USER_OPTION, OptionMapping::getAsMember);
		@Nullable String reason = e.getOption(REASON_OPTION, OptionMapping::getAsString);

		if (timeOutMember == null) {
			cmde.send("notamember");
			return;
		}

		if (!timeOutMember.isTimedOut()) {
			cmde.send("isnottimedout");
			return;
		}

		timeOutMember.removeTimeout().reason("[" + cmde.getUser().getId() + "]" + (reason != null ? " " + reason : "")).queue();
		cmde.send("timedoutremoved");
	}
}
