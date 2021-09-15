package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SkipCommand extends Command {

    public SkipCommand() {
	this.setCommand("skip");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final GuildVoiceState state = guild.getSelfMember().getVoiceState();
	if (state != null && state.getChannel() != null) {
	    final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getChannel());
	    final AudioPlayer player = musicManager.audioPlayer;

	    if (player.getPlayingTrack() == null) {
		cmde.error("wiat what", "why am i even in here? i should've gotten disconnected, lol");
		return;
	    }

	    final AudioTrackInfo previousTrack = musicManager.scheduler.player.getPlayingTrack().getInfo();
	    if (musicManager.scheduler.queue.peek() != null) {
		musicManager.scheduler.nextTrack();
		final AudioTrackInfo newTrack = musicManager.scheduler.player.getPlayingTrack().getInfo();
		cmde.success("songskipped", "songgotskipped",
			new Placeholder("oldsong", Utils.removeMarkdown(previousTrack.author + " - " + previousTrack.title)),
			new Placeholder("newsong", Utils.removeMarkdown(newTrack.author + " - " + newTrack.title)));
	    } else {
		musicManager.audioPlayer.stopTrack();
		guild.getAudioManager().closeAudioConnection();
		cmde.success("songskipped", "queueemptyskip");
	    }
	} else {
	    cmde.error("notconnected", "startmusictostop");
	}
    }
}