package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class GuildTypeCommand extends SlashCommand {

	public static final String GUILDTYPE = "guildtype";

	public GuildTypeCommand() {
		super(builder(
			Commands.slash("guildtype", "Set the type of a guild")
				.addOption(OptionType.STRING, "guildid", "The ID of the guild to get or set the type of", true)
				.addOptions(new OptionData(OptionType.STRING, GUILDTYPE, "The type of the guild to set")
					.addChoices(Arrays.stream(GuildType.values()).map(type -> new Command.Choice(type.name(), type.name())).toList()))
		)
			.setAdminGuild());
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		long guildID = Long.parseLong(Objects.requireNonNull(e.getOption("guildid", OptionMapping::getAsString)));
		final Guild mentionedGuild = e.getJDA().getGuildById(guildID);
		final @Nullable String newGuildType = e.getOption(GUILDTYPE, OptionMapping::getAsString);

		if (mentionedGuild == null) {
			cmde.send("thisguildnotfound");
			return;
		}

		if (newGuildType == null) {
			cmde.send("guildtypeis", new Placeholder("guild", Utils.escapeMarkdown(mentionedGuild.getName()) + " (" + mentionedGuild.getId() + ")"), new Placeholder("guildtype", guildSettings.getGuildType().getValue().name()));
			return;
		}

		final GuildType parsedGuildType = GuildType.parse(newGuildType);
		if (parsedGuildType == null) throw new IllegalArgumentException("Invalid guild type");

		guildSettings.getGuildType().setValue(parsedGuildType);
		cmde.send("guildtypesetto", new Placeholder("guild", Utils.escapeMarkdown(mentionedGuild.getName()) + " (" + mentionedGuild.getId() + ")"), new Placeholder("guildtype", parsedGuildType.name()));
	}
}
