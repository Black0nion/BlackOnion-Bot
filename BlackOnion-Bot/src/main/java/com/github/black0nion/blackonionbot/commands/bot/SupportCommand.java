package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SupportCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "support" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("Support").addField("Discord Server:", "[Invite Link](https://discord.gg/ZzGKy9RCBY)", false)
				.addField("Creators", "[SIMULATAN](https://github.com/SIMULATAN), [ManuelP](https://github.com/ManuelPuchner), [MatseCrafter_304](https://github.com/MatseCrafter-304), [blaumeise](https://github.com/blaumeise20)", false)
				.build()).queue();
	}

	@Override
	public Category getCategory() {
		return Category.BOT;
	}
}