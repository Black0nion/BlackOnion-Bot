package com.github.black0nion.blackonionbot.commands.bot;

import java.util.List;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.news.Newspost;
import com.github.black0nion.blackonionbot.systems.news.Newssystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NewsCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "news" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild)
				.setTitle(LanguageSystem.getTranslatedString("clickfornewspage", author, guild), "https://www.black-onion.com/news/");
		List<Newspost> news = Newssystem.posts.subList(0, (Newssystem.posts.size() <= 5 ? Newssystem.posts.size() : 5)); 
		for (int i = 0; i < news.size(); i++) {
			Newspost post = news.get(i);
			builder.addField(post.title, "`" + post.date + "`\n" + post.content + (i != news.size()-1 ? "\n​" : ""), false);
		}
		channel.sendMessage(builder.build()).queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
}
