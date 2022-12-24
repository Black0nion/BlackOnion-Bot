package com.github.black0nion.blackonionbot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandEventUtilsBase {
	SlashCommand getCommand();

	SlashCommandInteractionEvent getEvent();
}
