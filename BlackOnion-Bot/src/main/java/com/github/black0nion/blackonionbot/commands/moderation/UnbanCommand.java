package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UnbanCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "unban", "unyeet" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final List<BlackMember> mentionedMembers = message.getMentionedBlackMembers();
		if (mentionedMembers.size() != 0) {
			final BlackUser bannedUser = mentionedMembers.get(0).getBlackUser();
			guild.retrieveBan(bannedUser).queue(ban -> {
				message.reply(EmbedUtils.getDefaultSuccessEmbed(author, guild).setTitle("Unban").addField(LanguageSystem.getTranslation("userunbanned", author, guild), LanguageSystem.getTranslation("bannedfor", author, guild).replace("%reason%", "**" + ban.getReason() + "**"), false).build()).queue();
			});
			guild.unban(bannedUser).queue();
		} else {
			try {
				guild.retrieveBanById(args[1]).queue(ban -> {
					String reason = ban.getReason();
					guild.unban(ban.getUser()).queue();
					message.reply(EmbedUtils.getDefaultSuccessEmbed(author, guild).setTitle("Unban").addField(LanguageSystem.getTranslation("userunbanned", author, guild), LanguageSystem.getTranslation("bannedfor", author, guild).replace("%reason%", "**" + reason + "**"), false).build()).queue();
				}, fail -> {
					message.reply(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("usernotfound", author, guild), LanguageSystem.getTranslation("tagornameuser", author, guild), false).build()).queue();
				});
			} catch (Exception ignored) {}
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.BAN_MEMBERS};
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
		return "<@User>";
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}
}