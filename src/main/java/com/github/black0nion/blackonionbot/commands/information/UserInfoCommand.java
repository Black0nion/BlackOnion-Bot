package com.github.black0nion.blackonionbot.commands.information;


import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UserInfoCommand extends SlashCommand {
	private static final String USER = "user";
	private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	public UserInfoCommand() {

		super(builder(Commands.slash("userinfo", "Used to get information about a member.")
				.addOption(OptionType.USER,USER, "The member to get information about.", false)));
	}

	 static @NotNull EmbedBuilder getUserInfo(final @NotNull SlashCommandEvent cmde, final User user, final Member member) {
		var statsUser = BlackUser.from(user);
		var statsMember = BlackMember.from(member);
		final String[] flags = statsUser.getFlags().stream().map(UserFlag::getName).toArray(String[]::new);

		final TranslatedEmbed builder = cmde.success();
		builder.setTitle("userinfo");
		final String avatarUrl = statsUser.getAvatarUrl();
		builder.setThumbnail(avatarUrl != null ? avatarUrl : statsUser.getDefaultAvatarUrl());
		builder.addField("name", statsUser.getEscapedName(), true);
		builder.addField("discriminator", statsUser.getDiscriminator(), true);
		builder.addField("userid", statsUser.getId(), true);
		builder.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "empty"), false);
		builder.addField("language", statsUser.getLanguage() != null ? statsUser.getLanguage().getName() + " (" + statsUser.getLanguage().getLanguageCode() + ")" : "nopreference", true);
		builder.addField("created", statsUser.getTimeCreated().format(pattern), true);
		if (statsMember != null) {
			builder.addField("joined", statsMember.getTimeJoined().format(pattern), true);
		}
		if (statsMember != null && statsMember.getTimeBoosted() != null) {
			builder.addField("boosted", statsMember.getTimeBoosted().format(pattern), true);
		}
		return builder;
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var givenMember = e.getOption(USER, OptionMapping::getAsMember);

		getUserInfo(cmde, Objects.requireNonNullElseGet(givenMember.getUser(),
				() -> Objects.requireNonNull(e.getMember().getUser())),
				Objects.requireNonNullElseGet(givenMember,
						() -> Objects.requireNonNull(e.getMember())));
	}
}