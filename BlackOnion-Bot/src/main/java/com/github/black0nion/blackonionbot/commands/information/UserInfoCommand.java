package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
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
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		Member statsMember = null;
		User statsUser = null;
		if (message.getMentionedMembers().size() > 0) {
			statsMember = message.getMentionedMembers().get(0);
			statsUser = statsMember.getUser();
			channel.sendMessage(getUserInfo(author, member, statsUser, statsMember).build()).queue();
		} else {
			if (args.length >= 2) {
				try { Long.parseLong(args[1]); } catch (Exception ex) { channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notfound", "usernotfound", false).build()).queue(); return; }
				e.getJDA().retrieveUserById(args[1]).queue(idUser -> {
					guild.retrieveMember(idUser).queue(mem -> {
						channel.sendMessage(getUserInfo(author, member, idUser, mem).build()).queue();
						return;
					}, (error) -> {
						channel.sendMessage(getUserInfo(author, member, idUser, null).build()).queue();
						return;
					});
				}, new ErrorHandler()
						.handle(ErrorResponse.UNKNOWN_USER, (errr) -> channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notfound", "usernotfound", false).build()).queue())
						.handle(Throwable.class, (err) -> channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue())
				);
			} else {
				statsUser = author;
				statsMember = member;
				channel.sendMessage(getUserInfo(author, member, statsUser, statsMember).build()).queue();
				return;
			}
		}
	}
	
	private static final EmbedBuilder getUserInfo(User author, Member member, User statsUser, Member statsMember) {
		String[] flags = statsUser.getFlags().stream().map(entry -> entry.getName()).toArray(String[]::new);
		final Guild guild = member.getGuild();
		
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild);
		builder.setTitle("userinfo");
		builder.setThumbnail(statsUser.getAvatarUrl());
		builder.addField("name", Utils.removeMarkdown(statsUser.getName()), true);
		builder.addField("discriminator", statsUser.getDiscriminator(), true);
		builder.addField("userid", statsUser.getId(), true);
		builder.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "empty"), false);
		builder.addField("language", LanguageSystem.getLanguage(author, guild).getName() + " (" + LanguageSystem.getLanguage(author, guild).getLanguageCode() + ")", true);
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
