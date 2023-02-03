package com.github.black0nion.blackonionbot.commands.slash.impl.information;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;

public class UserInfoCommand extends SlashCommand {

	private static final String USER = "user";
	private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	public UserInfoCommand() {

		super(builder(Commands.slash("userinfo", "Used to get information about a member.")
			.addOption(OptionType.USER, USER, "The member to get information about.", false)));
	}

	static @NotNull EmbedBuilder getUserInfo(final @NotNull SlashCommandEvent cmde, final @NotNull User user, final @Nullable Member member) {
		var statsUser = BlackUser.from(user);
		var statsMember = member != null ? BlackMember.from(member) : null;

		final String avatarUrl = statsUser.getAvatarUrl();
		final String[] flags = statsUser.getFlags().stream().map(UserFlag::getName).toArray(String[]::new);
		final TranslatedEmbedBuilder builder = cmde.success()
			.setTitle("userinfo")
			.setThumbnail(avatarUrl != null ? avatarUrl : statsUser.getDefaultAvatarUrl())
			.addField("name", statsUser.getEscapedName(), true)
			.addField("discriminator", statsUser.getDiscriminator(), true)
			.addField("userid", statsUser.getId(), true)
			.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "empty"), false)
			.addField("language", statsUser.getLanguage() != null ? statsUser.getLanguage().getName() + " (" + statsUser.getLanguage().getLanguageCode() + ")" : "nopreference", true)
			.addField("created", statsUser.getTimeCreated().format(TIME_PATTERN), true)
			.addField("bot", statsUser.isBot() ? "yes" : "no", true);

		if (statsMember != null) {
			builder.addField("joined", statsMember.getTimeJoined().format(TIME_PATTERN), true);
			if (statsMember.getTimeBoosted() != null)
				builder.addField("boosted", statsMember.getTimeBoosted().format(TIME_PATTERN), true);
		}

		return builder;
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, UserSettings userSettings) {
		var givenUser = e.getOption(USER, OptionMapping::getAsUser);

		if (givenUser == null) {
			cmde.reply(getUserInfo(cmde, author, member));
		} else {
			guild.retrieveMember(givenUser).queue(loadedMember -> cmde.reply(getUserInfo(cmde, givenUser, loadedMember)), error -> cmde.reply(getUserInfo(cmde, givenUser, null)));
		}
	}
}
