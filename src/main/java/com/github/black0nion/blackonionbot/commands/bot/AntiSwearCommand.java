
package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AntiSwearCommand extends Command {

	public AntiSwearCommand() {
		this.setCommand("antiswear")
			.setSyntax("<replace | delete | off>")
			.setRequiredArgumentCount(1)
			.setRequiredPermissions(Permission.ADMINISTRATOR)
			.premiumRequired();
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_MANAGE)) return;
		if (args.length >= 2) {
			final String type = args[1];
			if (type.equalsIgnoreCase("replace")) {
				guild.setAntiSwearType(AntiSwearType.REPLACE);
				message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antiswearstatuschanged", cmde.getTranslation("antiswearis", new Placeholder("status", cmde.getTranslation("remove"))), false).build()).queue();
				return;
			} else if (type.equalsIgnoreCase("delete")) {
				guild.setAntiSwearType(AntiSwearType.DELETE);
				message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antiswearstatuschanged", cmde.getTranslation("antiswearis", new Placeholder("status", cmde.getTranslation("delete"))), false).build()).queue();
				return;
			} else if (type.equalsIgnoreCase("off")) {
				guild.setAntiSwearType(AntiSwearType.OFF);
				message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antiswearstatuschanged", cmde.getTranslation("antiswearis", new Placeholder("status", cmde.getTranslation("off"))), false).build()).queue();
				return;
			} else {
				message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", CommandEvent.getPleaseUse(guild, author, this), false).build()).queue();
				return;
			}
		} else {
			message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("antiswearstatus", author, guild).replace("%status%", LanguageSystem.getTranslation(guild.getAntiSwearType().name(), author, guild)), LanguageSystem.getTranslation("howtoantisweartoggle", author, guild).replace("%command%", "``" + CommandEvent.getCommandHelp(guild, this) + "``"), false).build()).queue();
			return;
		}
	}
}