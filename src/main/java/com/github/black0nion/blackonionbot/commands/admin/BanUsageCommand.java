package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.bson.Document;

import java.util.regex.Pattern;

public class BanUsageCommand extends SlashCommand {

	private static final SubcommandData[] data = {
		new SubcommandData("user", "(un)ban a user").addOption(OptionType.USER, "targetuser", "user to (un)ban", true),
		new SubcommandData("guild", "(un)ban a guild").addOption(OptionType.STRING, "targetguild", "guild to (un)ban", true),
	};

	public BanUsageCommand() {
		super(builder(
			Commands.slash("usage", "(un)ban a user or a guild from using commands.")
				.addSubcommandGroups(
					new SubcommandGroupData("ban", "Ban a user or a guild from using commands.").addSubcommands(data),
					new SubcommandGroupData("unban", "Unban a user or a guild from using commands.").addSubcommands(data)))
			.setAdminGuild()
			.permissions(CustomPermission.BAN_USAGE)
		);
	}

	public static final MongoCollection<Document> collection = MongoDB.DATABASE.getCollection("usagebans");
	private static final Pattern guildIdPattern = Pattern.compile("^\\d{17,18}$");

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final User user = e.getOption("targetuser", OptionMapping::getAsUser);
		final String guildId = e.getOption("targetguild", OptionMapping::getAsString);

		assert e.getSubcommandGroup() != null : "Subcommand group is null";

		if (user == null && guildId == null) {
			cmde.send("wrongargumentcount");
			return;
		}

		if (user != null) {
			if (e.getSubcommandGroup().equalsIgnoreCase("ban")) {
				MongoManager.insertOne(collection, new Document("userid", user.getIdLong()));
				cmde.send("cantusecommandsanymore", new Placeholder("userorguild", user.getAsTag()));
			} else {
				collection.deleteOne(Filters.eq("userid", user.getIdLong()));
				cmde.send("userunbanned");
			}
		}

		if (guildId != null) {
			if (guildIdPattern.matcher(guildId).matches()) {
				if (e.getSubcommandGroup().equalsIgnoreCase("ban")) {
					MongoManager.insertOne(collection, new Document("guildid", guildId));
					cmde.send("cantusecommandsanymore", new Placeholder("userorguild", guildId));
				} else {
					collection.deleteOne(Filters.eq("guildid", guildId));
					cmde.send("guildunbanned");
				}
			} else {
				cmde.send("invalidguildid");
			}
		}
	}
}