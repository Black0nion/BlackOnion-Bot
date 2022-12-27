package com.github.black0nion.blackonionbot.commands.slash;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandEvent extends AbstractCommandEvent<SlashCommand, SlashCommandInteractionEvent> implements SlashCommandEventJdaUtils {

	private final TextChannel channel;

	public SlashCommandEvent(SlashCommandInteractionEvent e, BlackGuild guild, BlackMember member, BlackUser user) {
		this(null, e, guild, member, user);
	}

	public SlashCommandEvent(SlashCommand cmd, SlashCommandInteractionEvent e, BlackGuild guild, BlackMember member, BlackUser user) {
		super(cmd, e, guild, member, user);
		this.channel = e.getChannel().asTextChannel();
	}

	public TextChannel getChannel() {
		return channel;
	}
}
