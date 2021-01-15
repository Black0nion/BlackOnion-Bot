package com.github.black0nion.blackonionbot.systems;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.black0nion.blackonionbot.SQL.LiteSQL;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SelfRoleSystem extends ListenerAdapter
{
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		if (!e.getUser().isBot()) {
			long guildid = e.getGuild().getIdLong();
			long channelid = e.getChannel().getIdLong();
			long messageid = e.getMessageIdLong();
			try {
				String emote = e.getReactionEmote().getName() + ":" + e.getReactionEmote().getId();
				ResultSet set = LiteSQL.onQuery("SELECT roleid FROM reactroles WHERE guildid = " + guildid + " AND channelid = " + channelid + " AND messageid = " + messageid + " AND emote = '" + emote + "'");
				
				try {
					if (set.next()) {
						long roleid = set.getLong("roleid");
						
						e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(roleid)).queue();
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			} catch (IllegalStateException ex1) {
				System.out.println("Emoji nicht erkannt: " + e.getReactionEmote().getName());
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		try {
		long guildid = e.getGuild().getIdLong();
		long channelid = e.getChannel().getIdLong();
		long messageid = e.getMessageIdLong();
		String emote = e.getReactionEmote().getName() + ":" + e.getReactionEmote().getId();
		
		ResultSet set = LiteSQL.onQuery("SELECT roleid FROM reactroles WHERE guildid = " + guildid + " AND channelid = " + channelid + " AND messageid = " + messageid + " AND emote = '" + emote + "'");
		if (set.next()) {
			long roleid = set.getLong("roleid");
			
			e.getGuild().removeRoleFromMember(e.getUserIdLong(), e.getGuild().getRoleById(roleid)).queue();
		}
		} catch (Exception ex) {
			if (ex instanceof SQLException)
				ex.printStackTrace();
		}
	}
}
