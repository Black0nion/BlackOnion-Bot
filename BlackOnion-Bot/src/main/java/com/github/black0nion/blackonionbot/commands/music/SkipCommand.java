package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "skip" };
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getTextChannel());
			final AudioPlayer player = musicManager.audioPlayer;
			
			if (player.getPlayingTrack() == null) {
				channel.sendMessage("nomusic").queue();
				return;
			}
			
			musicManager.scheduler.nextTrack();
			
			channel.sendMessage("skipped lmao").queue();
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notconnected", "startmusictostop", false).build()).queue();
		}
	}

}
