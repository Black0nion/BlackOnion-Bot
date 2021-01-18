package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class KickCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"kick", "yeet"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() == 0) {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslatedString("wrongargument", author, guild), LanguageSystem.getTranslatedString("tagornameuser", author, guild), false).build()).queue();
			return;
		} else {
			guild.kick(mentionedMembers.get(0)).queue();
			String kickMessage = LanguageSystem.getTranslatedString("yougotkicked", author);
			if (args.length >= 3) {
				kickMessage = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
			}
			channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild).setTitle("Kick").addField(LanguageSystem.getTranslatedString("usergotkicked", author, guild), LanguageSystem.getTranslatedString("message", author, guild) + ": " + kickMessage, false).build()).queue();
			mentionedMembers.get(0).getUser().openPrivateChannel().complete().sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).setTitle("Kick").addField(LanguageSystem.getTranslatedString("yougotkicked", author), LanguageSystem.getTranslatedString("message", author) + ": " + kickMessage, false).build()).queue();
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.KICK_MEMBERS};
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public String getSyntax() {
		return "<@User> [reason]";
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}

}
