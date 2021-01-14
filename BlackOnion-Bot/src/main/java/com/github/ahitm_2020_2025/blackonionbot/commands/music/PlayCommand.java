package com.github.ahitm_2020_2025.blackonionbot.commands.music;

import java.net.URI;
import java.net.URISyntaxException;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import com.github.ahitm_2020_2025.blackonionbot.lavaplayer.PlayerManager;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.Utils;

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
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		final GuildVoiceState selfVoiceState = e.getGuild().getSelfMember().getVoiceState();
		
		if (!selfVoiceState.inVoiceChannel()) {
			if (member.getVoiceState().inVoiceChannel()) {
				final AudioManager audioManager = e.getGuild().getAudioManager();
				final VoiceChannel memberChannel = member.getVoiceState().getChannel();
				
				audioManager.openAudioConnection(memberChannel);
			} else {
				channel.sendMessage("Du bist in keinem Voicechat!").queue();
				return;
			}
		} else if (member.getVoiceState().getChannel() == selfVoiceState.getChannel()) {
			
		} else {
			channel.sendMessage("Ich bin bereits in einem Voicechannel!").queue();
			return;
		}
		
		String link = String.join(" ", Utils.removeFirstArg(args));
		
		if (!isUrl(link)) {
			link = "ytsearch:" + link;
		}
		
		PlayerManager.getInstance().loadAndPlay(e.getTextChannel(), link);
	}
	
	private boolean isUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.MUSIC;
	}
	
	@Override
	public String getSyntax() {
		return "<Song Name / Link>";
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"play"};
	}
	
	@Override
	public Progress getProgress() {
		return Progress.PAUSED;
	}
}
