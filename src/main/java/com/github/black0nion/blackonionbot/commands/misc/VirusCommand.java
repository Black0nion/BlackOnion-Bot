package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VirusCommand extends TextCommand {

	public VirusCommand() {
		this.setCommand("viruscheck", "virus").setSyntax("<url / attachement>").setProgress(Progress.PAUSED);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
			final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild,
			final TextChannel channel) {
		cmde.reply(cmde.success().setTitle("VirusTotal", "https://www.virustotal.com/gui/home/upload")
				.addField("virustotalfieldtitle", "virustotalinfo", false));
	}
}
