package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bson.Document;

import java.util.regex.Pattern;

public class BanUsageCommand extends SlashCommand {

	public BanUsageCommand() {
		super(builder(
			Commands.slash("banusage", "Ban a user or a guild from using commands.")
				.addOption(OptionType.USER, "user", "The user to ban")
				.addOption(OptionType.STRING, "guild", "The id of the guild to ban"))
			.setHidden()
			.setRequiredCustomPermissions(CustomPermission.BAN_USAGE));
	}

	public static final MongoCollection<Document> collection = MongoDB.DATABASE.getCollection("usagebans");
	private static final Pattern guildIdPattern = Pattern.compile("^\\d{17,18}$");

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final User user = e.getOption("user", OptionMapping::getAsUser);
		final String guildId = e.getOption("guild", OptionMapping::getAsString);
		if (user == null && guildId == null) {
			cmde.send("wrongargumentcount");
		} else {
			if (user != null) {
				MongoManager.insertOne(collection, new Document("userid", user.getId()));
				cmde.send("cantusecommandsanymore", new Placeholder("userorguild", user.getAsTag()));
			}
			if (guildId != null) {
				if (guildIdPattern.matcher(guildId).matches()) {
					MongoManager.insertOne(collection, new Document("guildid", guildId));
					cmde.send("cantusecommandsanymore", new Placeholder("userorguild", guildId));
				} else {
					cmde.send("invalidguildid");
				}
			}
		}
	}
}