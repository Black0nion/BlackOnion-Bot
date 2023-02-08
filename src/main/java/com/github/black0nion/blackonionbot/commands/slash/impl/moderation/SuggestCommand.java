package com.github.black0nion.blackonionbot.commands.slash.impl.moderation;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class SuggestCommand extends SlashCommand {

	private static final String SUGGESTION = "suggestion";
	private final LanguageSystem languageSystem;

	public SuggestCommand(LanguageSystem languageSystem) {
		super(builder(Commands.slash("suggest", "Used to send a suggestion.")
			.addOption(OptionType.STRING, SUGGESTION, "The suggestion to send", true)));
		this.languageSystem = languageSystem;
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		var suggestion = e.getOption(SUGGESTION, OptionMapping::getAsString);
		final long suggestionsChannelId = guild.getSuggestionsChannel();

		if (suggestionsChannelId == -1) {
			cmde.send("invalidsuggestionschannel");
		} else {
			final TextChannel suggestionsChannel = guild.getTextChannelById(suggestionsChannelId);
			if (suggestionsChannel == null) {
				cmde.send("invalidsuggestionschannel");
			} else if (!(guild.getSelfMember().hasPermission(suggestionsChannel, Permission.MESSAGE_SEND,
				Permission.MESSAGE_ADD_REACTION))) {
				e.replyEmbeds(Utils.noRights(languageSystem, guild, guild.getSelfBlackMember().getBlackUser(), Permission.MESSAGE_SEND,
					Permission.MESSAGE_ADD_REACTION)).setEphemeral(true).queue();
			} else {
				// all good, we can send the suggestion
				suggestionsChannel.sendMessageEmbeds(cmde.success().setTitle(SUGGESTION).setDescription(String.join(" ", suggestion)).build())
					.queue(msg -> {
						msg.addReaction(Emoji.fromUnicode("U+1F44D")).queue();
						msg.addReaction(Emoji.fromUnicode("U+1F44E")).queue();
					});
				cmde.send("suggestionsucess");
			}
		}
	}
}
