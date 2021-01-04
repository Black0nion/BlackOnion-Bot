package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).setTitle("Ping").addField("Pong :D", "Mein Ping: " + e.getJDA().getGatewayPing() + "ms", false).build()).queue();
	}

	@Override
	public String getDescription() {
		return "Zeigt den Ping des Bots an";
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