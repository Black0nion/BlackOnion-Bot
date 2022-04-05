package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PermissionsCommand extends SlashCommand {

  private static final OptionData userOption =
      new OptionData(OptionType.USER, "user", "The user to modify the permissions of", true);

  public PermissionsCommand() {
    super(
        builder(
            Commands.slash("permissions", "Set the CustomPermissions of a user").addSubcommands(
                new SubcommandData("add", "Add permissions to the user").addOptions(userOption,
                    new OptionData(OptionType.STRING, "permission", "The permission to remove",
                        true)
                            .addChoices(Arrays.stream(CustomPermission.values())
                                .map(perm -> new Command.Choice(perm.name(), perm.name()))
                                .toList())),
                new SubcommandData("remove",
                    "Remove permissions from the user")
                        .addOptions(userOption,
                            new OptionData(OptionType.STRING, "permission",
                                "The permission to remove", true)
                                    .addChoices(Utils.add(
                                        Arrays.stream(CustomPermission.values())
                                            .map(perm -> new Command.Choice(perm.name(),
                                                perm.name()))
                                            .collect(Collectors.toList()),
                                        new Command.Choice("All permissions", "all")))),
                new SubcommandData("list", "List the permissions of the user").addOption(
                    OptionType.USER, "user", "The user to list the permissions of", true)))
                        .setAdminGuild());
  }

  @Override
  public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member,
      BlackUser author, BlackGuild guild, TextChannel channel) {
    final String mode = e.getSubcommandName();
    if (mode == null || mode.isEmpty()) {
      cmde.exception();
      return;
    }
    if (Utils.equalsOneIgnoreCase(mode, "add", "remove", "list")) {
      final BlackUser user =
          BlackUser.from(Objects.requireNonNull(e.getOption("user", OptionMapping::getAsUser)));
      if (user.isBot()) {
        cmde.send("invaliduser");
        return;
      }
      if (mode.equalsIgnoreCase("list")) {
        final List<CustomPermission> perms = user.getPermissions();
        cmde.send("permissionsof", new Placeholder("user", user.getFullNameEscaped()),
            new Placeholder("perms", perms.size() != 0 ? perms.stream().map(CustomPermission::name)
                .map(Utils::list).collect(Collectors.joining("\n")) : "empty"));
      } else {
        String permissionString = e.getOption("permission", OptionMapping::getAsString);
        CustomPermission permission = CustomPermission.parse(permissionString);
        if (mode.equalsIgnoreCase("remove")) {
          if (permission == null && !Objects.equals(permissionString, "all"))
            throw new NullPointerException("CustomPermission is null!");
          if (permission != null)
            user.removePermissions(permission);
          else
            user.setPermissions(new ArrayList<>());
          cmde.send("permissionsremoved", new Placeholder("perms", permissionString),
              new Placeholder("user", user.getFullName()));
        } else if (mode.equalsIgnoreCase("add")) {
          if (permission == null)
            throw new NullPointerException("CustomPermission is null!");
          user.addPermissions(permission);
          cmde.send("permissionsadded", new Placeholder("perms", permissionString),
              new Placeholder("user", user.getFullName()));
        }
      }
    } else {
      cmde.sendPleaseUse();
    }
  }
}
