package com.github.black0nion.blackonionbot.commands.bot;

import java.time.Duration;
import java.util.ArrayList;

import com.github.black0nion.blackonionbot.DefaultValues;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.systems.BirthdaySystem;
import com.github.black0nion.blackonionbot.systems.MessageLogSystem;
import com.github.black0nion.blackonionbot.systems.dashboard.SessionManager;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ReloadCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(e.getChannel(), Permission.MESSAGE_MANAGE)) return;
		message.delete().queue();
		//e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID, "k").queue();
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("configreload", "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		reload();
	}
	
	public static void reload() {
		DefaultValues.init();
		MessageLogSystem.init();
		Bot.notifyStatusUsers = new ArrayList<String>(ValueManager.getArrayAsList("notifyUsers"));
		BotInformation.init();
		BirthdaySystem.reload();
		LanguageSystem.init();
		GuildManager.init();
		SessionManager.init();
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
