package com.github.black0nion.blackonionbot.systems;

import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoRolesSystem extends ListenerAdapter {
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {		
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackUser user = BlackUser.from(event.getUser());
		
		List<Long> autoroles = guild.getList("autoroles", Long.class);
		List<Long> removedRoles = new ArrayList<>();
		
		if (Utils.handleRights(guild, user, null, Permission.MANAGE_ROLES)) return;
		
		for (long roleid : autoroles) {
			Role role = guild.getRoleById(roleid);
			if (role == null)
				removedRoles.add(roleid);
			else
				guild.addRoleToMember(user.getIdLong(), role).queue();
		}
		
		if (removedRoles.size() != 0) {
			autoroles.removeAll(removedRoles);
			guild.saveList("autoroles", removedRoles);
		}
 	}
}
