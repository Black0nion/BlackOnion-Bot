package com.github.black0nion.blackonionbot.commands.bot;

import java.time.Duration;

import org.bson.Document;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanUsageCommand extends Command {
	
	public BanUsageCommand() {
		this.setCommand("banusage", "usageban")
		.botAdminRequired()
		.setHidden()
		.setSyntax("<u:(userid) | g:(guildid)>");
	}
	
	public static final MongoCollection<Document> collection = MongoDB.botDatabase.getCollection("usagebans");

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		message.delete().queue();
		if (args.length >= 2) {
			final String stuffToBan = args[2];
			if (stuffToBan.startsWith("u:")) {
				MongoManager.insertOne(collection, new Document().append("userid", stuffToBan.replace("u:", "")));
				channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("bannedusage", LanguageSystem.getTranslation("cantusecommandsanymore", author, guild).replace("%userorguild", stuffToBan), false).build()).queue();
				return;
			} else if (stuffToBan.contains("g:")) {
				MongoManager.insertOne(collection, new Document().append("guildid", stuffToBan.replace("u:", "")));
				channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("bannedusage", LanguageSystem.getTranslation("cantusecommandsanymore", author, guild).replace("%userorguild", stuffToBan), false).build()).queue();
				return;
			} else {
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", "wrongargumentcount", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				return;
			}
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", "wrongargumentcount", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			return;
		}
	}
}