package com.github.black0nion.blackonionbot.commands.slash.impl.admin;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GuildListCommand extends SlashCommand {

	public GuildListCommand() {
		super(builder("guilds", "List all guilds").setAdminGuild());
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, BlackGuild eventGuild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		final List<Page> pages = new ArrayList<>();
		final EmbedBuilder baseEmbed = cmde.success()
			.setTitle("Guilds")
			.setDescription("```");
		EmbedBuilder currentEmbed = new EmbedBuilder(baseEmbed);
		boolean found = false;
		for (Guild guild : e.getJDA().getGuilds()) {
			found = true;
			logger.info("'{}'", guild.getOwner());
			@Nullable
			User owner = Optional.ofNullable(guild.getOwner())
				.map(Member::getUser)
				.orElse(null);
			String text = "- " + Utils.escapeMarkdown(guild.getName()) + " (" + guild.getId() + ")";
			try {
				if (currentEmbed.getDescriptionBuilder().length() + text.length() + 4 >= MessageEmbed.DESCRIPTION_MAX_LENGTH)
					return;
				currentEmbed.appendDescription("\n" + text + " (Owner: " + (owner == null ? cmde.getTranslation("empty") : Utils.escapeMarkdown(owner.getAsTag())) + ")");
			} catch (Exception ignored) {
				pages.add(new InteractPage(currentEmbed.appendDescription("\n```").build()));
				currentEmbed = new EmbedBuilder(baseEmbed);
				currentEmbed.appendDescription(text);
			}
		}
		pages.add(new InteractPage(currentEmbed.appendDescription("\n```").build()));
		// TODO: Fix or test
		if (!found)
			cmde.send("noguildsfound");
		else {
			cmde.reply((MessageEmbed) pages.get(0).getContent(), success -> success.retrieveOriginal().queue(message -> Pages.paginate(message, pages, true, 2, TimeUnit.MINUTES, true, u -> u.getIdLong() == author.getIdLong())));
		}
	}
}
