package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Progress;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public abstract class Command {

	private String[] command = null;
	private String syntax = null;
	private Category category = null;
	private Progress progress = Progress.DONE;
	private Permission[] requiredPermissions = new Permission[] {};
	private Permission[] requiredBotPermissions = new Permission[] {};
	private CustomPermission[] requiredCustomPermissions;
	private boolean isToggleable = true;
	private boolean isDashboardCommand = true;
	private boolean shouldAutoRegister = true;
	private boolean isPremium = false;

	public abstract void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel);

	public String[] getCommand() {
		return this.command;
	}

	public Command setCommand(final String... command) {
		this.command = command;
		return this;
	}

	public String getSyntax() {
		return this.syntax;
	}

	public Command setSyntax(final String syntax) {
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

	public Command setCategory(final Category category) {
		this.category = Objects.requireNonNullElse(category, Category.OTHER);
		return this;
	}

	public Progress getProgress() {
		return this.progress;
	}

	public Command setProgress(final Progress progress) {
		this.progress = Objects.requireNonNullElse(progress, Progress.DONE);
		return this;
	}

	public boolean isVisible(final BlackUser user) {
		return user.hasPermission(this.requiredCustomPermissions);
	}

	public Command setHidden() {
		this.requiredCustomPermissions = new CustomPermission[] { CustomPermission.ADMIN };
		return this;
	}

	public Permission[] getRequiredPermissions() {
		return this.requiredPermissions;
	}

	public Command setRequiredPermissions(final Permission... requiredPermissions) {
		this.requiredPermissions = Objects.requireNonNullElseGet(requiredPermissions, () -> new Permission[] {});
		return this;
	}

	public Permission[] getRequiredBotPermissions() {
		return this.requiredBotPermissions;
	}

	public Command setRequiredBotPermissions(final Permission... requiredBotPermissions) {
		this.requiredBotPermissions = Objects.requireNonNullElseGet(requiredBotPermissions, () -> new Permission[] {});
		return this;
	}

	public Command setRequiredCustomPermissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = permissions;
		return this;
	}

	public CustomPermission[] getRequiredCustomPermissions() {
		return this.requiredCustomPermissions;
	}

	public boolean isToggleable() {
		return this.isToggleable;
	}

	public Command toggleable() {
		this.isToggleable = true;
		return this;
	}

	public Command notToggleable() {
		this.isToggleable = false;
		return this;
	}

	public boolean isDashboardCommand() {
		return this.isDashboardCommand;
	}

	public Command setDashboardCommand(final boolean isDashboardCommand) {
		this.isDashboardCommand = isDashboardCommand;
		return this;
	}

	public boolean shouldAutoRegister() {
		return this.shouldAutoRegister;
	}

	public Command dontAutoRegister() {
		this.shouldAutoRegister = false;
		return this;
	}

	public Command premiumRequired() {
		this.isPremium = true;
		return this;
	}

	public boolean isPremiumCommand() {
		return this.isPremium;
	}

	public Command setRequiredArgumentCount(int i) {
		// dummy coz i'm not done with migrating yet
		return this;
	}

	public int getRequiredArgumentCount() {
		return 1337;
	}

	// TODO: override toString
}