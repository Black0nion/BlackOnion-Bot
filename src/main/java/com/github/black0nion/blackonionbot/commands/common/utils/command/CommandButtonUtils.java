package com.github.black0nion.blackonionbot.commands.common.utils.command;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public interface CommandButtonUtils extends CommandUtilsBase {

	/**
	 * Creates a button that'll be handled by {@link com.github.black0nion.blackonionbot.bot.SlashCommandBase#onButtonInteraction(ButtonInteractionEvent)}.<br>
	 * Therefore, the button won't be handled by the Command itself.
	 * @return a button with the ID "cancel"
	 */
	default Button getCancelButton() {
		return Button.secondary("cancel", Emoji.fromUnicode("U+2716"));
	}

	default Button getConfirmButton() {
		return Button.success("confirm", Emoji.fromUnicode("U+2705"));
	}

	default String getId() {
		return getName();
	}

	/**
	 * @return the command name, a pipe (|) and the supplied data, joined by an underscore (_)
	 */
	default String enrichId(String... id) {
		return getId() + "|" + String.join("_", id);
	}

	/**
	 * Parses the id and returns the supplied data
	 */
	default String[] getIdParts(String id) {
		return id.split("\\|")[1].split("_");
	}
}
