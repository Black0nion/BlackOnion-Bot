package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.common.Command;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.utils.ChainableAtomicReference;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.StartsWithLinkedList;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class ToggleCommand extends SlashCommand {

	public static final String COMMAND = "command";
	private final SlashCommandBase slashCommandBase;

	public ToggleCommand(SlashCommandBase slashCommandBase, Config config) {
		super(builder(Commands.slash("toggle", "Toggle commands")
				.addOptions(
					new OptionData(OptionType.STRING, COMMAND, "Command to toggle", true, true),
					new OptionData(OptionType.BOOLEAN, "on", "The new status", false))
			)
			.autocomplete(COMMAND, slashCommandBase.getCommands().keySet())
			.setRequiredPermissions(Permission.MANAGE_SERVER)
			.notToggleable(),
		config);
		this.slashCommandBase = slashCommandBase;
	}

	public void updateAutoComplete() {
		ChainableAtomicReference<Command> currentCommand = new ChainableAtomicReference<>();
		this.updateAutoComplete(COMMAND, slashCommandBase.getCommands().entrySet().stream()
			.filter(e ->
			{
				currentCommand.setAndGet(e.getValue().getSecond());
				return currentCommand.get().getRequiredCustomPermissions().isEmpty() && currentCommand.get().isToggleable();
			})
			.map(Map.Entry::getKey)
			.collect(Collectors.toCollection(StartsWithLinkedList::new)));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, Member member, @NotNull User author, @NotNull Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		final Command command = slashCommandBase.getCommand(e.getOption(COMMAND, OptionMapping::getAsString));
		if (command == null || (command instanceof SlashCommand slashCommand && slashCommand.isHidden(userSettings))) {
			cmde.send("commandnotfound");
			return;
		}

		if (!command.isToggleable()) {
			cmde.error("commandcantbetoggled", "thiscommandcantbetoggled");
			return;
		}

		Boolean newStatus = e.getOption("on", OptionMapping::getAsBoolean);
		if (newStatus == null) {
			cmde.send("commandstatus", new Placeholder("cmd", command.getName()), new Placeholder("status", cmde.getTranslation(guildSettings.isCommandActivated(command) ? "on" : "off")));
		} else {
			guildSettings.setCommandActivated(command, newStatus);
			final String commandName = command.getName().toUpperCase();
			cmde.success("commandtoggled", "commandisnow", new Placeholder(COMMAND, commandName), new Placeholder("status", cmde.getTranslation(newStatus ? "on" : "off")));
		}
	}
}