package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType;
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

public class AntiSwearCommand extends SlashCommand {
	private static final String STATUS = "status";
	private static final String REPLACE = "replace";
	private static final String OFF = "off";
	private static final String DELETE = "delete";

	public AntiSwearCommand() {
		super(builder(Commands.slash("antiswear", "Used to enable/disable the anti-swear system.")
				.addOptions(new OptionData(OptionType.STRING, STATUS, "Used to change the status of the anti-swear system.", true)
						.addChoice("Replace", REPLACE)
						.addChoice("Off", OFF)
						.addChoice("Delete", DELETE)))
				.setRequiredPermissions(Permission.ADMINISTRATOR));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var status = e.getOption(STATUS, OptionMapping::getAsString);
		if(status.equals(REPLACE)) {
			guild.setAntiSwearType(AntiSwearType.REPLACE);
			cmde.success("antiswearstatuschanged", "antiswearis", new Placeholder("status", cmde.getTranslation(REPLACE)));
		} else if(status.equals(OFF)) {
			guild.setAntiSwearType(AntiSwearType.OFF);
			cmde.success("antiswearstatuschanged", "antiswearis", new Placeholder("status", cmde.getTranslation(OFF)));
		} else if(status.equals(DELETE)) {
			guild.setAntiSwearType(AntiSwearType.DELETE);
			cmde.success("antiswearstatuschanged", "antiswearis", new Placeholder("status", cmde.getTranslation(DELETE)));
		} else {
			cmde.sendPleaseUse();
		}
	}
}