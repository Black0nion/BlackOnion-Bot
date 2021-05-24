package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BugReportCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "bug", "bugreport" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle("foundabug").addField("plsreportbug", LanguageSystem.getTranslation("inourgitrepo", author, guild).replace("%repo%", "[BlackOnion-Bugreports](https://github.com/Black0nion/BlackOnion-Bugreports)"), false).build()).queue();
	}

	@Override
	public Category getCategory() {
		return Category.BOT;
	}
}