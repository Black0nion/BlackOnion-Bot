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
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		cmde.success("poll", String.join(" ", Utils.removeFirstArg(args)), "polltutorial", msg -> {
			msg.addReaction("tick:822036832422068225").queue();
			msg.addReaction("cross:822036805117018132").queue();
		});
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var yesQuestion = e.getOption(YES_QUESTION, OptionMapping::getAsString);
		var noQuestion = e.getOption(NO_QUESTION, OptionMapping::getAsString);

		if (yesQuestion == null || noQuestion == null) {
			cmde.error("poll", "You must provide both a yes and no question.");
		} else {
		}
	}
}