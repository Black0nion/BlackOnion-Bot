package com.github.black0nion.blackonionbot.commands.bot;

import java.time.Duration;

import org.bson.Document;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanUsageCommand implements Command {
	
	public static final MongoCollection<Document> collection = MongoDB.botDatabase.getCollection("usagebans");

	@Override
	public String[] getCommand() {
		return new String[] { "banusage", "usageban" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		if (args.length >= 2) {
			final String stuffToBan = args[2];
			if (stuffToBan.startsWith("u:")) {
				MongoManager.insertOne(collection, new Document().append("userid", stuffToBan.replace("u:", "")));
				channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("bannedusage", LanguageSystem.getTranslatedString("cantusecommandsanymore", author, guild).replace("%userorguild", stuffToBan), false).build()).queue();
				return;
			} else if (stuffToBan.contains("g:")) {
				MongoManager.insertOne(collection, new Document().append("guildid", stuffToBan.replace("u:", "")));
				channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("bannedusage", LanguageSystem.getTranslatedString("cantusecommandsanymore", author, guild).replace("%userorguild", stuffToBan), false).build()).queue();
				return;
			} else {
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", "wrongargumentcount", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				return;
			}
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", "wrongargumentcount", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();			return;
		}
	}

	@Override
	public boolean requiresBotAdmin() {
		return true;
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	@Override
	public String getSyntax() {
		return "<u:(userid) | g:(guildid)>";
	}
}
