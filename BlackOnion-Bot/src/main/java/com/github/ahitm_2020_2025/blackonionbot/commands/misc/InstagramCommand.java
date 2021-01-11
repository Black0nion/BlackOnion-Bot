package com.github.ahitm_2020_2025.blackonionbot.commands.misc;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InstagramCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Reason:", "Bitte schrei Insta an weil die nur ne scheiﬂapi haben xD", false).build()).queue();
	}
	
	@Override
	public Progress getProgress() {
		return Progress.PLANNED;
	}
	
	@Override
	public Category getCategory() {
		return Category.MISC;
	}

	@Override
	public String[] getCommand() {
		return new String[]{"instagram", "insta"};
	}
}
