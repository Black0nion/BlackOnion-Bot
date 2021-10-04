package com.github.black0nion.blackonionbot.commands.fun;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CatBreedsCommand extends SlashCommand {

    private static LinkedList<HashMap<String, String>> pages = new LinkedList<>();

    public CatBreedsCommand() {
	this.setData(new CommandData("catexamples", "Shows all available cat breeds for the cat command"));
	Bot.executor.submit(() -> {
	    try {
		Unirest.setTimeouts(0, 0);
		final HttpResponse<String> response = Unirest.get("https://api.thecatapi.com/v1/breeds").header("Content-Type", "application/json").asString();
		final JSONArray responseAsJSONArray = new JSONArray(response.getBody());

		final int pageCount = (int) Math.ceil(responseAsJSONArray.length() / 10D);
		for (int i = 0; i < pageCount; i++) {
		    final HashMap<String, String> entrysOnThisPage = new LinkedHashMap<>();
		    for (int j = i * 10; j < (i + 1) * 10; j++) {
			if (j >= responseAsJSONArray.length()) {
			    break;
			}
			final JSONObject jsonObjectForThisPage = responseAsJSONArray.getJSONObject(j);
			entrysOnThisPage.put(jsonObjectForThisPage.getString("name"), jsonObjectForThisPage.getString("id"));
		    }
		    pages.add(entrysOnThisPage);
		}
		System.out.println("Successfully loaded the catbreeds!");
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	});
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final EmbedBuilder builder = cmde.success().setTitle("UwU");
	pages.get(0).forEach((name, value) -> {
	    builder.addField(name, value, false);
	});

	e.replyEmbeds(builder.build()).queue(m -> {
	    m.retrieveOriginal().queue(msg -> {
		for (int i = 0; i < pages.size(); i++) {
		    msg.addReaction(Utils.numbersUnicode.get(i)).queue();
		}
		waitForPageSwitch(cmde, msg, author);
	    });
	});
    }

    private static void waitForPageSwitch(final SlashCommandExecutedEvent cmde, final Message msg, final BlackUser user) {
	CommandBase.waiter.waitForEvent(GuildMessageReactionAddEvent.class, event -> event.getUserIdLong() == user.getIdLong() && event.getMessageIdLong() == msg.getIdLong(), event -> {
	    event.getReaction().removeReaction(event.getUser()).queue();
	    final Integer emojiReactionNum = Utils.numbersUnicode.entrySet().stream().filter(entry -> entry.getValue().equals(event.getReactionEmote().getAsCodepoints())).findFirst().get().getKey();

	    if (!event.getReactionEmote().isEmoji() || !Utils.numbersUnicode.containsValue(event.getReactionEmote().getAsCodepoints()) || pages.size() < emojiReactionNum) {
		waitForPageSwitch(cmde, msg, user);
	    }

	    // all fine, we can switch page
	    final EmbedBuilder builder = cmde.success().setTitle("UwU");
	    pages.get(emojiReactionNum).forEach((name, value) -> {
		builder.addField(name, value, false);
	    });

	    msg.editMessageEmbeds(builder.build()).queue();
	    waitForPageSwitch(cmde, msg, user);
	}, 1, TimeUnit.MINUTES, () -> {
	    msg.delete().queue();
	});
    }
}