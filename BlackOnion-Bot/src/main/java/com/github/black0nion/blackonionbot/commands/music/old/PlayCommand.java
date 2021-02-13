package com.github.black0nion.blackonionbot.commands.music.old;

import java.net.URI;
import java.net.URISyntaxException;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.enums.Progress;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		channel.sendMessage("paused!").queue();
//		final GuildVoiceState selfVoiceState = e.getGuild().getSelfMember().getVoiceState();
//		
//		if (!selfVoiceState.inVoiceChannel()) {
//			if (member.getVoiceState().inVoiceChannel()) {
//				final AudioManager audioManager = e.getGuild().getAudioManager();
//				final VoiceChannel memberChannel = member.getVoiceState().getChannel();
//				
//				audioManager.openAudioConnection(memberChannel);
//			} else {
//				channel.sendMessage("Du bist in keinem Voicechat!").queue();
//				return;
//			}
//		} else if (member.getVoiceState().getChannel() == selfVoiceState.getChannel()) {
//			
//		} else {
//			channel.sendMessage("Ich bin bereits in einem Voicechannel!").queue();
//			return;
//		}
//		
//		String link = String.join(" ", Utils.removeFirstArg(args));
//		
//		if (!isUrl(link)) {
//			link = "ytsearch:" + link;
//		}
//		
//		PlayerManager.getInstance().loadAndPlay(e.getTextChannel(), link);
	}
	
	@SuppressWarnings("unused")
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
