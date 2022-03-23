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
			if (type.equalsIgnoreCase("replace")) {
				guild.setAntiSpoilerType(AntiSpoilerType.REPLACE);
				message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("remove"))), false).build()).queue();
			} else if (type.equalsIgnoreCase("delete")) {
				guild.setAntiSpoilerType(AntiSpoilerType.DELETE);
				message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("delete"))), false).build()).queue();
			} else if (type.equalsIgnoreCase("off")) {
				guild.setAntiSpoilerType(AntiSpoilerType.OFF);
				message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("off"))), false).build()).queue();
			} else {
				cmde.sendPleaseUse();
			}
		} else {
			message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("antispoilerstatus", author, guild).replace("%status%", LanguageSystem.getTranslation(guild.getAntiSpoilerType().name(), author, guild)), LanguageSystem.getTranslation("howtoantispoilertoggle", author, guild).replace("%command%", "``" + CommandEvent.getCommandHelp(guild, this) + "``"), false).build()).queue();
		}
	}
}