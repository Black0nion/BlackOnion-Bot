package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;

import com.github.black0nion.blackonionbot.blackobjects.BlackEmbed;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class UserInfoCommand extends Command {

    private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public UserInfoCommand() {
	this.setCommand("userinfo").setSyntax("[@User | UserID]");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	BlackMember statsMember = null;
	BlackUser statsUser = null;
	if (message.getMentionedMembers().size() > 0) {
	    statsMember = BlackMember.from(message.getMentionedMembers().get(0));
	    statsUser = statsMember.getBlackUser();
	    cmde.reply(getUserInfo(cmde, statsUser, statsMember));
	} else if (args.length >= 2) {
	    try {
		Long.parseLong(args[1]);
	    } catch (final Exception ex) {
		cmde.error("notfound", "usernotfound");
		return;
	    }
	    e.getJDA().retrieveUserById(args[1]).queue(idUser -> {
		guild.retrieveMember(idUser).queue(mem -> {
		    cmde.reply(getUserInfo(cmde, BlackUser.from(idUser), BlackMember.from(mem, guild)));
		    return;
		}, error -> {
		    cmde.reply(getUserInfo(cmde, BlackUser.from(idUser), null));
		    return;
		});
	    }, new ErrorHandler().handle(ErrorResponse.UNKNOWN_USER, errr -> cmde.error("notfound", "usernotfound")).handle(Throwable.class, err -> cmde.exception()));
	} else {
	    statsUser = author;
	    statsMember = member;
	    cmde.reply(getUserInfo(cmde, statsUser, statsMember));
	    return;
	}
    }

    private static final EmbedBuilder getUserInfo(final CommandEvent cmde, final BlackUser statsUser, final BlackMember statsMember) {
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