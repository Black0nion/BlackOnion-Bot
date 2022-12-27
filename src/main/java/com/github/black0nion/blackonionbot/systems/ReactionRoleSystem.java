package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class ReactionRoleSystem extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ReactionRoleSystem.class);

	private final SQLHelperFactory sql;

	public ReactionRoleSystem(SQLHelperFactory factory) {
		this.sql = factory;
	}

	@Override
	public void onMessageReactionAdd(@Nonnull final MessageReactionAddEvent e) {
		handle(e, e.getGuild()::addRoleToMember);
	}

	@Override
	public void onMessageReactionRemove(@Nonnull final MessageReactionRemoveEvent e) {
		handle(e, e.getGuild()::removeRoleFromMember);
	}

	@SQLSetup
	private static void setupDB(SQLHelperFactory sql) throws SQLException {
		sql.run("CREATE TABLE IF NOT EXISTS reaction_roles (" +
			"id SERIAL, " +
			"guild_id BIGINT NOT NULL, " +
			"role_id BIGINT NOT NULL, " +
			"channel_id BIGINT NOT NULL, " +
			"message_id BIGINT NOT NULL, " +
			"emoji VARCHAR(40) NOT NULL, " +
			"PRIMARY KEY (guild_id, channel_id, message_id, emoji, role_id));");
	}

	private void handle(GenericMessageReactionEvent event, BiFunction<UserSnowflake, Role, RestAction<?>> action) {
		if (event.getUser() == null || event.getUser().isBot()) return;

		final long guildId = event.getGuild().getIdLong();
		final long channelId = event.getChannel().getIdLong();
		final long messageId = event.getMessageIdLong();
		try {
			List<Role> roles = getRoles(event, guildId, channelId, messageId);

			for (Role r : roles) {
				action.apply(event.getUser(), r).queue();
			}
		} catch (final IllegalStateException ex) {
			logger.error("Unknown Emoji: '{}'", event.getReaction().getEmoji().getName(), ex);
		}
	}

	private List<Role> getRoles(final GenericMessageReactionEvent e, long guildId, long channelID, long messageId) {
		String emoji = Utils.serializeEmoji(e.getEmoji());

		try (SQLHelper sq = sql.create("SELECT role_id FROM reaction_roles WHERE guild_id = ? AND channel_id = ? AND message_id = ? AND emoji = ?")
				.addParameters(guildId, channelID, messageId, emoji);
				ResultSet rs = sq.executeQuery()) {
			if (!rs.next()) return Collections.emptyList();

			List<Role> result = new LinkedList<>();
			do {
				result.add(e.getGuild().getRoleById(rs.getLong("role_id")));
			} while (rs.next());
			return result;
		} catch (SQLException ex) {
			logger.error("Error while querying a reactionrole entry", ex);
		}
		return Collections.emptyList();
	}
}
