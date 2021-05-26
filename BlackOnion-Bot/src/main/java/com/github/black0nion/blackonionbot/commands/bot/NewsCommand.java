package com.github.black0nion.blackonionbot.commands.bot;

import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.news.Newspost;
import com.github.black0nion.blackonionbot.systems.news.Newssystem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NewsCommand extends Command {
	
	public NewsCommand() {
		this.setCommand("news");
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		EmbedBuilder builder = cmde.success()
				.setTitle("clickfornewspage", "https://www.black-onion.com/news/");
		List<Newspost> news = Newssystem.posts.subList(0, (Newssystem.posts.size() <= 5 ? Newssystem.posts.size() : 5)); 
		for (int i = 0; i < news.size(); i++) {
			Newspost post = news.get(i);
			builder.addField(post.title, "`" + post.date + "`\n" + post.content + (i != news.size()-1 ? "\n​" : ""), false);
		}
		cmde.reply(builder);
	}
}