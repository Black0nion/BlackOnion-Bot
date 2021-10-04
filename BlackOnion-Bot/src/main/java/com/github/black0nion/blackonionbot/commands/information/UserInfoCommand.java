package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackEmbed;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class UserInfoCommand extends SlashCommand {

    private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public UserInfoCommand() {
	this.setData(new CommandData("userinfo", "Get a info about a user").addOption(OptionType.USER, "user", "The user to the information of", false));
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final com.github.black0nion.blackonionbot.blackobjects.BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	BlackMember statsMember = null;
	BlackUser statsUser = null;
	final List<OptionMapping> user = e.getOptionsByType(OptionType.USER);
	if (!user.isEmpty() && user.get(0).getAsMember() != null) {
	    statsMember = BlackMember.from(user.get(0).getAsMember());
	    statsUser = statsMember.getBlackUser();
	    cmde.reply(getUserInfo(cmde, statsUser, statsMember));
	} else if (!user.isEmpty()) {
	    final User idUser = user.get(0).getAsUser();
	    guild.retrieveMember(idUser).queue(mem -> {
		cmde.reply(getUserInfo(cmde, BlackUser.from(idUser), BlackMember.from(mem, guild)));
		return;
	    }, error -> {
		cmde.reply(getUserInfo(cmde, BlackUser.from(idUser), null));
		return;
	    });
	} else {
	    statsUser = author;
	    statsMember = member;
	    cmde.reply(getUserInfo(cmde, statsUser, statsMember));
	    return;
	}
    }

    private static final EmbedBuilder getUserInfo(final SlashCommandExecutedEvent cmde, final BlackUser statsUser, final BlackMember statsMember) {
	final String[] flags = statsUser.getFlags().stream().map(UserFlag::getName).toArray(String[]::new);

	final BlackEmbed builder = cmde.success();
	builder.setTitle("userinfo");
	final String avatarUrl = statsUser.getAvatarUrl();
	builder.setThumbnail(avatarUrl != null ? avatarUrl : statsUser.getDefaultAvatarUrl());
	builder.addField("name", Utils.removeMarkdown(statsUser.getName()), true);
	builder.addField("discriminator", statsUser.getDiscriminator(), true);
	builder.addField("userid", statsUser.getId(), true);
	builder.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "empty"), false);
	builder.addField("language", statsUser.getLanguage().getName() + " (" + statsUser.getLanguage().getLanguageCode() + ")", true);
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