package com.github.black0nion.blackonionbot.commands.bot;

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

public class PingCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("Ping").addField("Pong :D", LanguageSystem.getTranslatedString("myping", author, guild).replace("%ping%", String.valueOf(e.getJDA().getGatewayPing())), false).build()).queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"ping"};
	}
}