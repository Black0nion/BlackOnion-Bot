package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class TextCommand extends GenericCommand {

  private String[] command = null;
  private String syntax = null;
  private Category category = null;
  private Progress progress = Progress.DONE;
  private Permission[] requiredPermissions = new Permission[] {};
  private Permission[] requiredBotPermissions = new Permission[] {};
  private CustomPermission[] requiredCustomPermissions;
  private boolean isToggleable = true;
  private boolean isDashboardCommand = true;
  private boolean isPremium = false;

  public abstract void execute(final String[] args, final CommandEvent cmde,
      final MessageReceivedEvent e, final Message message, final BlackMember member,
      final BlackUser author, final BlackGuild guild, final TextChannel channel);

  public String[] getCommand() {
    return this.command;
  }

  @Override
  public String getName() {
    return this.command[0];
  }

  public TextCommand setCommand(final String... command) {
    this.command = command;
    return this;
  }

  public String getSyntax() {
    return this.syntax;
  }

  public TextCommand setSyntax(final String syntax) {
    if (syntax == null) {
      this.syntax = "";
    } else {
      this.syntax = syntax;
    }
    return this;
  }

  public Category getCategory() {
    return this.category;
  }

  public TextCommand setCategory(final Category category) {
    this.category = Objects.requireNonNullElse(category, Category.OTHER);
    return this;
  }

  public Progress getProgress() {
    return this.progress;
  }

  public TextCommand setProgress(final Progress progress) {
    this.progress = Objects.requireNonNullElse(progress, Progress.DONE);
    return this;
  }

  public boolean isVisible(final BlackUser user) {
    return user.hasPermission(this.requiredCustomPermissions);
  }

  public TextCommand setHidden() {
    this.requiredCustomPermissions = new CustomPermission[] {CustomPermission.ADMIN};
    return this;
  }

  public Permission[] getRequiredPermissions() {
    return this.requiredPermissions;
  }

  public TextCommand setRequiredPermissions(final Permission... requiredPermissions) {
    this.requiredPermissions =
        Objects.requireNonNullElseGet(requiredPermissions, () -> new Permission[] {});
    return this;
  }

  public Permission[] getRequiredBotPermissions() {
    return this.requiredBotPermissions;
  }

  public TextCommand setRequiredBotPermissions(final Permission... requiredBotPermissions) {
    this.requiredBotPermissions =
        Objects.requireNonNullElseGet(requiredBotPermissions, () -> new Permission[] {});
    return this;
  }

  public TextCommand setRequiredCustomPermissions(final CustomPermission... permissions) {
    this.requiredCustomPermissions = permissions;
    return this;
  }

  public CustomPermission[] getRequiredCustomPermissions() {
    return this.requiredCustomPermissions;
  }

  @Override
  public boolean isToggleable() {
    return this.isToggleable;
  }

  public TextCommand toggleable() {
    this.isToggleable = true;
    return this;
  }

  public TextCommand notToggleable() {
    this.isToggleable = false;
    return this;
  }

  public boolean isDashboardCommand() {
    return this.isDashboardCommand;
  }

  public TextCommand setDashboardCommand(final boolean isDashboardCommand) {
    this.isDashboardCommand = isDashboardCommand;
    return this;
  }

  public TextCommand premiumRequired() {
    this.isPremium = true;
    return this;
  }

  public boolean isPremiumCommand() {
    return this.isPremium;
  }

  public TextCommand setRequiredArgumentCount(int i) {
    // dummy coz I'm not done with migrating yet
    return this;
  }

  public int getRequiredArgumentCount() {
    return 0;
  }

  // TODO: override toString
}
