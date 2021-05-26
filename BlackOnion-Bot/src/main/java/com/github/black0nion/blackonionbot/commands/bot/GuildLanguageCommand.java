package com.github.black0nion.blackonionbot.commands.bot;

import java.util.Map;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildLanguageCommand extends Command {

	@Override
	public String[] getCommand() {
		return new String[] { "guildlanguage", "guildlang", "guildlocale", "guildsprache", "serverlang", "serverlanguage", "serversprache" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (args.length >= 2) {
			final Language newLanguage = LanguageSystem.getLanguageFromName(args[1].toUpperCase());
			if (newLanguage != null) {
				guild.setLanguage(newLanguage);
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("languageupdated", LanguageSystem.getTranslation("newlanguage", author, guild) + " " + newLanguage.getName() + " (" + newLanguage.getLanguageCode().toUpperCase() + ")", false).build()).queue();
			} else {
				String validLanguages = "\n";
				for (Map.Entry<String, Language> entry : LanguageSystem.getLanguages().entrySet()) {
					validLanguages += entry.getValue().getName() + " (" + entry.getKey() + ")\n";
				}
				if (args[1].equalsIgnoreCase("list")) {
					message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle("Languages").addField("Valid Languages", validLanguages, false).build()).queue();
				} else {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).setTitle("Language doesn't exist").addField("Valid languages:", validLanguages, false).build()).queue();
				}
			}
		} else {
			String language = "";
			final Language guildLanguage = guild.getLanguage();
			if (guildLanguage != null) {
				language = guildLanguage.getName() + " (" + guildLanguage.getLanguageCode() + ")";
			} else {
				language = LanguageSystem.getDefaultLanguage().getName() + " (" + LanguageSystem.getDefaultLanguage().getLanguageCode() + ")";
			}
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle("Languages").addField("Guild Language: " + language, "To change the guild language, use " + Utils.getCommandHelp(guild, author, this) + "\nTo get a list of all valid language codes use `" + guild.getPrefix() + "language list`", false).build()).queue();
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.ADMINISTRATOR};
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public String getSyntax() {
		return "<language code>";
	}
}