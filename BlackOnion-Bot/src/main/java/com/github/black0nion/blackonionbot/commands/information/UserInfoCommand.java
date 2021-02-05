package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UserInfoCommand implements Command {
	
	private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	@Override
	public String[] getCommand() {
		return new String[] { "userinfo" };
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		Member statsMember = null;
		User statsUser = null;
		if (message.getMentionedMembers().size() > 0) {
			statsMember = message.getMentionedMembers().get(0);
			statsUser = statsMember.getUser();
		} else {
			if (args.length >= 2) {
				User idUser = e.getJDA().retrieveUserById(args[1]).submit().join();
			
				if (idUser != null) {
					statsUser = idUser;
					try { statsMember = guild.retrieveMember(idUser).submit().join(); } catch (Exception ignored) {}
				} else {
					statsUser = author;
					statsMember = member;
				}
			} else {
				statsUser = author;
				statsMember = member;
			}
		}
		
		final List<UserFlag> flagsList = statsUser.getFlags().stream().collect(Collectors.toList());
		final int flagCount = flagsList.size();
		String[] flags = new String[flagCount];
		
		for (int i = 0; i < flagCount; i++) {
			flags[i] = flagsList.get(i).getName();
		}
		
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild);
		builder.setTitle("userinfo");
		builder.setThumbnail(statsUser.getAvatarUrl());
		builder.addField("name", Utils.removeMarkdown(statsUser.getName()), true);
		builder.addField("discriminator", statsUser.getDiscriminator(), true);
		builder.addField("userid", statsUser.getId(), true);
		builder.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "NONE"), false);
		builder.addField("language", LanguageSystem.getLanguage(author, guild).getName() + " (" + LanguageSystem.getLanguage(author, guild).getLanguageCode() + ")", true);
		builder.addField("created", statsUser.getTimeCreated().format(pattern), true);
		if (statsMember != null)
			builder.addField("joined", statsMember.getTimeJoined().format(pattern), true);
		if (statsMember != null && statsMember.getTimeBoosted() != null)
			builder.addField("boosted", statsMember.getTimeBoosted().format(pattern), true);
		
		channel.sendMessage(builder.build()).queue();
		
//		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild)
//				.setTitle(LanguageSystem.getTranslatedString("userinfo", author, guild))
//				.setThumbnail(statsUser.getAvatarUrl())
//				.addField(LanguageSystem.getTranslatedString("name", author, guild), Utils.removeMarkdown(statsUser.getName()), true)
//				.addField(LanguageSystem.getTranslatedString("discriminator", author, guild), statsUser.getDiscriminator(), true)
//				.addField(LanguageSystem.getTranslatedString("userid", author, guild), statsUser.getId(), true)
//				.addField(LanguageSystem.getTranslatedString("badges", author, guild), (flags.length != 0 ? String.join("\n", flags) : "NONE"), false)
//				.addField(LanguageSystem.getTranslatedString("language", author, guild), LanguageSystem.getLanguage(author, guild).getName() + " (" + LanguageSystem.getLanguage(author, guild).getLanguageCode() + ")", true)
//				.addField(LanguageSystem.getTranslatedString("created", author, guild), statsUser.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), true)
//				.build()).queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.INFORMATION;
	}

}
