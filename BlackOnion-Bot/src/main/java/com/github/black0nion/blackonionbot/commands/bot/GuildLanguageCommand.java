package com.github.black0nion.blackonionbot.commands.bot;

import java.util.Map;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildLanguageCommand extends Command {
	
	public GuildLanguageCommand() {
		this.setCommand("guildlanguage", "guildlang", "guildlocale", "guildsprache", "serverlang", "serverlanguage", "serersprache")
			.setRequiredPermissions(Permission.ADMINISTRATOR)
			.setSyntax("[language code]");
	}

	@Override
	public String[] getCommand() {
		return new String[] { "guildlanguage", "guildlang", "guildlocale", "guildsprache", "serverlang", "serverlanguage", "serversprache" };
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (args.length >= 2) {
			final Language newLanguage = LanguageSystem.getLanguageFromName(args[1].toUpperCase());
			if (newLanguage != null) {
				guild.setLanguage(newLanguage);
				cmde.success("languageupdated", "newlanguage", new Placeholder("newlang", newLanguage.getName() + " (" + newLanguage.getLanguageCode().toUpperCase() + ")"));
			} else {
				String validLanguages = "\n";
				for (final Map.Entry<String, Language> entry : LanguageSystem.getLanguages().entrySet())
					validLanguages += entry.getValue().getName() + " (" + entry.getKey() + ")\n";
				if (args[1].equalsIgnoreCase("list"))
					cmde.success("Languages", "Valid Languages:", validLanguages);
				else
					cmde.error("Language doesn't exist!", "Valid Languages:", validLanguages);
			}
		} else {
			String language = "";
			final Language guildLanguage = guild.getLanguage();
			if (guildLanguage != null)
				language = guildLanguage.getName() + " (" + guildLanguage.getLanguageCode() + ")";
			else
				language = LanguageSystem.getDefaultLanguage().getName() + " (" + LanguageSystem.getDefaultLanguage().getLanguageCode() + ")";
			cmde.reply(cmde.success().setTitle("Languages").addField("Guild Language: " + language, "To change the guild language, use " + CommandEvent.getCommandHelp(guild, author, this) + "\nTo get a list of all valid language codes use `" + guild.getPrefix() + "language list`", false));
		}
	}
}