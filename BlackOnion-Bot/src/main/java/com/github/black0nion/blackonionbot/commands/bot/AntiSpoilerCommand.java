package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AntiSpoilerCommand extends Command {
	
	public AntiSpoilerCommand() {
		this.setCommand("antispoiler", "as")
			.setSyntax("[on | delete | off]")
			.setRequiredPermissions(Permission.MESSAGE_MANAGE);
	}

	@Override
	public String[] getCommand() {
		return new String[] { "antispoiler", "as" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (Utils.handleRights(guild, author, null, Permission.MESSAGE_MANAGE)) return;
		if (args.length >= 2) {
			final String type = args[1];
			if (type.equalsIgnoreCase("remove")) {
				guild.setAntiSpoilerType(AntiSpoilerType.REMOVE);
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", LanguageSystem.getReplacedTranslation("%antispoileris%", author, guild, "status", "on"), false).build()).queue();
				return;
			} else if (type.equalsIgnoreCase("delete")) {
				guild.setAntiSpoilerType(AntiSpoilerType.DELETE);
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", LanguageSystem.getReplacedTranslation("%antispoileris%", author, guild, "status", "delete"), false).build()).queue();
				return;
			} else if (type.equalsIgnoreCase("off")) {
				guild.setAntiSpoilerType(AntiSpoilerType.OFF);
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", LanguageSystem.getReplacedTranslation("%antispoileris%", author, guild, "status", "off"), false).build()).queue();
				return;
			} else {
				cmde.sendPleaseUse();
				return;
			}
		} else {
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("antispoilerstatus", author, guild).replace("%status%", LanguageSystem.getTranslation(guild.getAntiSpoilerType().name(), author, guild)), LanguageSystem.getTranslation("howtoantispoilertoggle", author, guild).replace("%command%", "``" + CommandEvent.getCommandHelp(guild, author, this) + "``"), false).build()).queue();
			return;
		}
	}
}