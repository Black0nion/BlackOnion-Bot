package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "stop" };
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getTextChannel());
			musicManager.scheduler.player.stopTrack();
			musicManager.scheduler.queue.clear();
			
			channel.sendMessage("stopped lmao").queue();
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notconnected", "startmusictostop", false).build()).queue();
		}
	}

}
