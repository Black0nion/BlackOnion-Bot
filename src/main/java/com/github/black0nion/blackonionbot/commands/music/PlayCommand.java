package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import net.dv8tion.jda.api.entities.*;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand extends SlashCommand {

	public PlayCommand() {
		this.setCommand("play")
			.setSyntax("<url / search term>")
			.setRequiredArgumentCount(1)
			.setRequiredBotPermissions(Permission.VOICE_SPEAK);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final GuildVoiceState state = member.getVoiceState();
		if (state != null && state.getChannel() != null) {
			final StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) builder.append(args[i]).append(" ");

			String url = builder.toString().trim();
			if (!url.startsWith("http"))
				url = "ytsearch:" + url;

			final AudioManager audioManager = e.getGuild().getAudioManager();
			final AudioChannel memberChannel = member.getVoiceState().getChannel();

			audioManager.openAudioConnection(memberChannel);

			PlayerManager.getInstance().loadAndPlay(author, e.getTextChannel(), url, audioManager, memberChannel);
		} else
			cmde.error("notinvc", "goinvc");
	}
}