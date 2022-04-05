package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.util.Objects;

// TODO: extract common part
public class ReactionRoleSystem extends ListenerAdapter {

  public static final MongoCollection<Document> collection =
      MongoDB.DATABASE.getCollection("reactionroles");

  @Override
  public void onMessageReactionAdd(final MessageReactionAddEvent e) {
    if (!Objects.requireNonNull(e.getUser()).isBot()) {
      final long guildid = e.getGuild().getIdLong();
      final long channelid = e.getChannel().getIdLong();
      final long messageid = e.getMessageIdLong();
      try {
        String emote = e.getReactionEmote().getAsReactionCode();

        if (e.getReactionEmote().isEmote())
          emote = "<:" + emote + ">";

        final Document doc = collection.find(new BasicDBObject().append("guildid", guildid)
            .append("channelid", channelid).append("messageid", messageid).append("emote", emote))
            .first();

        if (doc == null)
          return;

        e.getGuild().addRoleToMember(Objects.requireNonNull(e.getMember()),
            Objects.requireNonNull(e.getGuild().getRoleById(doc.getLong("roleid")))).queue();
      } catch (final IllegalStateException ex1) {
        System.out.println("Emoji nicht erkannt: " + e.getReactionEmote().getName());
      }
    }
  }

  @Override
  public void onMessageReactionRemove(final MessageReactionRemoveEvent e) {
    if (!Objects.requireNonNull(e.getUser()).isBot()) {
      final long guildid = e.getGuild().getIdLong();
      final long channelid = e.getChannel().getIdLong();
      final long messageid = e.getMessageIdLong();
      try {
        String emote = e.getReactionEmote().getAsReactionCode();

        if (e.getReactionEmote().isEmote())
          emote = "<:" + emote + ">";

        final Document doc = collection.find(new BasicDBObject().append("guildid", guildid)
            .append("channelid", channelid).append("messageid", messageid).append("emote", emote))
            .first();

        if (doc == null)
          return;

        e.getGuild().removeRoleFromMember(Objects.requireNonNull(e.getMember()),
            Objects.requireNonNull(e.getGuild().getRoleById(doc.getLong("roleid")))).queue();
      } catch (final IllegalStateException ex1) {
        System.out.println("Emoji nicht erkannt: " + e.getReactionEmote().getName());
      }
    }
  }
}
