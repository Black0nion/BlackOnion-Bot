package com.github.black0nion.blackonionbot.systems;

import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoRolesSystem extends ListenerAdapter {
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		final Guild guild = event.getGuild();
		final User user = event.getUser();
		
		List<Long> autoroles = GuildManager.getList(guild.getId(), "autoroles", new ArrayList<Long>(), Long.class);
		List<Long> removedRoles = new ArrayList<>();
		
		for (long roleid : autoroles) {
			Role role = guild.getRoleById(roleid);
			if (role == null)
				removedRoles.add(roleid);
			else
				guild.addRoleToMember(user.getIdLong(), role).queue();
		}
		
		if (removedRoles.size() != 0)
			GuildManager.saveList(guild.getId(), "autoroles", removedRoles);
 	}
}
