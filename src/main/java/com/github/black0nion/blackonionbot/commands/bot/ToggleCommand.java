package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.ChainableAtomicReference;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.config.api.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
		ChainableAtomicReference<SlashCommand> currentCommand = new ChainableAtomicReference<>();
		this.updateAutoComplete(COMMAND, slashCommandBase.getCommands().entrySet().stream()
			.filter(e ->
				((currentCommand.setAndGet(e.getValue().getValue())).getRequiredCustomPermissions() == null
					|| currentCommand.get().getRequiredCustomPermissions().length == 0)
					&& currentCommand.get().isToggleable())
			.map(Map.Entry::getKey)
			.toList());
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, @NotNull BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		final SlashCommand command = SlashCommandBase.getCommand(e.getOption(COMMAND, OptionMapping::getAsString));
		if (command == null || command.isHidden(author)) {
			cmde.send("commandnotfound");
			return;
		}

		if (!command.isToggleable()) {
			cmde.error("commandcantbetoggled", "thiscommandcantbetoggled");
			return;
		}

		Boolean newStatus = e.getOption("on", OptionMapping::getAsBoolean);
		if (newStatus == null) {
			cmde.send("commandstatus", new Placeholder("cmd", command.getName()), new Placeholder("status", cmde.getTranslation(guild.isCommandActivated(command) ? "on" : "off")));
		} else {
			if (guild.setCommandActivated(command, newStatus)) {
				final String commandName = command.getName().toUpperCase();
				cmde.success("commandtoggled", "commandisnow", new Placeholder(COMMAND, commandName), new Placeholder("status", cmde.getTranslation(newStatus ? "on" : "off")));
			}
		}
	}
}
