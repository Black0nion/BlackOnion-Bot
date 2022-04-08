package com.github.black0nion.blackonionbot.commands.information;


import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
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

	 static EmbedBuilder getUserInfo(final SlashCommandEvent cmde, final BlackUser statsUser, final BlackMember statsMember) {
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