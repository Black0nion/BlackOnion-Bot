package com.github.black0nion.blackonionbot.commands.slash.impl.information;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class GuildInfoCommand extends SlashCommand {

	public GuildInfoCommand() {
		super(builder(Commands.slash("guildinfo", "Provides information about the current guild.")));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, @NotNull BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		Language language = guildSettings.getLanguage().getValue();
		cmde.reply(cmde.success().setTitle("guildinfo")
			.setThumbnail(guild.getIconUrl())
			.addField("name", Utils.escapeMarkdown(guild.getName()), true)
			.addField("language", language != null ? (language.getName() + " (" + language.getLanguageCode() + ")") : "none", true)
			.addField("owner", guild.retrieveOwner().submit().join().getUser().getAsMention(), true)
			.addField("serverid", guild.getId(), true)
			.addField("rolecount", String.valueOf(guild.getRoles().size()), true)
			.addField("membercount", String.valueOf(guild.getMemberCount()), true)
			.addField("channelcount", String.valueOf(guild.getChannels().size()), true)
			.addField("boostlevel", guild.getBoostTier().name(), true)
			.addField("created", guild.getTimeCreated().format(BotInformation.DATE_TIME_FORMATTER), true));
	}
}
