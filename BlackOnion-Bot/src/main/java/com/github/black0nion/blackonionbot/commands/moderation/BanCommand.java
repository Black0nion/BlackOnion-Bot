package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "ban", "permayeet" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() == 0) {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslatedString("wrongargument", author, guild), LanguageSystem.getTranslatedString("tagornameuser", author, guild), false).build()).queue();
			return;
		} else {
			String banMessage = LanguageSystem.getTranslatedString("yougotbanned", author);
			if (args.length >= 3) {
				banMessage = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				guild.ban(mentionedMembers.get(0), 0, banMessage).queue();
			} else
				guild.ban(mentionedMembers.get(0), 0).queue();
			final String finalBanMessage = banMessage;
			channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild).setTitle("Ban").addField(LanguageSystem.getTranslatedString("usergotbanned", author, guild), LanguageSystem.getTranslatedString("message", author, guild) + ": " + banMessage, false).build()).queue();
			mentionedMembers.get(0).getUser().openPrivateChannel().queue(c -> {
				c.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).setTitle("Ban").addField(LanguageSystem.getTranslatedString("yougotbanned", author), LanguageSystem.getTranslatedString("message", author) + ": " + finalBanMessage, false).build()).queue();
			});
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.BAN_MEMBERS };
	}
	
	@Override
	public Permission[] getRequiredBotPermissions() {
		return new Permission[] { Permission.BAN_MEMBERS };
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