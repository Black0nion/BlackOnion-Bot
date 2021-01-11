package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.DefaultValues;
import com.github.ahitm_2020_2025.blackonionbot.bot.Bot;
import com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.systems.BirthdaySystem;
import com.github.ahitm_2020_2025.blackonionbot.systems.MessageLogSystem;
import com.github.ahitm_2020_2025.blackonionbot.systems.language.LanguageSystem;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReloadCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		message.delete().queue();
		//e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID, "k").queue();
		reload();
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).addField("Alle Configs wurden reloaded!", "Diese Nachricht löscht sich in 5 Sekunden!", false).build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
	}
	
	public static void reload() {
		DefaultValues.init();
		MessageLogSystem.init();
		Bot.notifyStatusUsers = new ArrayList<String>(ValueManager.getArrayAsList("notifyUsers"));
		BotInformation.init();
		BirthdaySystem.reload();
		LanguageSystem.init();
	}
	
	@Override
	public boolean requiresBotAdmin() {
		return true;
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"reload", "rl"};
	}

}
