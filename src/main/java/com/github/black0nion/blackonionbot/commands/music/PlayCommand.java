package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

public class PlayCommand extends SlashCommand {
	private static final String URL_OR_NAME = "url_or_name";
	public PlayCommand() {
		super(builder(Commands.slash("play", "Used to play a song from a url or a search query.")
				.addOption(OptionType.STRING, URL_OR_NAME, "The url or search query to play.", true))
				.setRequiredBotPermissions(Permission.VOICE_SPEAK));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var urlOrName = e.getOption(URL_OR_NAME, OptionMapping::getAsString);
		final GuildVoiceState state = member.getVoiceState();
		if (state != null && state.getChannel() != null) {
			if (!urlOrName.startsWith("http"))
				urlOrName = "ytsearch:" + urlOrName;
			final AudioManager audioManager = e.getGuild().getAudioManager();
			final AudioChannel memberChannel = member.getVoiceState().getChannel();

			audioManager.openAudioConnection(memberChannel);

			PlayerManager.getInstance().loadAndPlay(author, e.getTextChannel(), urlOrName, audioManager, memberChannel);
		} else
			cmde.error("notinvc", "goinvc");
	}
}