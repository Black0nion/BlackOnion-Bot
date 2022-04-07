package com.github.black0nion.blackonionbot.commands.information;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UserInfoCommand extends SlashCommand {

	private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	public UserInfoCommand() {
		this.setCommand("userinfo").setSyntax("[@User | UserID]");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember eventMember, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		BlackMember statsMember;
		BlackUser statsUser;
		if (message.getMentionedMembers().size() > 0) {
			statsMember = BlackMember.from(message.getMentionedMembers().get(0));
			assert statsMember != null;
			statsUser = statsMember.getBlackUser();
			cmde.reply(getUserInfo(cmde, statsUser, statsMember));
		} else if (args.length >= 2) {
			try {
				Long.parseLong(args[1]);
			} catch (final Exception ex) {
				cmde.error("notfound", "usernotfound");
				return;
			}
			e.getJDA().retrieveUserById(args[1]).queue(idUser ->
					guild.retrieveMember(idUser).queue(
						member -> cmde.reply(getUserInfo(cmde, Objects.requireNonNull(BlackUser.from(idUser)), BlackMember.from(member, guild))),
						error -> cmde.reply(getUserInfo(cmde, Objects.requireNonNull(BlackUser.from(idUser)), null))), new ErrorHandler().handle(ErrorResponse.UNKNOWN_USER, error -> cmde.error("notfound", "usernotfound")).handle(Throwable.class, err -> cmde.exception()));
		} else {
			statsUser = author;
			statsMember = eventMember;
			cmde.reply(getUserInfo(cmde, statsUser, statsMember));
		}
	}

	private static EmbedBuilder getUserInfo(final CommandEvent cmde, final BlackUser statsUser, final BlackMember statsMember) {
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
}