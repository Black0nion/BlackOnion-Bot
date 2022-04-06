package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.regex.Pattern;

public class PrefixCommand extends SlashCommand {

	private static final Pattern PREFIX_PATTERN = Pattern
			.compile("^[a-zA-Z?:()/&%$§!*;.,-_+#'~|><°^={}\\[\\]´`]{1,10}$");

	public PrefixCommand() {
		super(builder(Commands.slash("prefix", "Change the prefix of the bot for this guild")
				.addOption(OptionType.STRING, "prefix", "The prefix to set")).notToggleable()
						.setRequiredPermissions(Permission.MANAGE_SERVER));
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author,
			BlackGuild guild, TextChannel channel) {
		String prefix = e.getOption("prefix", OptionMapping::getAsString);
		if (prefix == null) {
			cmde.send("myprefixis", new Placeholder("prefix", Utils.escapeMarkdown(guild.getPrefix())));
		} else {
			if (PREFIX_PATTERN.matcher(prefix).matches()) {
				guild.setPrefix(prefix);
				cmde.send("prefixchanged", new Placeholder("prefix", Utils.escapeMarkdown(prefix)));
			} else {
				cmde.send("prefixinvalid",
						new Placeholder("format", PREFIX_PATTERN.pattern().replaceAll("[_*~`]", "\\$0")));
			}
		}
	}
}
