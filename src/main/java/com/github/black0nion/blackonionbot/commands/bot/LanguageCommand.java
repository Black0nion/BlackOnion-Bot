package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.Optional;

public class LanguageCommand extends SlashCommand {

  private static final OptionData languageOptions =
      new OptionData(OptionType.STRING, "language", "The code of the language to set", true)
          .addChoices(LanguageSystem.getLanguages().values().stream()
              .map(e -> new Command.Choice(e.getName(), e.getLanguageCode())).toList());

  private static final SubcommandData[] subcommands =
      {new SubcommandData("get", "Get the current language"),
          new SubcommandData("set", "Change the current language").addOptions(languageOptions)};

  public LanguageCommand() {
    super(builder(Commands.slash("language", "Set the language of either the guild or yourself")
        .addSubcommandGroups(
            new SubcommandGroupData("user", "Set the language of yourself")
                .addSubcommands(subcommands),
            new SubcommandGroupData("guild", "Set the language for the guild")
                .addSubcommands(subcommands))
        .addSubcommands(new SubcommandData("list", "List all available languages"))));
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member,
      BlackUser author, BlackGuild guild, TextChannel channel) {
    String subcommandGroup = null;
    String subcommand;
    Language lang = null;
    Checks.notNull(subcommand = e.getSubcommandName(), "Subcommand Name");
    if (!subcommand.equalsIgnoreCase("list"))
      Checks.notNull(subcommandGroup = e.getSubcommandGroup(), "Subcommand Group");
    if (subcommand.equalsIgnoreCase("set"))
      Checks.notNull(
          lang = LanguageSystem
              .getLanguageFromName(e.getOption("language", OptionMapping::getAsString)),
          "Language");

    if (subcommand.equalsIgnoreCase("list")) {
      cmde.send("languagelist", new Placeholder("langs", LanguageSystem.getLanguageString()));
    } else if (subcommandGroup.equalsIgnoreCase("user")) {
      if (subcommand.equalsIgnoreCase("get")) {
        cmde.send("currentlanguage",
            new Placeholder("language", Optional.ofNullable(author.getLanguage())
                .orElse(LanguageSystem.getDefaultLanguage()).getFullName()));
      } else if (subcommand.equalsIgnoreCase("set")) {
        // can't be null because of the checks above
        author.setLanguage(lang);
        cmde.setLanguage(lang);
        cmde.send("languageupdated", new Placeholder("newlang", lang.getFullName()));
      } else
        throw new NotImplementedException("Subcommand");
    } else if (subcommandGroup.equalsIgnoreCase("guild")) {
      if (subcommand.equalsIgnoreCase("get")) {
        cmde.send("currentlanguage",
            new Placeholder("language", Optional.ofNullable(guild.getLanguage())
                .orElseGet(LanguageSystem::getDefaultLanguage).getFullName()));
      } else if (subcommand.equalsIgnoreCase("set")) {
        cmde.handlePerms(Permission.MANAGE_SERVER);
        // can't be null because of the checks above
        guild.setLanguage(lang);
        cmde.setLanguage(lang);
        cmde.send("languageupdated", new Placeholder("newlang", lang.getFullName()));
      } else
        throw new NotImplementedException("Subcommand");
    } else
      throw new NotImplementedException("Subcommand Group");
  }
}
