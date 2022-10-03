package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.DummyException;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import com.google.common.base.Suppliers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CatBreedsCommand extends SlashCommand {

	public CatBreedsCommand() {
		super("catbreeds", "Shows you a list of avaliable cat breeds");
	}

	private static final Logger logger = LoggerFactory.getLogger(CatBreedsCommand.class);

	private final Supplier<JSONArray> breedsCache = Suppliers.memoizeWithExpiration(() -> {
		logger.info("Reloading cat breeds cache");
		HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.thecatapi.com/v1/breeds"))
			.GET()
			.header("Content-Type", "application/json")
			.timeout(Duration.ofSeconds(2))
			.build();

		logger.debug("Sending request");

		try {
			String body = Bot.getInstance().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
			logger.debug("Cache loading response: {}", body);
			return new JSONArray(body);
		} catch (Exception ignored) {
			logger.error("Error loading cat breeds cache", ignored);
			return null;
		}
	}, 1, TimeUnit.DAYS);

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		try {
			JSONArray response = breedsCache.get();

			final EmbedBuilder baseEmbed = cmde.success().setTitle("Cat");
			EmbedBuilder currentEmbed = new EmbedBuilder(baseEmbed);

			final List<Page> pages = new ArrayList<>();
			boolean found = false;
			for (int i = 1; i <= response.length(); i++) {
				found = true;
				final JSONObject jsonObjectForThisPage = response.getJSONObject(i - 1);
				currentEmbed.addField(jsonObjectForThisPage.getString("name"), jsonObjectForThisPage.getString("id"), false);
				if (i % 10 == 0) {
					pages.add(new InteractPage(currentEmbed.build()));
					currentEmbed = new EmbedBuilder(baseEmbed);
				}
			}
			if (!currentEmbed.getFields().isEmpty()) {
				pages.add(new InteractPage(currentEmbed.build()));
			}
			if (!found) {
				throw new DummyException("No breeds found");
			} else {
				cmde.reply((MessageEmbed) pages.get(0).getContent(), success -> success.retrieveOriginal().queue(message -> Pages.paginate(message, pages, true, 2, TimeUnit.MINUTES, true, u -> u.getIdLong() == author.getIdLong())));
			}
		} catch (final Exception ex) {
			cmde.exception(ex);
		}
	}
}
