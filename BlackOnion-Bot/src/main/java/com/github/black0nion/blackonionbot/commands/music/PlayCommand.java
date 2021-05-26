package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "play" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		GuildVoiceState state = member.getVoiceState();
		if (state != null && state.getChannel() != null) {
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) builder.append(args[i] + " ");
			
			String url = builder.toString().trim();
			if (!url.startsWith("http")) {
				url = "ytsearch:" + url;
			}
			
			final AudioManager audioManager = e.getGuild().getAudioManager();
			final VoiceChannel memberChannel = member.getVoiceState().getChannel();
			
			audioManager.openAudioConnection(memberChannel);
			
			PlayerManager.getInstance().loadAndPlay(author, e.getChannel(), url, audioManager, memberChannel);
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("notinvc", "goinvc", false).build()).queue();
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
	
	@Override
	public Permission[] getRequiredBotPermissions() {
		return new Permission[] { Permission.VOICE_SPEAK };
	}
}