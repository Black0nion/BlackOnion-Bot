package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LanguageCommand extends SlashCommand {

	private static SubcommandData[] getSubcommands(LanguageSystem languageSystem) {
		return new SubcommandData[] {
			new SubcommandData("get", "Get the current language"),
			new SubcommandData("set", "Change the current language").addOptions(new OptionData(OptionType.STRING, "language", "The code of the language to set", true)
				.addChoices(languageSystem.getLanguages().values().stream().map(e -> new Command.Choice(e.getName(), e.getLanguageCode())).toList()))
		};
	}

	private final LanguageSystem languageSystem;

	public LanguageCommand(LanguageSystem languageSystem) {
		super(builder(Commands.slash("language", "Set the language of either the guild or yourself").addSubcommandGroups(
			new SubcommandGroupData("user", "Set the language of yourself")
				.addSubcommands(getSubcommands(languageSystem)),
			new SubcommandGroupData("guild", "Set the language for the guild")
				.addSubcommands(getSubcommands(languageSystem))
		).addSubcommands(new SubcommandData("list", "List all available languages"))));
		this.languageSystem = languageSystem;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, Member member, @NotNull User author, @NotNull Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		String subcommandGroup = null;
		String subcommand = cmde.getSubcommandName();
		Language lang = null;

		if (!subcommand.equalsIgnoreCase("list"))
			subcommandGroup = cmde.getSubcommandGroup();
		if (subcommand.equalsIgnoreCase("set"))
			Checks.notNull(lang = languageSystem.getLanguageFromCode(e.getOption("language", OptionMapping::getAsString)), "Language");

		if (subcommand.equalsIgnoreCase("list")) {
			cmde.send("languagelist", new Placeholder("langs", languageSystem.getLanguageString()));
		} else if (subcommandGroup.equalsIgnoreCase("user")) {
			if (subcommand.equalsIgnoreCase("get")) {
				cmde.send("currentlanguage", new Placeholder("language", Optional.ofNullable(userSettings.getLanguage().getValue())
					.map(Language::getFullName)
					.orElse(cmde.getTranslation("empty"))
				));
			} else if (subcommand.equalsIgnoreCase("set")) {
				// can't be null because of the checks above
				userSettings.getLanguage().setValue(lang);
				cmde.setLanguage(lang);
				cmde.send("languageupdated", new Placeholder("newlang", lang.getFullName()));
			} else throw new NotImplementedException("Subcommand");
		} else if (subcommandGroup.equalsIgnoreCase("guild")) {
			if (subcommand.equalsIgnoreCase("get")) {
				cmde.send("currentlanguage", new Placeholder("language", Optional.ofNullable(guildSettings.getLanguage().getValue())
					.map(Language::getFullName)
					.orElse(cmde.getTranslation("empty"))
				));
			} else if (subcommand.equalsIgnoreCase("set")) {
				cmde.handlePerms(Permission.MANAGE_SERVER);
				// can't be null because of the checks above
				guildSettings.getLanguage().setValue(lang);
				cmde.setLanguage(lang);
				cmde.send("languageupdated", new Placeholder("newlang", lang.getFullName()));
			} else throw new NotImplementedException("Subcommand");
		} else throw new NotImplementedException("Subcommand Group");
	}
}
