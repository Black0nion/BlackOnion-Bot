package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class AvatarCommand extends SlashCommand {
	private static final String USER = "user";

	public AvatarCommand() {
		super(builder(Commands.slash("avatar", "Used to get the avatar of a user.")
			.addOption(OptionType.USER, USER, "The user to get the avatar of.")));
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember eventMember, BlackUser author, BlackGuild guild, TextChannel channel) {
		var user = e.getOption(USER, OptionMapping::getAsMember);
		if (user == null)
			user = eventMember;

		cmde.reply(cmde.success()
			.setTitle(cmde.getTranslation("pfpof") + " " + user.getEffectiveName(), user.getEffectiveAvatarUrl())
			.setImage(user.getEffectiveAvatarUrl() + "?size=2048")
			.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl()));
	}
}