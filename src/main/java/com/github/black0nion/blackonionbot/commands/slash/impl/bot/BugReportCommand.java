package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class BugReportCommand extends SlashCommand {

	public BugReportCommand() {
		super(builder("bug", "Displays information on how to report a bug"));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, UserSettings userSettings) {
		cmde.success("foundabug", "plsreportbug", "inourgithubrepo", new Placeholder("repo", "[BlackOnion-Bot](https://github.com/Black0nion/BlackOnion-Bot)"));
	}
}
