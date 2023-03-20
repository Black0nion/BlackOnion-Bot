package com.github.black0nion.blackonionbot.commands.slash.impl.admin;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.mutable.api.Settings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

	private final Settings settings;

	public StatusCommand(Settings settings) {
		super(builder(Commands.slash("status", "Set the status of the bot").addOptions(
			new OptionData(OptionType.STRING, STATUS, "The OnlineStatus of the bot", true)
				.addChoices(Arrays.stream(OnlineStatus.values()).map(m -> new Command.Choice(m.name(), m.name())).toList())
		)).setAdminGuild());
		this.settings = settings;
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		OnlineStatus status = Utils.parse(OnlineStatus.class, e.getOption(STATUS, OptionMapping::getAsString));
		if (status == null) {
			cmde.send("invalidrole");
			return;
		}
		settings.setOnlineStatus(status);
		cmde.send("newstatus", new Placeholder(STATUS, status.name()));

		e.getJDA().getPresence().setStatus(status);
	}

	public static OnlineStatus getStatusFromConfig(Settings settings) {
		return Optional.ofNullable(settings.getOnlineStatus()).orElse(OnlineStatus.ONLINE);
	}
}
