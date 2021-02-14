package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.Guild;
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
		AudioPlayer player = MusicSystem.playerManager.getController(guild.getIdLong()).getPlayer();
		
		if (player.getPlayingTrack() == null) {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("NOt playing bla", "blabla", false).build()).queue();
		}
	}

}
