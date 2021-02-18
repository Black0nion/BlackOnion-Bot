package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.bot.BotInformation;
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
		final long botId = BotInformation.botId;
		if (event.getMessage().getMentionedUsers().stream().filter(user -> {return user.getIdLong() == botId;}).findFirst().get() != null) {
			if (event.getMember().hasPermission(Permission.ADMINISTRATOR) && event.getMessage().getContentRaw().split(" ").length >= 2)
				BotInformation.setPrefix(guild, event.getMessage().getContentRaw().split(" ")[1]);
			event.getChannel().sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("\u1F44B", "My Prefix is: " + BotInformation.getPrefix(guild), false).build()).queue();
		}
	}
}
