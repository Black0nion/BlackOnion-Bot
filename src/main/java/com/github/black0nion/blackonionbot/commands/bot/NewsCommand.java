package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.news.Newspost;
import com.github.black0nion.blackonionbot.systems.news.Newssystem;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewsCommand extends SlashCommand {

	public NewsCommand() {
		super("news", "Display the latest news posts");
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final List<Page> pages = new ArrayList<>();
		final EmbedBuilder baseEmbed = cmde.success()
			.setTitle("news", "https://black-onion.com/news/");
		EmbedBuilder currentEmbed = new EmbedBuilder(baseEmbed);
		boolean found = false;
		for (Newspost news : Newssystem.posts) {
			found = true;
			currentEmbed.addField(news.title(), "`" + news.date() + "`\n" + news.content(), false);
			if (currentEmbed.getFields().size() >= 10) {
				pages.add(new InteractPage(currentEmbed.build()));
				currentEmbed = new EmbedBuilder(baseEmbed).clearFields();
			}
		}
		pages.add(new InteractPage(currentEmbed.build()));
		if (!found)
			cmde.send("nonewsfound");
		else {
			cmde.reply((MessageEmbed) pages.get(0).getContent(), success -> success.retrieveOriginal().queue(message -> Pages.paginate(message, pages, true, 2, TimeUnit.MINUTES, true, u -> u.getIdLong() == author.getIdLong())));
		}
	}
}