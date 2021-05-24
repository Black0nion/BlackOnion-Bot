package com.github.black0nion.blackonionbot.commands.fun;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class CatBreedsCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "catbreeds", "catexamples" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.get("https://api.thecatapi.com/v1/breeds")
			  .header("Content-Type", "application/json")
			  .asString();
			final JSONArray responseAsJSONArray = new JSONArray(response.getBody());
			final HashMap<Integer, HashMap<String, String>> pages = new HashMap<>();
			
			final int pageCount = (int) Math.ceil((double) responseAsJSONArray.length() / 10D);
			for (int i = 0; i < pageCount; i++) {
				HashMap<String, String> entrysOnThisPage = new HashMap<>();
				for (int j = i * 10; j < (i+1) * 10; j++) {
					if (j >= responseAsJSONArray.length()) break;
					final JSONObject jsonObjectForThisPage = responseAsJSONArray.getJSONObject(j);
					entrysOnThisPage.put(jsonObjectForThisPage.getString("name"), jsonObjectForThisPage.getString("id"));
				}
				pages.put(i, entrysOnThisPage);
			}
			
			System.out.println(pages);
			
			EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild).setTitle("UwU");
			pages.get(0).forEach((name, value) -> {
				builder.addField(name, value, false);
			});
			
			message.reply(builder.build()).queue((msg) -> {
				for (int i = 0; i < pages.size(); i++)
					msg.addReaction(Utils.numbersUnicode.get(i)).queue();
				waitForPageSwitch(msg, author, pages);
			});
		} catch (Exception ex) {
			ex.printStackTrace();
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
			return;
		}
	}
	
	private static void waitForPageSwitch(Message msg, User user, HashMap<Integer, HashMap<String, String>> pages) {
		CommandBase.waiter.waitForEvent(GuildMessageReactionAddEvent.class,
				(event) -> event.getUserIdLong() == user.getIdLong() && event.getMessageIdLong() == msg.getIdLong(),
				(event) -> {
					event.getReaction().removeReaction(event.getUser()).queue();
					Integer emojiReactionNum = Utils.numbersUnicode.entrySet().stream().filter((entry) -> {return entry.getValue().equals(event.getReactionEmote().getAsCodepoints());}).findFirst().get().getKey();
					
					if (!event.getReactionEmote().isEmoji() || !Utils.numbersUnicode.containsValue(event.getReactionEmote().getAsCodepoints()) || pages.size() < emojiReactionNum)
						waitForPageSwitch(msg, user, pages);
					
					// all fine, we can switch page
					EmbedBuilder builder = EmbedUtils.getSuccessEmbed(user, msg.getGuild()).setTitle("UwU");
					pages.get(emojiReactionNum).forEach((name, value) -> {
						builder.addField(name, value, false);
					});
					
					msg.editMessage(builder.build()).queue();
					waitForPageSwitch(msg, user, pages);
				}, 1, TimeUnit.MINUTES, () -> { msg.delete().queue(); });
	}

	@Override
	public Category getCategory() {
		return Category.FUN;
	}
}