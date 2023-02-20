package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AntiSpoilerCommand extends SlashCommand {
	private static final String STATUS = "status";

	public AntiSpoilerCommand() {
		super(builder(Commands.slash("antispoiler", "Used to deleted/disable/replace the anti-spoiler system.")
			.addOptions(new OptionData(OptionType.STRING, STATUS, "The status", false)
				.addChoices(Arrays.stream(AntiSpoilerSystem.AntiSpoilerType.values()).map(m -> new Command.Choice(m.name(), m.name().toLowerCase())).toList())))
			.setRequiredPermissions(Permission.MESSAGE_MANAGE));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		var status = e.getOption(STATUS, OptionMapping::getAsString);

		final AntiSpoilerSystem.AntiSpoilerType parsedType = AntiSpoilerSystem.AntiSpoilerType.parse(status);
		if (status != null) {
			if (parsedType != null) {
				guildSettings.getAntiSpoiler().setValue(parsedType);
				cmde.success("antispoilerstatuschanged", "antispoileris", new Placeholder(STATUS, parsedType.name()));
			} else {
				cmde.sendPleaseUse();
			}
		} else {
			cmde.success("antispoilerstatus", "howtoantispoilertoggle",
				new Placeholder(STATUS, guildSettings.getAntiSpoiler().getValue().name()),
				new Placeholder("command", "`" + cmde.getCommandHelp() + "`"));
		}
	}
}
