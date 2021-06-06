package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;

public class PrefixInfo {
	
	@SuppressWarnings("deprecation")
	public static void handle(final CommandEvent cmde) {
		final BlackMessage message = cmde.getMessage();
		final BlackUser author = cmde.getUser();
		if (author.isBot() || message.getMentionedUsers().size() == 0) return;
		final BlackGuild guild = cmde.getGuild();
		final BlackMember member = cmde.getMember();
		final JDA jda = cmde.getJda();
		
		final String msgContent = message.getContentRaw();
		if (msgContent.replace("!", "").startsWith(cmde.getEvent().getJDA().getSelfUser().getAsMention())) {
			final String[] args = msgContent.split(" ");
			if (member.hasPermission(Permission.ADMINISTRATOR)) {
				if (args.length >= 3 && args[1].equalsIgnoreCase("prefix")) {					
					if (args[2].toCharArray().length > 10) {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("toolong", "undertenchars", false).build()).queue();
						return;
					}
					guild.setPrefix(args[2]);
					message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("prefixchanged", LanguageSystem.getTranslation("myprefixis", author, guild).replace("%prefix%", guild.getPrefix()), false).build()).queue();
					return;
				} else if (args.length >= 2 && msgContent.replace("!", "").startsWith(jda.getSelfUser().getAsMention() + "prefix")) {
					if (args[1].toCharArray().length > 10) {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("toolong", "undertenchars", false).build()).queue();
						return;
					}
					guild.setPrefix(args[1]);
					message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("prefixchanged", LanguageSystem.getTranslation("myprefixis", author, guild).replace("%prefix%", guild.getPrefix()), false).build()).queue();
					return;
				}
			} else {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("missingpermissions", author, guild), LanguageSystem.getTranslation("requiredpermissions", author, guild) + "\n" + Utils.getPermissionString(Permission.ADMINISTRATOR), false).build()).queue();
				return;
			}
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle(":wave:").addField(LanguageSystem.getTranslation("myprefixis", author, guild).replace("%prefix%", guild.getPrefix()), LanguageSystem.getTranslation("changeprefix", author, guild).replace("%command%", jda.getSelfUser().getAsMention() + " prefix <prefix>"), false).build()).queue();
		}
	}
}