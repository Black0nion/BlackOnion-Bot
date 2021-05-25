package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AntiSwearCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "antiswear" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		
		if (!guild.isPremium()) {
			channel.sendMessage(EmbedUtils.premiumRequired(author, guild)).queue();
			return;
		}
		
		if (Utils.handleRights(guild, author, channel, Permission.MESSAGE_MANAGE)) return;
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
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
				return;
			}
		} else {
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("antispoilerstatus", author, guild).replace("%status%", LanguageSystem.getTranslation(guild.getAntiSpoilerType().name(), author, guild)), LanguageSystem.getTranslation("howtoantispoilertoggle", author, guild).replace("%command%", "``" + Utils.getCommandHelp(guild, author, this) + "``"), false).build()).queue();
			return;
		}
	}
	
	@Override
	public String getSyntax() {
		return "<on | off>";
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.ADMINISTRATOR };
	}
}