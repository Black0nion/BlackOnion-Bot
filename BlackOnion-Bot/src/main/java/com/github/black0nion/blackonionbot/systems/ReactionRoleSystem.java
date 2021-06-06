package com.github.black0nion.blackonionbot.systems;

import org.bson.Document;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionRoleSystem extends ListenerAdapter {
	
	public static final MongoCollection<Document> collection = MongoDB.botDatabase.getCollection("reactionroles");
	
	@Override
	public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent e) {
		if (!e.getUser().isBot()) {
			final long guildid = e.getGuild().getIdLong();
			final long channelid = e.getChannel().getIdLong();
			final long messageid = e.getMessageIdLong();
			try {
				String emote = e.getReactionEmote().getAsReactionCode();
				
				if (e.getReactionEmote().isEmote())
					emote = "<:" + emote + ">";
				
				final Document doc = collection.find(new BasicDBObject().append("guildid", guildid).append("channelid", channelid).append("messageid", messageid).append("emote", emote)).first();
				
				if (doc == null)
					return;
				
				e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(doc.getLong("roleid"))).queue();
			} catch (final IllegalStateException ex1) {
				System.out.println("Emoji nicht erkannt: " + e.getReactionEmote().getName());
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(final GuildMessageReactionRemoveEvent e) {
		if (!e.getUser().isBot()) {
			final long guildid = e.getGuild().getIdLong();
			final long channelid = e.getChannel().getIdLong();
			final long messageid = e.getMessageIdLong();
			try {
				String emote = e.getReactionEmote().getAsReactionCode();
				
				if (e.getReactionEmote().isEmote())
					emote = "<:" + emote + ">";
				
				final Document doc = collection.find(new BasicDBObject().append("guildid", guildid).append("channelid", channelid).append("messageid", messageid).append("emote", emote)).first();
				
				if (doc == null)
					return;
				
				e.getGuild().removeRoleFromMember(e.getMember(), e.getGuild().getRoleById(doc.getLong("roleid"))).queue();
			} catch (final IllegalStateException ex1) {
				System.out.println("Emoji nicht erkannt: " + e.getReactionEmote().getName());
			}
		}
	}
}
