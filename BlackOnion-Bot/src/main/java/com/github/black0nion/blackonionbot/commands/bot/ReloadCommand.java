package com.github.black0nion.blackonionbot.commands.bot;

import java.time.Duration;
import java.util.ArrayList;

import com.github.black0nion.blackonionbot.DefaultValues;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.BirthdaySystem;
import com.github.black0nion.blackonionbot.systems.dashboard.SessionManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ReloadCommand extends Command {
	
	public ReloadCommand() {
		this.setCommand("reload", "rl")
			.botAdminRequired()
			.setHidden();
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(e.getChannel(), Permission.MESSAGE_MANAGE)) return;
		message.delete().queue();
		//e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID, "k").queue();
		// TODO: delete after x seconds
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("configreload", "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		reload();
	}
	
	@SuppressWarnings("deprecation")
	public static void reload() {
		DefaultValues.init();
		Bot.notifyStatusUsers = new ArrayList<String>(ValueManager.getArrayAsList("notifyUsers"));
		BotInformation.init();
		BirthdaySystem.reload();
		LanguageSystem.init();
		SessionManager.init();
		CommandBase.addCommands(CommandBase.waiter);
		
		BlackGuild.clearCache();
		BlackMember.clearCache();
		BlackMessage.clearCache();
		BlackUser.clearCache();
	}
}