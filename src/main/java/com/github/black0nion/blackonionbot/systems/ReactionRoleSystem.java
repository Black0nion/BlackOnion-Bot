package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import javassist.NotFoundException;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// TODO: test
public class ReactionRoleSystem extends ListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ReactionRoleSystem.class);

	public static final MongoCollection<Document> collection = MongoDB.getInstance().getDatabase().getCollection("reactionroles");

	@Override
	public void onMessageReactionAdd(final MessageReactionAddEvent e) {
		handle(e, e.getGuild()::addRoleToMember);
	}

	@Override
	public void onMessageReactionRemove(final MessageReactionRemoveEvent e) {
		handle(e, e.getGuild()::removeRoleFromMember);
	}

	private void handle(GenericMessageReactionEvent event, BiConsumer<UserSnowflake, Role> action) {
		if (event.getUser() == null || event.getUser().isBot()) return;

		final long guildid = event.getGuild().getIdLong();
		final long channelid = event.getChannel().getIdLong();
		final long messageid = event.getMessageIdLong();
		try {
			Role role = getRole(event, guildid, channelid, messageid);

			action.accept(event.getUser(), role);
		} catch (final IllegalStateException ex1) {
			logger.error("Unknown Emoji: '{}'", event.getReaction().getEmoji().getName());
		} catch (final NotFoundException ignored) {
			// ran when no reaction role is found
		}
	}

	private Role getRole(final GenericMessageReactionEvent e, long guildid, long channelid, long messageid) throws NotFoundException {
		String emote = e.getEmoji().getAsReactionCode();

		if (e.getEmoji().getType() == Emoji.Type.CUSTOM)
			emote = "<:" + emote + ">";

		final Document doc = collection.find(new BasicDBObject().append("guildid", guildid).append("channelid", channelid).append("messageid", messageid).append("emote", emote)).first();

		if (doc == null)
			throw new NotFoundException("Reaction entry");

		return e.getGuild().getRoleById(doc.getLong("roleid"));
	}
}