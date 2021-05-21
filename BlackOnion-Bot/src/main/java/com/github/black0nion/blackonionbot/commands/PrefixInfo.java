package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrefixInfo extends ListenerAdapter {
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		final Message message = event.getMessage();
		if (event.getAuthor().isBot() || message.getMentionedUsers().size() == 0) return;
		final User author = event.getAuthor();
		final Guild guild = event.getGuild();
		final String msgContent = message.getContentRaw();
		if (msgContent.replace("!", "").startsWith(event.getJDA().getSelfUser().getAsMention())) {
			final String[] args = msgContent.split(" ");
			if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				if (args.length >= 3 && args[1].equalsIgnoreCase("prefix")) {					
					if (args[2].toCharArray().length > 10) {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("toolong", "undertenchars", false).build()).queue();
						return;
					}
					BotInformation.setPrefix(guild, args[2]);
					message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("prefixchanged", LanguageSystem.getTranslation("myprefixis", author, guild).replace("%prefix%", BotInformation.getPrefix(guild)), false).build()).queue();
					return;
				} else if (args.length >= 2 && msgContent.replace("!", "").startsWith(event.getJDA().getSelfUser().getAsMention() + "prefix")) {
					if (args[1].toCharArray().length > 10) {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("toolong", "undertenchars", false).build()).queue();
						return;
					}
					BotInformation.setPrefix(guild, args[1]);
					message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("prefixchanged", LanguageSystem.getTranslation("myprefixis", author, guild).replace("%prefix%", BotInformation.getPrefix(guild)), false).build()).queue();
					return;
				}
			} else {
				message.reply(EmbedUtils.getDefaultErrorEmbed(author, guild)
						.addField(LanguageSystem.getTranslation("missingpermissions", author, guild), LanguageSystem.getTranslation("requiredpermissions", author, guild) + "\n" + Utils.getPermissionString(Permission.ADMINISTRATOR), false).build()).queue();
				return;
			}
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).setTitle(":wave:").addField(LanguageSystem.getTranslation("myprefixis", author, guild).replace("%prefix%", BotInformation.getPrefix(guild)), LanguageSystem.getTranslation("changeprefix", author, guild).replace("%command%", event.getJDA().getSelfUser().getAsMention() + " prefix <prefix>"), false).build()).queue();
		}
	}
}