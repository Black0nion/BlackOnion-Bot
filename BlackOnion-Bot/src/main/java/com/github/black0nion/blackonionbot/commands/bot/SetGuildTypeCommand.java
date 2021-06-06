/**
 * 
 */
package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 */
public class SetGuildTypeCommand extends Command {

	public SetGuildTypeCommand() {
		this.setCommand("setguildtype")
			.setSyntax("<guildid> <guildtype>")
			.setRequiredArgumentCount(2)
			.botAdminRequired();
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (!Utils.isLong(args[1])) {
			cmde.error("notanumber", "inputnumber");
		} else {
			Guild mentionedGuild = e.getJDA().getGuildById(Long.parseLong(args[1]));
			final BlackGuild mentionedBlackGuild = BlackGuild.from(mentionedGuild);
			if (mentionedBlackGuild != null) {
				final GuildType parsedGuildType = GuildType.parse(args[2].toUpperCase());
				if (parsedGuildType != null) {
					mentionedBlackGuild.setGuildType(parsedGuildType);
					cmde.success("guildtypeset", "guildtypesetto", new Placeholder("guild", mentionedBlackGuild.getName() + " (G:" + mentionedBlackGuild.getId() + ")"), new Placeholder("guildtype", parsedGuildType.name()));
				} else {
					String validGuildTypes = "";
					for (GuildType type : GuildType.values()) {
						validGuildTypes += "\n- " + type.name();
					}
					cmde.error("guildtypenotfound", "validguildtypes", new Placeholder("guildtypes", "```" + validGuildTypes + "```"));
				}
			} else {
				cmde.error("notfound", "thisguildnotfound");
			}
		}
	}
}
