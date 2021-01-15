package com.github.black0nion.blackonionbot.systems;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HandRaiseSystem extends ListenerAdapter {
	
	private static String guildID = "756810598158696458";
	public static String channelID = "788776045188612146";
	public static String messageID = "788776201930014740";
	
	public static HashMap<String, String> nicked = new HashMap<>();
	
	@Override
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent e) {
		if (nicked.containsKey(e.getUser().getId())) {
			e.getMember().modifyNickname("! 🙋 |  " + nicked.get(e.getUser().getId())).complete();
			e.getUser().openPrivateChannel().queue((channel) ->
	        {
	            channel.sendMessage("Wenn du deinen Namen ändern möchtest, bitte nimm zuerst deine Hand herunter!").queue();
	        });
		}
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		Guild guild = e.getGuild();
		TextChannel channel = e.getChannel();
		String message = e.getMessageId();
		
		if (guild.getId().equals(guildID) && channel.getId().equals(channelID) && message.equals(messageID) && !e.getMember().isOwner() && !e.getMember().getId().equals(e.getJDA().getSelfUser().getId())) {
			nicked.put(e.getUserId(), e.getMember().getNickname());
			e.getMember().modifyNickname("! 🙋 |  " + e.getMember().getNickname()).complete();
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		Guild guild = e.getGuild();
		TextChannel channel = e.getChannel();
		String message = e.getMessageId();
		
		if (guild.getId().equals(guildID) && channel.getId().equals(channelID) && message.equals(messageID) && nicked.containsKey(e.getUserId())) {
			e.getMember().modifyNickname(nicked.get(e.getUserId())).complete();
			nicked.remove(e.getUserId());
		}
	}
}
