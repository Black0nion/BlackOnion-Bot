package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrefixInfo extends ListenerAdapter {
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.getMessage().getMentionedUsers().size() == 0) return;
		final User author = event.getAuthor();
		final Guild guild = event.getGuild();
		if (event.getMessage().getContentRaw().replace("!", "").startsWith(event.getJDA().getSelfUser().getAsMention())) {
			final String[] args = event.getMessage().getContentRaw().split(" ");
			if (event.getMember().hasPermission(Permission.ADMINISTRATOR) && args.length >= 3 && args[1].equalsIgnoreCase("prefix")) {
				if (args[2].toCharArray().length > 10) {
					event.getChannel().sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("toolong", "undertenchars", false).build()).queue();
					return;
				}
				BotInformation.setPrefix(guild, args[2]);
			}
			event.getChannel().sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle(":wave:").addField(LanguageSystem.getTranslatedString("myprefixis", author, guild).replace("%prefix%", BotInformation.getPrefix(guild)), LanguageSystem.getTranslatedString("changeprefix", author, guild).replace("%command%", event.getJDA().getSelfUser().getAsMention() + " prefix <prefix>"), false).build()).queue();
		}
	}
}