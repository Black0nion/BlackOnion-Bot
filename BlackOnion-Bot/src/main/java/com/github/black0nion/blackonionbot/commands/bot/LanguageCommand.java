package com.github.black0nion.blackonionbot.commands.bot;

import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LanguageCommand extends SlashCommand {

    public LanguageCommand() {
	this.setData(new CommandData("language", "Set your language").addOptions(new OptionData(OptionType.STRING, "lang", "The language code of your desired language", false).addChoices(LanguageSystem.getLanguages().entrySet().stream().map(entry -> new Command.Choice(entry.getValue().getFullName(), entry.getKey())).collect(Collectors.toList()))));
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final OptionMapping option = e.getOption("lang");
	if (option != null) {
	    final String optionAsString = option.getAsString();
	    final Language newLanguage = LanguageSystem.getLanguageFromName(optionAsString.toUpperCase());
	    if (newLanguage != null) {
		author.setLanguage(newLanguage);
		cmde.success(newLanguage.getTranslation("languageupdated"), newLanguage.getTranslation("newlanguage"), new Placeholder("newlang", newLanguage.getFullName()));
	    } else if (optionAsString.equalsIgnoreCase("list")) {
		cmde.success("Languages", "Valid Languages:", LanguageSystem.validLanguages);
	    } else {
		cmde.error("Language doesn't exist!", "Valid Languages:", LanguageSystem.validLanguages);
	    }
	} else {
	    String language = "";
	    final Language userLanguage = author.getLanguage();
	    if (userLanguage != null) {
		language = userLanguage.getFullName();
	    } else {
		language = LanguageSystem.getDefaultLanguage().getFullName();
	    }
	    cmde.success("Languages", "Your Language: " + language, "To change your language, use\n`/" + SlashCommandExecutedEvent.getCommandHelp(guild, author, this) + "`.");
	}
    }
}