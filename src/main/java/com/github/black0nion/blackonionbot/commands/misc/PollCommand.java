package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PollCommand extends SlashCommand {
	private static final String YES_QUESTION = "yes";
	private static final String NO_QUESTION = "no";

	public PollCommand() {
		super(builder(Commands.slash("poll", "Used to create a poll.")
				.addOption(OptionType.String, YES_QUESTION,"The question to ask for yes.", true)
				.addOption(OptionType.String, NO_QUESTION,"The question to ask for no.", true)));
	}
	
	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		//TODO: Set it were the discord user sets a poll channel and the poll is sent to it.
	}
}