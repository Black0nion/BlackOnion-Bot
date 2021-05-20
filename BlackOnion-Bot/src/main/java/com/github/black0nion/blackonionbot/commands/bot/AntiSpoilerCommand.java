package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AntiSpoilerCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "antispoiler", "as" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		if (Utils.handleRights(guild, author, null, Permission.MESSAGE_MANAGE)) return;
		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("on")) {
				GuildManager.save(guild, "antispoiler", true);
				GuildManager.save(guild, "deletespoiler", false);
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", LanguageSystem.getReplacedTranslation("%antispoileris%", author, guild, "status", "on"), false).build()).queue();
				return;
			} else if (args[1].equalsIgnoreCase("delete")) {
				GuildManager.save(guild, "antispoiler", false);
				GuildManager.save(guild, "deletespoiler", true);
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", LanguageSystem.getReplacedTranslation("%antispoileris%", author, guild, "status", "delete"), false).build()).queue();
				return;
			} else if (args[1].equalsIgnoreCase("off")) {
				GuildManager.save(guild, "antispoiler", false);
				GuildManager.save(guild, "deletespoiler", false);
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", LanguageSystem.getReplacedTranslation("%antispoileris%", author, guild, "status", "off"), false).build()).queue();
				return;
			} else {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
				return;
			}
		} else {
			boolean antispoiler = GuildManager.getBoolean(guild, "antispoiler");
			boolean deletespoiler = GuildManager.getBoolean(guild, "deletespoiler");
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslatedString("antispoilerstatus", author, guild).replace("%status%", LanguageSystem.getTranslatedString((antispoiler ? "on" : (!deletespoiler ? "off" : "delete")), author, guild)), LanguageSystem.getTranslatedString("howtoantispoilertoggle", author, guild).replace("%command%", "``" + Utils.getCommandHelp(guild, author, this) + "``"), false).build()).queue();
			return;
		}
	}
	
	@Override
	public String getSyntax() {
		return "[on | delete | off]";
	}

	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.MESSAGE_MANAGE };
	}
}