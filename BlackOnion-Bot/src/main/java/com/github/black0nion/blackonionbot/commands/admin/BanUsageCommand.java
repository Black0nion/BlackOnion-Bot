package com.github.black0nion.blackonionbot.commands.admin;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

// TODO: implement
public class BanUsageCommand extends SlashCommand {

    public BanUsageCommand() {
	this.setData(new CommandData("banusage", "Bans the usage for a specific user or guild")
		.addSubcommandGroups(
			new SubcommandGroupData("create", "Create a new ban").addSubcommands(new SubcommandData("guild", "File a Ban against a Guild")
		        	.addOption(OptionType.STRING, "guild", "The Guild to ban", true)
		        	.addOption(OptionType.STRING, "message", "The reason of the ban", false),
		        new SubcommandData("user", "File a Ban against a user")
		        	.addOption(OptionType.USER, "user", "The User to ban", true)
		        	.addOption(OptionType.STRING, "message", "The reason of the ban", false)),
			new SubcommandGroupData("delete", "Delete a existing ban").addSubcommands(
				new SubcommandData("guild", "Remove a Ban of a Guild").addOption(OptionType.STRING, "guild", "The ID of the Guild to unban", true),
				new SubcommandData("user", "Remove a Ban of a User").addOption(OptionType.USER, "user", "The User to unban", true)))
		.addSubcommands(new SubcommandData("list", "List all bans")
			.addOptions(new OptionData(OptionType.STRING, "type", "The type of the bans to show", false)
				.addChoice("Guild", "guild")
				.addChoice("User", "user"))))
	.setHidden();
    }

    public static final MongoCollection<Document> collection = MongoDB.botDatabase.getCollection("usagebans");

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (e.getCommandPath().contains("create") || e.getCommandPath().contains("delete")) {
	    String reason = null;
	    if (e.getOption("message") != null) {
		reason = e.getOption("message").getAsString();
	    }
	    long id;
	    if (e.getCommandPath().endsWith("user")) {
		id = e.getOptionsByType(OptionType.USER).get(0).getAsUser().getIdLong();
	    } else if (e.getCommandPath().endsWith("guild")) {
		try {
		    id = e.getOptionsByType(OptionType.STRING).get(0).getAsLong();
		} catch (final Exception ignored) {
		    cmde.errorPrivate("notanumber", "inputnumber");
		    return;
		}
	    } else {
		System.err.println("[BanUsage Command] Command Path " + e.getCommandPath() + " doesn't end with guild or user!");
		cmde.privateException();
		return;
	    }
	    final String[] split = e.getCommandPath().split("/");
	    if (e.getCommandPath().contains("create")) {
		final Document append = new Document().append(split[split.length-1] + "id", id);
		if (reason != null) {
		    append.append("reason", reason);
		}
		MongoManager.insertOne(collection, append);
	    } else {
		collection.deleteOne(Filters.eq(split[split.length-1] + "id", id));
	    }
	    cmde.successPrivate("bannedusage", cmde.getTranslation(e.getCommandPath().contains("create") ? "cantusecommandsanymore" : "canusecommandsagain", new Placeholder("userorguild", id)));
	    return;
	} else if (e.getCommandPath().contains("list")) {
	    final String type = e.getOption("type") != null ? e.getOption("type").getAsString() : null;
	    cmde.replyPrivate(cmde.success().setDescription(
		    StreamSupport.stream(collection.find().spliterator(), false)
		    .filter(d -> d.containsKey("guildid") || d.containsKey("userid"))
		    .filter(d -> type != null ? ((d.containsKey("guildid") && type.equals("guild")) || (d.containsKey("userid") && type.equals("user"))) : true)
		    .map(d -> "- " + (d.containsKey("guildid") ? "Guild: " + d.getLong("guildid") : "User: " + d.getLong("userid")) + (d.containsKey("reason") ? " | " + d.getString("reason") : ""))
		    .collect(Collectors.joining("\n"))));
	}
    }
}