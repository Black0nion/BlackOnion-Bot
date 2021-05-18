package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "play" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
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
			
			if (!guild.getSelfMember().hasPermission(memberChannel, Permission.VOICE_SPEAK)) {
				Utils.noRights(guild, author, Permission.VOICE_CONNECT);
				return;
			}
			
			PlayerManager.getInstance().loadAndPlay(author, e.getChannel(), url, audioManager, memberChannel);
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