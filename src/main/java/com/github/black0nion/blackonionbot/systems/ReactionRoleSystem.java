package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;

public class ReactionRoleSystem extends ListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ReactionRoleSystem.class);

	@Override
	public void onMessageReactionAdd(@Nonnull final MessageReactionAddEvent e) {
		handle(e, e.getGuild()::addRoleToMember);
	}

	@Override
	public void onMessageReactionRemove(@Nonnull final MessageReactionRemoveEvent e) {
		handle(e, e.getGuild()::removeRoleFromMember);
	}

	@SQLSetup
	private static void setupDB() throws SQLException {
		SQLHelper.run("CREATE TABLE IF NOT EXISTS reaction_roles (" +
			"guild_id BIGINT NOT NULL, " +
			"role_id BIGINT NOT NULL, " +
			"channel_id BIGINT NOT NULL, " +
			"message_id BIGINT NOT NULL, " +
			"emoji VARCHAR(40) NOT NULL, " +
			"PRIMARY KEY (guild_id, channel_id, message_id, emoji, role_id));");
	}

	private void handle(GenericMessageReactionEvent event, BiConsumer<UserSnowflake, Role> action) {
		if (event.getUser() == null || event.getUser().isBot()) return;

		final long guildid = event.getGuild().getIdLong();
		final long channelid = event.getChannel().getIdLong();
		final long messageid = event.getMessageIdLong();
		try {
			Role role = getRole(event, guildid, channelid, messageid);
			if (role == null) return;

			action.accept(event.getUser(), role);
		} catch (final IllegalStateException ex1) {
			logger.error("Unknown Emoji: '{}'", event.getReaction().getEmoji().getName());
		}
	}

	// TODO: test
	private Role getRole(final GenericMessageReactionEvent e, long guildId, long channelID, long messageId) {
		String emoji = Utils.serializeEmoji(e.getEmoji());

		try (SQLHelper sq = new SQLHelper("SELECT role_id FROM reaction_roles WHERE guild_id = ? AND channel_id = ? AND message_id = ? AND emoji = ?")
					.addParameters(guildId, channelID, messageId, emoji);
				ResultSet rs = sq.executeQuery()) {
			if (rs.next()) {
				return e.getGuild().getRoleById(rs.getLong("role_id"));
			}
		} catch (SQLException ex) {
			logger.error("Error while querying a reactionrole entry", ex);
		}
		return null;
	}
}
