package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LanguageCommand extends Command {
	
	public LanguageCommand() {
		this.setCommand("language", "lang", "locale", "sprache")
			.setSyntax("[language code | list]");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (args.length >= 2) {
			if (LanguageSystem.getLanguageFromName(args[1].toUpperCase()) != null) {
				final Language newLang = LanguageSystem.getLanguageFromName(args[1].toUpperCase());
				author.setLanguage(newLang);
				cmde.success(newLang.getTranslationNonNull("languageupdated"), newLang.getTranslationNonNull("newlanguage"), new Placeholder("newlang", newLang.getName() + " (" + newLang.getLanguageCode() + ")"));
			} else if (args[1].equalsIgnoreCase("list"))
				cmde.success("Languages", "Valid Languages:", LanguageSystem.validLanguages);
			else
				cmde.error("Language doesn't exist!", "Valid Languages:", LanguageSystem.validLanguages);
		} else {
			String language = "";
			final Language userLanguage = author.getLanguage();
			if (userLanguage != null)
				language = userLanguage.getName() + " (" + userLanguage.getLanguageCode() + ")";
			else
				language = LanguageSystem.getDefaultLanguage().getName() + " (" + LanguageSystem.getDefaultLanguage().getLanguageCode() + ")";
			cmde.success("Languages", "Your Language: " + language, "To change your language, use `" + CommandEvent.getCommandHelp(guild, author, this) + "`.\nTo get a list of all valid language codes use `" + guild.getPrefix() + "language list" + "`");
		}
	}
}