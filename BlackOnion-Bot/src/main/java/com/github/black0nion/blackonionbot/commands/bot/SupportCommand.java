package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SupportCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"support"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).setTitle("Support").addField("Discord Server:", "[Invite Link](https://discord.gg/ZzGKy9RCBY)", false)
				.addField("Creators", "[SIMULATAN](https://github.com/SIMULATAN), [ManuelP](https://github.com/ManuelPuchner), [MatseCrafter_304](https://github.com/MatseCrafter-304)", false)
				.addField("Libraries", "```org.json:20201115\ncom.google.gson\nnet.dv8tion:JDA:4.2.0_227\ncom.jagrosh:jda-utilities:3.0.5\ncom.sparkjava:spark-core:2.9.3\nnet.java.dev.jna:jna-platform:5.6.0\norg.menudocs:paste-client-java:1.0.16\norg.xerial:sqlite-jdbc:3.32.3.2\ncom.mashape.unirest:unirest-java:1.3.1\nclub.minnced:discord-webhooks:0.5.4-rc```", false)
				.build()).queue();
	}

	@Override
	public Category getCategory() {
		return Category.BOT;
	}
}
