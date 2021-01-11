package com.github.ahitm_2020_2025.blackonionbot.commands.fun;

import java.time.Instant;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AvatarCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		User mentionedUser = null;
		String user = String.join(" ", Utils.getStringFromList(args));
		if (!message.getMentionedUsers().isEmpty()) {
			mentionedUser = message.getMentionedUsers().get(0);
		} else if (!e.getGuild().getMembersByEffectiveName(user, true).isEmpty()) {
			mentionedUser = e.getGuild().getMembersByEffectiveName(user, true).get(0).getUser();
		} else if (!e.getGuild().getMembersByName(user, true).isEmpty()) {
			mentionedUser =  e.getGuild().getMembersByName(user, true).get(0).getUser();
		} else if(!e.getGuild().getMembersByNickname(user, true).isEmpty()) {
			mentionedUser = e.getGuild().getMembersByNickname(user, true).get(0).getUser();
		} else {
			channel.sendMessage("Bitte erwähne einen User oder gib den Namen eines Users ein [EXPERIMENTEL]!").queue();
			return;
		}
		EmbedBuilder builder = new EmbedBuilder()
		.setTitle("Profilbild von " + Utils.removeMarkdown(mentionedUser.getName()) + "#" + mentionedUser.getDiscriminator(), mentionedUser.getEffectiveAvatarUrl())
			.setImage(mentionedUser.getEffectiveAvatarUrl())
			.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
			.setTimestamp(Instant.now());
		channel.sendMessage(builder.build()).queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}

	@Override
	public String getSyntax() {
		return "<@User>";
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"avatar"};
	}
}
