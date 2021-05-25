package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class UserInfoCommand implements Command {
	
	private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	@Override
	public String[] getCommand() {
		return new String[] { "userinfo" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		BlackMember statsMember = null;
		BlackUser statsUser = null;
		if (message.getMentionedBlackMembers().size() > 0) {
			statsMember = message.getMentionedBlackMembers().get(0);
			statsUser = statsMember.getBlackUser();
			message.reply(getUserInfo(author, member, statsUser, statsMember).build()).queue();
		} else {
			if (args.length >= 2) {
				try { Long.parseLong(args[1]); } catch (Exception ex) { message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("notfound", "usernotfound", false).build()).queue(); return; }
				e.getJDA().retrieveUserById(args[1]).queue(idUser -> {
					guild.retrieveMember(idUser).queue(mem -> {
						message.reply(getUserInfo(author, member, BlackUser.from(idUser), BlackMember.from(mem)).build()).queue();
						return;
					}, (error) -> {
						message.reply(getUserInfo(author, member, BlackUser.from(idUser), null).build()).queue();
						return;
					});
				}, new ErrorHandler()
						.handle(ErrorResponse.UNKNOWN_USER, (errr) -> message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("notfound", "usernotfound", false).build()).queue())
						.handle(Throwable.class, (err) -> message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue())
				);
			} else {
				statsUser = author;
				statsMember = member;
				message.reply(getUserInfo(author, member, statsUser, statsMember).build()).queue();
				return;
			}
		}
	}
	
	private static final EmbedBuilder getUserInfo(BlackUser author, BlackMember member, BlackUser statsUser, BlackMember statsMember) {
		String[] flags = statsUser.getFlags().stream().map(entry -> entry.getName()).toArray(String[]::new);
		final BlackGuild guild = member.getBlackGuild();
		
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild);
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
	
	@Override
	public Category getCategory() {
		return Category.INFORMATION;
	}
}