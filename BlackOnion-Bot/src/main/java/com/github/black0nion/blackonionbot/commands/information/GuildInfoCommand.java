package com.github.black0nion.blackonionbot.commands.information;

import java.time.format.DateTimeFormatter;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildInfoCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "guildinfo", "serverinfo" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		//TODO: fix shit
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild)
				.setTitle(LanguageSystem.getTranslatedString("guildinfo", author, guild))
				.setThumbnail(guild.getIconUrl())
				.addField(LanguageSystem.getTranslatedString("name", author, guild), guild.getName(), true)
				.addField("Owner", guild.retrieveOwner().complete().getUser().getAsMention(), true)
				.addField(LanguageSystem.getTranslatedString("serverid", author, guild), guild.getId(), true)
				.addField(LanguageSystem.getTranslatedString("rolecount", author, guild), String.valueOf(guild.getRoles().size()), true)
				.addField(LanguageSystem.getTranslatedString("membercount", author, guild), String.valueOf(guild.getMemberCount()), true)
				.addField(LanguageSystem.getTranslatedString("channelcount", author, guild), String.valueOf(guild.getChannels().size()), true)
				.addField(LanguageSystem.getTranslatedString("boostlevel", author, guild), guild.getBoostTier().name(), true)
				.addField(LanguageSystem.getTranslatedString("created", author, guild), guild.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), true)
				.build()).queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.INFORMATION;
	}
}
