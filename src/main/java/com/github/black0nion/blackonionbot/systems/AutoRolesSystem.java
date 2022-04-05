package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AutoRolesSystem extends ListenerAdapter {
  @Override
  public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
    final BlackGuild guild = BlackGuild.from(event.getGuild());
    final BlackUser user = BlackUser.from(event.getUser());

    final List<Long> autoroles = guild.getAutoRoles();
    final List<Long> removedRoles = new ArrayList<>();

    if (Utils.handleRights(guild, user, null, Permission.MANAGE_ROLES))
      return;

    for (final long roleid : autoroles) {
      final Role role = guild.getRoleById(roleid);
      if (role == null)
        removedRoles.add(roleid);
      else
        guild.addRoleToMember(user.getIdLong(), role).queue();
    }

    if (removedRoles.size() != 0) {
      autoroles.removeAll(removedRoles);
      guild.setAutoRoles(autoroles);
    }
  }
}
