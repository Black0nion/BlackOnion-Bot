package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.systems.music.AudioLoadResult;
import com.github.black0nion.blackonionbot.systems.music.MusicController;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "playlol" };
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		GuildVoiceState state = member.getVoiceState();
		VoiceChannel vc;
		if (state != null && (vc = state.getChannel()) != null) {
			MusicController controller = MusicSystem.playerManager.getController(guild.getIdLong());
			AudioPlayerManager apm = MusicSystem.audioPlayerManager;
			AudioManager manager = guild.getAudioManager();
			manager.openAudioConnection(vc);
			
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) builder.append(args[i] + " ");
			
			String url = builder.toString().trim();
			if (!url.startsWith("http")) {
				url = "ytsearch:" + url;
			}
			System.out.println(url);
			apm.loadItem(url, new AudioLoadResult(controller, url));
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notinvc", "goinvc", false).build()).queue();
		}
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}

	@Override
	public Category getCategory() {
		return Category.MUSIC;
	}
	
	@Override
	public String getSyntax() {
		return "<url / search term>";
	}
}
