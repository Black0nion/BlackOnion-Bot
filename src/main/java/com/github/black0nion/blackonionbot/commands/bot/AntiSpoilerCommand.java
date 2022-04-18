package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class AntiSpoilerCommand extends TextCommand {

	public AntiSpoilerCommand() {
		this.setCommand("antispoiler", "as")
			.setSyntax("[replace | delete | off]")
			.setRequiredPermissions(Permission.MESSAGE_MANAGE);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (Utils.handleRights(guild, author, null, Permission.MESSAGE_MANAGE)) return;
		if (args.length >= 2) {
			final String type = args[1];
			AntiSpoilerType newType = AntiSpoilerType.parse(type);
			if (newType == null) {
				cmde.sendPleaseUse();
				return;
			}
			guild.getAntiSpoilerType().setValue(newType);
			cmde.success("antispoilerstatuschanged", "antispoileris", new Placeholder("status", switch (newType) {
				case REPLACE -> "remove";
				case DELETE -> "delete";
				case OFF -> "off";
			}));
		} else {
			// never happens because the setting is non-null
			assert guild.getAntiSpoilerType().getValue() != null;
			cmde.success("antispoilerstatus", "howtoantispoilertoggle", new Placeholder("status", switch (guild.getAntiSpoilerType().getValue()) {
				case REPLACE -> "remove";
				case DELETE -> "delete";
				case OFF -> "off";
			}), new Placeholder("command", "``" + CommandEvent.getCommandHelp(guild, this) + "``"));
		}
	}
}