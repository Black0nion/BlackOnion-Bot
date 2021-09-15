package com.github.black0nion.blackonionbot.commands.misc;

import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.utils.Pair;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class TestCommand extends Command {

    public TestCommand() {
	this.setCommand("test", "tet").setHidden();
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    final List<CommandData> collect = SlashCommandBase.commands.values().stream().map(Pair::getValue).map(SlashCommand::getData).collect(Collectors.toList());
	    guild.updateCommands().addCommands(collect).queue(yeet -> {
		System.out.println(yeet);
	    }, Throwable::printStackTrace);
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
    }
}