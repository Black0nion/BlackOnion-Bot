package com.github.black0nion.blackonionbot.systems;

import java.util.HashMap;

import com.vdurmont.emoji.EmojiParser;

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
			e.getMember().modifyNickname(e.getOldNickname()).queue();
			e.getUser().openPrivateChannel().queue((channel) -> {
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
			final String previousNick = e.getMember().getNickname();
			final String newNick = "!" + EmojiParser.parseToUnicode(":raising_hand:") + " | " + previousNick;
			e.getMember().modifyNickname(newNick.substring(0, newNick.length() > 32 ? 32 : newNick.length())).queue(nothing -> {
				nicked.put(e.getUserId(), previousNick);
			});
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		Guild guild = e.getGuild();
		TextChannel channel = e.getChannel();
		String message = e.getMessageId();
		
		if (guild.getId().equals(guildID) && channel.getId().equals(channelID) && message.equals(messageID) && nicked.containsKey(e.getUserId())) {
			final String previousName = nicked.get(e.getUserId());
			nicked.remove(e.getUserId());
			e.getMember().modifyNickname(previousName).queue();
		}
	}
}
