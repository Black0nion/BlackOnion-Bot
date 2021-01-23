package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.DefaultValues;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.systems.BirthdaySystem;
import com.github.black0nion.blackonionbot.systems.MessageLogSystem;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReloadCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		message.delete().queue();
		//e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID, "k").queue();
		reload();
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).addField(LanguageSystem.getTranslatedString("configreload", author, guild), LanguageSystem.getTranslatedString("messagedelete5", author, guild), false).build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
	}
	
	public static void reload() {
		DefaultValues.init();
		MessageLogSystem.init();
		Bot.notifyStatusUsers = new ArrayList<String>(ValueManager.getArrayAsList("notifyUsers"));
		BotInformation.init();
		BirthdaySystem.reload();
		LanguageSystem.init();
		GuildManager.init();
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
