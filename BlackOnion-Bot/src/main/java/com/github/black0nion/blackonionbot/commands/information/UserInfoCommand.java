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
		
		String[] flags = statsUser.getFlags().stream().map(entry -> entry.getName()).toArray(String[]::new);
		
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
	}
	
	@Override
	public Category getCategory() {
		return Category.INFORMATION;
	}

}
