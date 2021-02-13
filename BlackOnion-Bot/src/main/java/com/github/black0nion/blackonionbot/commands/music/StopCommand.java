package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.music.MusicController;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class StopCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "stoplol" };
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			MusicController controller = MusicSystem.playerManager.getController(guild.getIdLong());
			AudioManager manager = guild.getAudioManager();
			AudioPlayer player = controller.getPlayer();
			
			player.stopTrack();
			manager.closeAudioConnection();
			message.addReaction("U+1F44B").queue();
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notconnected", "startmusictostop", false).build()).queue();
		}
	}

}
