package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.dynamic.api.Settings;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class SetBotLogsChannelCommand extends SlashCommand {

	private final Settings settings;

	public SetBotLogsChannelCommand(Settings settings) {
		super(builder("setbotlogschannel", "Used to set the channel where bot logs are sent.")
			.setAdminGuild());
		this.settings = settings;
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, @NotNull TextChannel channel) {
		settings.setLogsChannel(channel.getIdLong());
		cmde.success("savedbotlogschannel", "thisisbotlogschannel");
	}
}
