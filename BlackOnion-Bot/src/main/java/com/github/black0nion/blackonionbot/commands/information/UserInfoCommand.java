package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class UserInfoCommand extends Command {
	
	private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	public UserInfoCommand() {
		this.setCommand("userinfo")
			.setSyntax("[@User | UserID");
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		BlackMember statsMember = null;
		BlackUser statsUser = null;
		if (message.getMentionedBlackMembers().size() > 0) {
			statsMember = message.getMentionedBlackMembers().get(0);
			statsUser = statsMember.getBlackUser();
			cmde.reply(getUserInfo(cmde, statsUser, statsMember));
		} else {
			if (args.length >= 2) {
				try { Long.parseLong(args[1]); } catch (Exception ex) { cmde.error("notfound", "usernotfound"); return; }
				e.getJDA().retrieveUserById(args[1]).queue(idUser -> {
					guild.retrieveMember(idUser).queue(mem -> {
						cmde.reply(getUserInfo(cmde, BlackUser.from(idUser), BlackMember.from(mem)));
						return;
					}, (error) -> {
						cmde.reply(getUserInfo(cmde, BlackUser.from(idUser), null));
						return;
					});
				}, new ErrorHandler()
						.handle(ErrorResponse.UNKNOWN_USER, (errr) -> cmde.error("notfound", "usernotfound"))
						.handle(Throwable.class, (err) -> cmde.exception())
				);
			} else {
				statsUser = author;
				statsMember = member;
				cmde.reply(getUserInfo(cmde, statsUser, statsMember));
				return;
			}
		}
	}
	
	private static final EmbedBuilder getUserInfo(CommandEvent cmde, BlackUser statsUser, BlackMember statsMember) {
		String[] flags = statsUser.getFlags().stream().map(entry -> entry.getName()).toArray(String[]::new);
		
		EmbedBuilder builder = cmde.success();
		builder.setTitle("userinfo");
		builder.setThumbnail(statsUser.getAvatarUrl());
		builder.addField("name", Utils.removeMarkdown(statsUser.getName()), true);
		builder.addField("discriminator", statsUser.getDiscriminator(), true);
		builder.addField("userid", statsUser.getId(), true);
		builder.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "empty"), false);
		builder.addField("language", statsUser.getLanguage().getName() + " (" + statsUser.getLanguage() + ")", true);
		builder.addField("created", statsUser.getTimeCreated().format(pattern), true);
		if (statsMember != null)
			builder.addField("joined", statsMember.getTimeJoined().format(pattern), true);
		if (statsMember != null && statsMember.getTimeBoosted() != null)
			builder.addField("boosted", statsMember.getTimeBoosted().format(pattern), true);
		return builder;
	}
}