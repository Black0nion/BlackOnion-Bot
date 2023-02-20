package com.github.black0nion.blackonionbot.commands.slash;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommandBuilder;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.wrappers.StartsWithLinkedList;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A builder class for creating a {@link SlashCommand}.<br>
 * Handles the creation of the command and its properties like required permissions, if the command is toggleable or the required {@link GuildType GuildType}.
 */
@SuppressWarnings("unused")
public class SlashCommandBuilder extends AbstractCommandBuilder<SlashCommandBuilder, SlashCommandData> {

	private final Map<String, StartsWithLinkedList> autoComplete = new HashMap<>();

	public SlashCommandBuilder(final SlashCommandData data) {
		super(data);
	}

	public SlashCommandBuilder autocomplete(String command, Collection<String> values) {
		this.autoComplete.put(command, new StartsWithLinkedList(values));
		return this;
	}

	public Map<String, StartsWithLinkedList> getAutoComplete() {
		return autoComplete;
	}
}
