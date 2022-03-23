package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class GuildTypeCommand extends SlashCommand {

	public GuildTypeCommand() {
		super(builder(
			Commands.slash("guildtype", "Set the type of a guild")
				.addOption(OptionType.STRING, "guildid", "The ID of the guild to get or set the type of", true)
				.addOptions(new OptionData(OptionType.STRING, "guildtype", "The type of the guild to set")
					.addChoices(Arrays.stream(GuildType.values()).map(type -> new Command.Choice(type.name(), type.name())).toList()))
			)
			.setAdminGuild());
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		long guildID = Utils.getOrReplaceMessage(() -> Long.parseLong(Objects.requireNonNull(e.getOption("guildid", OptionMapping::getAsString))), "Invalid guild ID");
		final Guild mentionedGuild = e.getJDA().getGuildById(guildID);
		final BlackGuild mentionedBlackGuild = BlackGuild.from(mentionedGuild);
		final @Nullable String newGuildType = e.getOption("guildtype", OptionMapping::getAsString);
		if (mentionedBlackGuild != null) {
			if (newGuildType != null) {
				final GuildType parsedGuildType = GuildType.parse(newGuildType);
				if (parsedGuildType != null) {
					mentionedBlackGuild.setGuildType(parsedGuildType);
					cmde.send("guildtypesetto", new Placeholder("guild", mentionedBlackGuild.getEscapedName() + " (" + mentionedBlackGuild.getId() + ")"), new Placeholder("guildtype", parsedGuildType.name()));
				} else throw new IllegalArgumentException("Invalid guild type");
			} else {
				cmde.send("guildtypeis", new Placeholder("guild", mentionedBlackGuild.getEscapedName() + " (" + mentionedBlackGuild.getId() + ")"), new Placeholder("guildtype", mentionedBlackGuild.getGuildType().name()));
			}
		} else {
			cmde.send("thisguildnotfound");
		}
	}
}