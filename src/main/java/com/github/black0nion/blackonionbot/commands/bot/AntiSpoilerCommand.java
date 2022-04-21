package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerType;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class AntiSpoilerCommand extends SlashCommand {
	private static final String STATUS = "status";

	public AntiSpoilerCommand() {
		super(builder(Commands.slash("antispoiler", "Used to deleted/disable/replace the anti-spoiler system.")
				.addOptions(new OptionData(OptionType.STRING, STATUS, "The status", true)
						.addChoice("replace", "replace")
						.addChoice("delete", "delete")
						.addChoice("off", "off")))
				.setRequiredPermissions(Permission.MESSAGE_MANAGE));
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var status = e.getOption(STATUS, OptionMapping::getAsString);

		if (status.equalsIgnoreCase("replace")) {
			guild.setAntiSpoilerType(AntiSpoilerType.REPLACE);
			e.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("remove"))), false).build()).queue();
		} else if (status.equalsIgnoreCase("delete")) {
			guild.setAntiSpoilerType(AntiSpoilerType.DELETE);
			e.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("delete"))), false).build()).queue();
		} else if (status.equalsIgnoreCase("off")) {
			guild.setAntiSpoilerType(AntiSpoilerType.OFF);
			e.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("antispoilerstatuschanged", cmde.getTranslation("antispoileris", new Placeholder("status", cmde.getTranslation("off"))), false).build()).queue();
		} else {
			cmde.sendPleaseUse();
		}
	}
}