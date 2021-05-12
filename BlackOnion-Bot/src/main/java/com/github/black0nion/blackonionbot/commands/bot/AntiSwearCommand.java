package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AntiSwearCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "antiswear" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (args.length >= 2) {
			boolean on;
			if (args[1].equalsIgnoreCase("on")) on = true;
			else if (args[1].equalsIgnoreCase("off")) on = false;
			else {
				print(e, channel, author, member, guild);
				return;
			}
			
			if (!GuildManager.isPremium(guild)) {
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notpremium", "premiumrequired", false).build()).queue();
				return;
			}
			GuildManager.save(guild, "antiSwear", on);
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("antiswearstatuschanged", LanguageSystem.getTranslatedString("antiswearis", author, guild).replace("%status%", LanguageSystem.getTranslatedString(on ? "on" : "off", author, guild)), false).build()).queue();
		} else print(e, channel, author, member, guild);
	}
	
	private final void print(GuildMessageReceivedEvent e, MessageChannel channel, User author, Member member, Guild guild) {
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslatedString("antiswearstatus", author, guild).replace("%status%", LanguageSystem.getTranslatedString(GuildManager.getBoolean(guild, "antiSwear") ? "on" : "off", author, guild)), LanguageSystem.getTranslatedString("howtoantiswearstatustoggle", author, guild).replace("%command%", "``" + BotInformation.getPrefix(guild) + getCommand()[0] + " " + getSyntax() + "``"), false).build()).queue();
	}
	
	@Override
	public String getSyntax() {
		return "<on | off>";
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.ADMINISTRATOR };
	}
}
