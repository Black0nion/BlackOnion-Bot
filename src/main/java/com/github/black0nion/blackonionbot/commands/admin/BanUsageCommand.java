package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.database.DatabaseConnector;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class BanUsageCommand extends SlashCommand {

	private static final SubcommandData[] DATA = {
		new SubcommandData("user", "(un)ban a user").addOption(OptionType.USER, "targetuser", "user to (un)ban", true),
		new SubcommandData("guild", "(un)ban a guild").addOption(OptionType.STRING, "targetguild", "guild to (un)ban", true),
	};

	private final SQLHelperFactory sql;

	public BanUsageCommand(SQLHelperFactory sql) {
		super(builder(
			Commands.slash("usage", "(un)ban a user or a guild from using commands.")
				.addSubcommandGroups(
					new SubcommandGroupData("ban", "Ban a user or a guild from using commands.").addSubcommands(DATA),
					new SubcommandGroupData("unban", "Unban a user or a guild from using commands.").addSubcommands(DATA)))
			.setAdminGuild()
			.permissions(CustomPermission.BAN_USAGE)
		);
		this.sql = sql;
	}

	private static final Pattern GUILD_ID_PATTERN = Pattern.compile("^\\d{17,18}$");

	@SQLSetup
	private static void setup(SQLHelperFactory sql) throws SQLException {
		sql.run("DO $$ BEGIN " +
			"CREATE TYPE user_or_guild AS ENUM ('user', 'guild');" +
			"EXCEPTION WHEN duplicate_object THEN null;" +
			"END $$;"
		);
		sql.run("CREATE TABLE IF NOT EXISTS banned_entities (id BIGINT PRIMARY KEY, type user_or_guild NOT NULL)");
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild executedGuild, TextChannel channel) throws SQLException {
		final User user = e.getOption("targetuser", OptionMapping::getAsUser);
		final String guildId = e.getOption("targetguild", OptionMapping::getAsString);

		if (user == null && guildId == null) {
			cmde.send("wrongargumentcount");
			return;
		}

		if (user != null) {
			if (cmde.getSubcommandGroup().equalsIgnoreCase("ban")) {
				if (sql.run("INSERT INTO banned_entities (id, type) VALUES (?, 'user') ON CONFLICT DO NOTHING", user.getIdLong())) {
					cmde.send("cantusecommandsanymore", new Placeholder("userorguild", Utils.escapeMarkdown(user.getAsTag())));
				} else {
					cmde.exception();
				}
			} else {
				if (sql.run("DELETE FROM banned_entities WHERE id = ? AND type = 'user'", user.getIdLong())) {
					cmde.send("userunbannednoreason");
				} else {
					cmde.send("erroroccurred");
					LoggerFactory.getLogger(BanUsageCommand.class).error("Failed to unban user {}", user.getIdLong());
				}
			}
		}

		if (guildId == null) return;

		if (!GUILD_ID_PATTERN.matcher(guildId).matches()) {
			cmde.send("invalidguildid");
			return;
		}

		if (cmde.getSubcommandGroup().equalsIgnoreCase("ban")) {
			// TODO: test behaviour of ON CONFLICT DO NOTHING
			if (sql.run("INSERT INTO banned_entities (id, type) VALUES (?, 'guild') ON CONFLICT DO NOTHING", guildId))
				cmde.send("cantusecommandsanymore", new Placeholder("userorguild", guildId));
			else
				cmde.exception();
		} else {
			if (sql.run("DELETE FROM banned_entities WHERE id = ? AND type = 'guild'", guildId)) {
				cmde.send("guildunbanned");
			} else {
				cmde.exception();
				LoggerFactory.getLogger(BanUsageCommand.class).error("Failed to unban guild {}", guildId);
			}
		}
	}

	// TODO: implement caching
	public static boolean isBanned(DatabaseConnector connector, long guildID, long userID) {
		try {
			try (SQLHelper sq = new SQLHelper(connector::getLowPriorityConnection, "SELECT * FROM banned_entities WHERE (id = ? AND type = 'user') OR (id = ? AND type = 'guild')")
					.addParameters(userID, guildID)) {
				return sq.anyMatch();
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(BanUsageCommand.class).error("Failed to check if user " + userID + " or guild " + guildID + " are banned!", e);
			return false;
		}
	}
}
