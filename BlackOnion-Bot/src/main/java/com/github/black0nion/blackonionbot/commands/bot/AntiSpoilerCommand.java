package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AntiSpoilerCommand extends SlashCommand {

    public AntiSpoilerCommand() {
	this.setData(new CommandData("antispoiler", "Automatically deletes spoilers from a message").addOptions(new OptionData(OptionType.STRING, "type", "The type to set the AntiSpoiler feature to", false).addChoice("replace", "Remove spoilers in a message and resend it").addChoice("delete", "Completely delete Messages containing spoilers").addChoice("off", "Don't do anything"))).setRequiredPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (Utils.handleRights(guild, author, null, Permission.MESSAGE_MANAGE)) return;
	final OptionMapping typeRaw = e.getOption("type");
	if (typeRaw != null) {
	    final String type = typeRaw.getAsString();
	    if (type.equalsIgnoreCase("replace")) {
		guild.setAntiSpoilerType(AntiSpoilerType.REPLACE);
		cmde.success("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("remove"))));
		return;
	    } else if (type.equalsIgnoreCase("delete")) {
		guild.setAntiSpoilerType(AntiSpoilerType.DELETE);
		cmde.success("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("delete"))));
		return;
	    } else if (type.equalsIgnoreCase("off")) {
		guild.setAntiSpoilerType(AntiSpoilerType.OFF);
		cmde.success("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("off"))));
		return;
	    } else {
		cmde.sendPleaseUse();
		return;
	    }
	} else {
	    cmde.success("antispoilerstatus", "howtoantispoilertoggle", new Placeholder("status", cmde.getTranslation(guild.getAntiSpoilerType().name())), new Placeholder("command", SlashCommandExecutedEvent.getCommandHelp(guild, author, this)));
	    return;
	}
    }
}