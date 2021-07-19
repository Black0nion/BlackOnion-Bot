package com.github.black0nion.blackonionbot.commands.bot;

import java.util.Map;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class GuildLanguageCommand extends SlashCommand {

    public GuildLanguageCommand() {
	this.setData(new CommandData("guildlanguage", "Set the language of your server").addOptions(new OptionData(OptionType.STRING, "lang", "The language code of your desired language", false).addChoices(LanguageSystem.getLanguages().entrySet().stream().map(entry -> new Command.Choice(entry.getValue().getFullName(), entry.getKey())).collect(Collectors.toList())))).setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final OptionMapping option = e.getOption("lang");
	if (option != null) {
	    final String optionAsString = option.getAsString();
	    final Language newLanguage = LanguageSystem.getLanguageFromName(optionAsString.toUpperCase());
	    if (newLanguage != null) {
		guild.setLanguage(newLanguage);
		cmde.success(newLanguage.getTranslation("languageupdated"), newLanguage.getTranslation("newlanguage"), new Placeholder("newlang", newLanguage.getFullName()));
	    } else {
		String validLanguages = "\n";
		for (final Map.Entry<String, Language> entry : LanguageSystem.getLanguages().entrySet()) {
		    validLanguages += entry.getValue().getName() + " (" + entry.getKey() + ")\n";
		}
		if (optionAsString.equalsIgnoreCase("list")) {
		    cmde.success("Languages", "Valid Languages:", validLanguages);
		} else {
		    cmde.error("Language doesn't exist!", "Valid Languages:", validLanguages);
		}
	    }
	} else {
	    String language = "";
	    final Language guildLanguage = guild.getLanguage();
	    if (guildLanguage != null) {
		language = guildLanguage.getFullName();
	    } else {
		language = LanguageSystem.getDefaultLanguage().getFullName();
	    }
	    cmde.reply(cmde.success().setTitle("Languages").addField("Guild Language: " + language, "To change the guild language, use \n`/" + this.getData().getName() + " " + SlashCommandExecutedEvent.getCommandHelp(guild, author, this) + "`\nTo get a list of all valid language codes use `" + guild.getPrefix() + "language list`", false));
	}
    }
}