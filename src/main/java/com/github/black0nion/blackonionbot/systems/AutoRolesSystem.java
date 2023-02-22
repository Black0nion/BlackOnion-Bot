package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.ListSetting;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashSet;
import java.util.Set;

public class AutoRolesSystem extends ListenerAdapter {

	private final LanguageSystem languageSystem;
	private final GuildSettingsRepo guildSettingsRepo;

	public AutoRolesSystem(LanguageSystem languageSystem, GuildSettingsRepo guildSettingsRepo) {
		this.languageSystem = languageSystem;
		this.guildSettingsRepo = guildSettingsRepo;
	}

	@Override
	public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackUser user = BlackUser.from(event.getUser());
		final GuildSettings guildSettings = guildSettingsRepo.getSettings(guild);

		final ListSetting<Long, Set<Long>> autoroles = guildSettings.getAutoRoles();
		final Set<Long> removedRoles = new HashSet<>();

		if (Utils.handleSelfRights(languageSystem, guild, guildSettings, user, null, null, null, Permission.MANAGE_ROLES)) return;

		for (final long roleid : autoroles.getValue()) {
			final Role role = guild.getRoleById(roleid);
			if (role == null)
				removedRoles.add(roleid);
			else
				guild.addRoleToMember(user, role).queue();
		}

		if (!removedRoles.isEmpty()) {
			autoroles.removeAll(removedRoles);
		}
	}
}
