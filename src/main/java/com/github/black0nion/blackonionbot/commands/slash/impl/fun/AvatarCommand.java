package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		var user = e.getOption(USER, OptionMapping::getAsUser);
		if (user == null)
			user = author;

		cmde.reply(cmde.success()
			.setTitle(cmde.getTranslation("pfpof", new Placeholder("user", "`" + Utils.escapeMarkdown(user.getAsTag()) + "`")), user.getEffectiveAvatarUrl())
			.setImage(user.getEffectiveAvatarUrl() + "?size=2048")
			.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl()));
	}
}
