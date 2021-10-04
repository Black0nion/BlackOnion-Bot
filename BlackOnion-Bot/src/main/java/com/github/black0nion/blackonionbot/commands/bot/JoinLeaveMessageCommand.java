package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class JoinLeaveMessageCommand extends SlashCommand {

    public JoinLeaveMessageCommand() {
	this.setData(new CommandData("joinleavemessage", "Sets the join or leave message")
		.addOptions(new OptionData(OptionType.STRING, "mode", "Set the Join or Leave message", true)
			.addChoice("Join", "join")
			.addChoice("Leave", "leave"))
		.addOption(OptionType.STRING, "message", "The message to set", true));
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String newMessage = e.getOption("message").getAsString();
	if (e.getOption("mode").getAsString().equalsIgnoreCase("join")) {
	    guild.setJoinMessage(newMessage);
	    cmde.success("setjoinmessage", "joinmessagesestto", new Placeholder("msg", "`" + newMessage + "`"));
	} else if (e.getOption("mode").getAsString().equalsIgnoreCase("leave")) {
	    guild.setLeaveMessage(newMessage);
	    cmde.success("setleavemessage", "leavemessagesestto", new Placeholder("msg", "`" + newMessage + "`"));
	} else {
	    cmde.sendPleaseUse();
	}
    }
}