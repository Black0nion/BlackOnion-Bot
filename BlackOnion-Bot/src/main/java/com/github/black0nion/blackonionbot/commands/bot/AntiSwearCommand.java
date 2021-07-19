
package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AntiSwearCommand extends SlashCommand {

    public AntiSwearCommand() {
	this.setData(new CommandData("antiswear", "Automatically deletes profanity from a message").addOptions(new OptionData(OptionType.STRING, "type", "The type to set the AntiSwear feature to", false).addChoice("replace", "Remove profanity in a message and resend it").addChoice("delete", "Completely delete Messages containing profanity").addChoice("off", "Don't do anything"))).setRequiredPermissions(Permission.ADMINISTRATOR).premiumRequired();
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_MANAGE)) return;
	final OptionMapping typeRaw = e.getOption("type");
	if (typeRaw != null) {
	    final String type = typeRaw.getAsString();
	    if (type.equalsIgnoreCase("replace")) {
		guild.setAntiSwearType(AntiSwearType.REPLACE);
		cmde.success("antiswearstatuschanged", cmde.getTranslation("antiswearis", new Placeholder("status", cmde.getTranslation("remove"))));
		return;
	    } else if (type.equalsIgnoreCase("delete")) {
		guild.setAntiSwearType(AntiSwearType.DELETE);
		cmde.success("antiswearstatuschanged", cmde.getTranslation("antiswearis", new Placeholder("status", cmde.getTranslation("delete"))));
		return;
	    } else if (type.equalsIgnoreCase("off")) {
		guild.setAntiSwearType(AntiSwearType.OFF);
		cmde.success("antiswearstatuschanged", cmde.getTranslation("antiswearis", new Placeholder("status", cmde.getTranslation("off"))));
		return;
	    } else {
		cmde.sendPleaseUse();
		return;
	    }
	} else {
	    cmde.success("antiswearstatus", "howtoantisweartoggle", new Placeholder("status", cmde.getTranslation(guild.getAntiSpoilerType().name())), new Placeholder("command", SlashCommandExecutedEvent.getCommandHelp(guild, author, this)));
	    return;
	}
    }
}