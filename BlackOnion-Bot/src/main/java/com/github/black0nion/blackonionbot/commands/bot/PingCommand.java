package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).setTitle("Ping").addField("Pong :D", LanguageSystem.getTranslatedString("myping", author, guild) + e.getJDA().getGatewayPing() + "ms", false).build()).queue();
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