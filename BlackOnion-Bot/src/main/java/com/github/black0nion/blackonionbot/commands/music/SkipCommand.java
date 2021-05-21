package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SkipCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "skip" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getChannel());
			final AudioPlayer player = musicManager.audioPlayer;
			
			if (player.getPlayingTrack() == null) {
				message.reply("nomusic").queue();
				return;
			}
			
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("songskipped", LanguageSystem.getTranslatedString("songgotskipped", author, guild).replace("%song%", musicManager.audioPlayer.getPlayingTrack().getInfo().title), false).build()).queue();
			musicManager.scheduler.nextTrack();
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("notconnected", "startmusictostop", false).build()).queue();
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.MUSIC;
	}
}