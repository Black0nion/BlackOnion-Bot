package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SetSuggestionChannel extends SlashCommand {
	private static final String CHANNEL = "channel";
	public SetSuggestionChannel() {
		super(builder(Commands.slash("set_suggestion_channel", "Used to set the channel where suggestions are sent to.")
				.addOption(OptionType.CHANNEL, CHANNEL, "The channel to send suggestions to."))
						.setRequiredPermissions(Permission.MANAGE_CHANNEL)
						.setRequiredBotPermissions(Permission.MANAGE_SERVER));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
			BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var suggestionsChannel = e.getOption(CHANNEL, OptionMapping::getChannelType);

		if (Objects.requireNonNull(suggestionsChannel).isMessage()) {
			var textChannel = e.getOption(CHANNEL, OptionMapping::getAsTextChannel);
			guild.setSuggestionsChannel(textChannel);
		} else {
			cmde.send("nottextchannel");
			e.reply("The suggestions channel must be a text channel.").setEphemeral(true).queue();
		}
	}
}
