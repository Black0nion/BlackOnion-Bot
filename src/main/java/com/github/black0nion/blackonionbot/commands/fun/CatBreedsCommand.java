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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CatBreedsCommand extends SlashCommand {

	public CatBreedsCommand() {
		super("catbreeds", "Shows you a list of avaliable cat breeds");
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author,
			BlackGuild guild, TextChannel channel) {
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.thecatapi.com/v1/breeds")).GET()
					.header("Content-Type", "application/json").timeout(Duration.ofSeconds(2)).build();
			final JSONArray responseAsJSONArray = new JSONArray(
					Bot.getInstance().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body());

			final EmbedBuilder baseEmbed = cmde.success().setTitle("Cato :3");
			EmbedBuilder currentEmbed = new EmbedBuilder(baseEmbed);

			final List<Page> pages = new ArrayList<>();
			boolean found = false;
			for (int i = 1; i <= responseAsJSONArray.length(); i++) {
				found = true;
				final JSONObject jsonObjectForThisPage = responseAsJSONArray.getJSONObject(i - 1);
				currentEmbed.addField(jsonObjectForThisPage.getString("name"), jsonObjectForThisPage.getString("id"),
						false);
				if (i % 10 == 0) {
					pages.add(new InteractPage(currentEmbed.build()));
					currentEmbed = new EmbedBuilder(baseEmbed);
				}
			}
			if (currentEmbed.getFields().size() > 0) {
				pages.add(new InteractPage(currentEmbed.build()));
			}
			if (!found) {
				throw new DummyException("No breeds found");
			} else {
				cmde.reply((MessageEmbed) pages.get(0).getContent(),
						success -> success.retrieveOriginal().queue(message -> Pages.paginate(message, pages, true, 2,
								TimeUnit.MINUTES, true, u -> u.getIdLong() == author.getIdLong())));
			}
		} catch (final Exception ex) {
			cmde.exception(ex);
		}
	}
}
