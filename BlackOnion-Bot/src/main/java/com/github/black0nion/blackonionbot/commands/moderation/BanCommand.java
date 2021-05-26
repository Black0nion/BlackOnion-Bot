package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanCommand extends Command {
	
	public BanCommand() {
		this.setCommand("ban", "permayeet")
			.setSyntax("<@User> [reason]")
			.setRequiredArgumentCount(1)
			.setRequiredPermissions(Permission.BAN_MEMBERS)
			.setRequiredBotPermissions(Permission.BAN_MEMBERS);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final List<BlackMember> mentionedMembers = message.getMentionedBlackMembers();
		if (mentionedMembers.size() == 0) {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("tagornameuser", author, guild), false).build()).queue();
			return;
		} else {
			String banMessage = LanguageSystem.getTranslation("yougotbanned", author);
			if (args.length >= 3) {
				banMessage = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				guild.ban(mentionedMembers.get(0), 0, banMessage).queue();
			} else
				guild.ban(mentionedMembers.get(0), 0).queue();
			final String finalBanMessage = banMessage;
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle("Ban").addField(LanguageSystem.getTranslation("usergotbanned", author, guild), LanguageSystem.getTranslation("message", author, guild) + ": " + banMessage, false).build()).queue();
			mentionedMembers.get(0).getBlackUser().openPrivateChannel().queue(c -> {
				c.sendMessage(EmbedUtils.getErrorEmbed(author, guild).setTitle("Ban").addField(LanguageSystem.getTranslation("yougotbanned", author), LanguageSystem.getTranslation("message", author) + ": " + finalBanMessage, false).build()).queue();
			});
		}
	}
}