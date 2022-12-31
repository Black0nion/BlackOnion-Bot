package com.github.black0nion.blackonionbot.systems;

import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoRolesSystem extends ListenerAdapter {

	private final LanguageSystem languageSystem;

	public AutoRolesSystem(LanguageSystem languageSystem) {
		this.languageSystem = languageSystem;
	}

	@Override
	public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackUser user = BlackUser.from(event.getUser());

		final List<Long> autoroles = guild.getAutoRoles();
		final List<Long> removedRoles = new ArrayList<>();

		if (Utils.handleSelfRights(languageSystem, guild, user, null, null, Permission.MANAGE_ROLES)) return;

		for (final long roleid : autoroles) {
			final Role role = guild.getRoleById(roleid);
			if (role == null)
				removedRoles.add(roleid);
			else
				guild.addRoleToMember(user, role).queue();
		}

		if (!removedRoles.isEmpty()) {
			autoroles.removeAll(removedRoles);
			guild.setAutoRoles(autoroles);
		}
 	}
}
