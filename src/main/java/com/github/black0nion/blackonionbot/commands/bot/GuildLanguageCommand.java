package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.Map;

public class GuildLanguageCommand extends SlashCommand {

	private static final OptionData languageOption = new OptionData(OptionType.STRING, "language", "The code of the language to set", true)
		.addChoices(LanguageSystem.getLanguages().values().stream().map(e -> new Command.Choice(e.getName(), e.getLanguageCode())).toList());

	public GuildLanguageCommand() {
		super(builder(Commands.slash("language", "Set the language of either the guild or yourself").addSubcommands(
			new SubcommandData("user", "Set the language of yourself").addOptions(languageOption),
			new SubcommandData("guild", "Set the language for the guild").addOptions(languageOption)
		)));
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		String subcommand;
		Checks.notNull(subcommand = e.getSubcommandName(), "Subcommand Name");
		Language lang = LanguageSystem.getLanguageFromName(e.getOption("languageOption", OptionMapping::getAsString));
		Checks.notNull(lang, "Language");
		if (subcommand.equalsIgnoreCase("user")) {

		} else if (subcommand.equalsIgnoreCase("guild")) {

		} else throw new NotImplementedException("Subcommand");
		if (args.length >= 2) {
			final Language newLanguage = LanguageSystem.getLanguageFromName(args[1].toUpperCase());
			if (newLanguage != null) {
				guild.setLanguage(newLanguage);
				cmde.success("languageupdated", "newlanguage", new Placeholder("newlang", newLanguage.getName() + " (" + newLanguage.getLanguageCode().toUpperCase() + ")"));
			} else {
				String validLanguages = "\n";
				for (final Map.Entry<String, Language> entry : LanguageSystem.getLanguages().entrySet()) {
				    validLanguages += entry.getValue().getName() + " (" + entry.getKey() + ")\n";
				}
				if (args[1].equalsIgnoreCase("list")) {
				    cmde.success("Languages", "Valid Languages:", validLanguages);
				} else {
				    cmde.error("Language doesn't exist!", "Valid Languages:", validLanguages);
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
			cmde.reply(cmde.success().setTitle("Languages").addField("Guild Language: " + language, "To change the guild language, use " + CommandEvent.getCommandHelp(guild, this) + "\nTo get a list of all valid language codes use `" + guild.getPrefix() + "language list`", false));
		}
	}
}