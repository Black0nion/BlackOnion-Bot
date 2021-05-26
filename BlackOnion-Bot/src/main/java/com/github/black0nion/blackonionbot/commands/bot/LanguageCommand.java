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

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LanguageCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "language", "lang", "locale", "sprache" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (args.length >= 2) {
			if (LanguageSystem.getLanguageFromName(args[1].toUpperCase()) != null) {
				author.setLanguage(LanguageSystem.getLanguageFromName(args[1]));
				message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("languageupdated", author, guild), LanguageSystem.getTranslation("newlanguage", author, guild) + " " + LanguageSystem.getLanguageFromName(args[1]).getName() + " (" + args[1].toUpperCase() + ")", false).build()).queue();
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
			final Language userLanguage = author.getLanguage();
			final Language guildLanguage = author.getLanguage();
			if (userLanguage != null) {
				language = userLanguage.getName() + " (" + userLanguage.getLanguageCode() + ")";
			} else if (guildLanguage != null) {
				language = guildLanguage.getName() + " (" + guildLanguage.getLanguageCode() + ")";
			} else {
				language = LanguageSystem.getDefaultLanguage().getName() + " (" + LanguageSystem.getDefaultLanguage().getLanguageCode() + ")";
			}
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle("Languages").addField("Your Language: " + language, "To change your language, use ``" + guild.getPrefix() + getCommand()[0] + " " + getSyntax() + "``\nTo get a list of all valid language codes use ``" + guild.getPrefix() + "language list``", false).build()).queue();
		}
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