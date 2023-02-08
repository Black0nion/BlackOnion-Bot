package com.github.black0nion.blackonionbot.commands.slash.impl.moderation;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UnbanCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String REASON = "reason";

	public UnbanCommand() {
		super(builder(Commands.slash("unban", "Used to unban an user")
			.addOption(OptionType.USER, USER, "The user to unban", true)
			.addOption(OptionType.STRING, REASON, "The reason for the unban", false))
			.setRequiredPermissions(Permission.BAN_MEMBERS)
			.setRequiredBotPermissions(Permission.BAN_MEMBERS));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		var user = e.getOption(USER, OptionMapping::getAsUser);
		var reason = e.getOption(REASON, OptionMapping::getAsString);

		if (reason != null && reason.length() > 512) {
			cmde.send("reasonoption");
			return;
		}

		guild.retrieveBan(Objects.requireNonNull(user)).queue(
			success -> guild.unban(user).reason("[" + author.getId() + "]" + (reason != null ? " " + reason : "")).queue(
				v -> cmde.send(reason != null ? "userunbanned" : "userunbannednoreason", new Placeholder("user", user.getAsMention()), new Placeholder("reason", reason)),
				cmde::exception),
			fail ->
				new ErrorHandler()
					.handle(ErrorResponse.UNKNOWN_BAN, err -> cmde.send("bannotfound"))
					.handle(Throwable.class, cmde::exception).accept(fail)
		);
	}
}
