package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.config.api.Config;
import com.github.black0nion.blackonionbot.config.ConfigFileLoader;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Optional;

public class StatusCommand extends SlashCommand {

	private static final String STATUS = "status";

	public StatusCommand(Config config) {
		super(builder(Commands.slash("status", "Set the status of the bot").addOptions(
			new OptionData(OptionType.STRING, STATUS, "The OnlineStatus of the bot", true)
				.addChoices(Arrays.stream(OnlineStatus.values()).map(m -> new Command.Choice(m.name(), m.name())).toList())
		)).setAdminGuild(), config);
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		OnlineStatus status = Utils.parse(OnlineStatus.class, e.getOption(STATUS, OptionMapping::getAsString));
		if (status == null) {
			cmde.send("invalidrole");
			return;
		}
		config.setOnlineStatus(status);
		ConfigFileLoader.saveConfig();
		cmde.send("newstatus", new Placeholder(STATUS, status.name()));

		e.getJDA().getPresence().setStatus(status);
	}

	public static OnlineStatus getStatusFromConfig(Config config) {
		return Optional.ofNullable(config.getOnlineStatus()).orElse(OnlineStatus.ONLINE);
	}
}
