package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;

import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnbanCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"unban", "unyeet"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() == 0) {
			try {
				guild.retrieveBanById(args[1]).queue(success -> {}, 
				fail -> {
					channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslatedString("usernotfound", author, guild), LanguageSystem.getTranslatedString("tagornameuser", author, guild), false).build()).queue();
				});
				Ban ban = guild.retrieveBanById(args[1]).complete();
				String reason = ban.getReason();
				guild.unban(ban.getUser()).queue();
				channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild).setTitle("Unban").addField(LanguageSystem.getTranslatedString("userunbanned", author, guild), LanguageSystem.getTranslatedString("bannedfor", author, guild).replace("%reason%", "**" + reason + "**"), false).build()).queue();
			} catch (Exception ignored) {}
		} else {
			channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild).setTitle("Unban").addField(LanguageSystem.getTranslatedString("userunbanned", author, guild), LanguageSystem.getTranslatedString("bannedfor", author, guild).replace("%reason%", "**" + guild.retrieveBan(mentionedMembers.get(0).getUser()).complete().getReason() + "**"), false).build()).queue();
			guild.unban(mentionedMembers.get(0).getUser()).queue();
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.BAN_MEMBERS};
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public String getSyntax() {
		return "<@User>";
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}

}
