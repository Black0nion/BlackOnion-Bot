package com.github.black0nion.blackonionbot.commands.bot;

import java.util.Map;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LanguageCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"language", "lang", "locale", "sprache"};
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (args.length >= 2) {
			if (LanguageSystem.getLanguageFromName(args[1].toUpperCase()) != null) {
				LanguageSystem.updateUserLocale(author.getId(), args[1]);
				channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, e.getGuild()).addField(LanguageSystem.getTranslation("languageupdated", author, e.getGuild()), LanguageSystem.getTranslation("newlanguage", author, e.getGuild()) + " " + LanguageSystem.getLanguageFromName(args[1]).getName() + " (" + args[1].toUpperCase() + ")", false).build()).queue();
			} else {
				String validLanguages = "\n";
				for (Map.Entry<String, Language> entry : LanguageSystem.getLanguages().entrySet()) {
					validLanguages += entry.getValue().getName() + " (" + entry.getKey() + ")\n";
				}
				if (args[1].equalsIgnoreCase("list")) {
					channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).setTitle("Languages").addField("Valid Languages", validLanguages, false).build()).queue();
				} else {
					channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).setTitle("Language doesn't exist").addField("Valid languages:", validLanguages, false).build()).queue();
				}
			}
		} else {
			String language = "";
			final Language userLanguage = LanguageSystem.getUserLanguage(author.getId());
			final Language guildLanguage = LanguageSystem.getGuildLanguage(e.getGuild().getId());
			if (userLanguage != null) {
				language = userLanguage.getName() + " (" + userLanguage.getLanguageCode() + ")";
			} else if (guildLanguage != null) {
				language = guildLanguage.getName() + " (" + guildLanguage.getLanguageCode() + ")";
			} else {
				language = LanguageSystem.getDefaultLanguage().getName() + " (" + LanguageSystem.getDefaultLanguage().getLanguageCode() + ")";
			}
			channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).setTitle("Languages").addField("Your Language: " + language, "To change your language, use ``" + BotInformation.getPrefix(guild) + getCommand()[0] + " " + getSyntax() + "``\nTo get a list of all valid language codes use ``" + BotInformation.getPrefix(guild) + "language list``", false).build()).queue();
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
