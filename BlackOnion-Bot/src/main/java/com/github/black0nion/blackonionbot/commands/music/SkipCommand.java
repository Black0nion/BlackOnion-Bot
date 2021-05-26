package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SkipCommand extends Command {
	
	public SkipCommand() {
		this.setCommand("skip");
	}
	
	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getChannel());
			final AudioPlayer player = musicManager.audioPlayer;
			
			if (player.getPlayingTrack() == null) {
				cmde.error("wiat what", "why am i even in here? i should've gotten disconnected, lol");
				return;
			}
			
			musicManager.scheduler.nextTrack();
			cmde.success("songskipped", "songgotskipped", new Placeholder("song", musicManager.audioPlayer.getPlayingTrack().getInfo().title));
		} else {
			cmde.error("notconnected", "startmusictostop");
		}
	}
}