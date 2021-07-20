package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class PrefixCommand extends SlashCommand {

    public PrefixCommand() {
	this.setData(new CommandData("prefix", "Sets the prefix of the bot for this server").addOption(OptionType.STRING, "prefix", "The new prefix", true)).setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String newPrefix = e.getOption("prefix").getAsString();
	if (newPrefix.toCharArray().length > 10) {
	    cmde.error("toolong", "undertenchars");
	    return;
	}
	guild.setPrefix(newPrefix.replace(" ", ""));
	cmde.success("prefixchanged", "myprefixis", new Placeholder("prefix", guild.getPrefix()));
    }
}