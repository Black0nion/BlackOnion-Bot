package com.github.black0nion.blackonionbot.commands;

import java.util.Arrays;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.misc.Progress;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class Command {
	
	private String[] command = null;
	private String syntax = null;
	private Category category = Category.OTHER;
	private Progress progress = Progress.DONE;
	private CommandVisibility visibility = CommandVisibility.SHOWN;
	private int requiredArgumentCount = 0;
	private Permission[] requiredPermissions = new Permission[] {};
	private Permission[] requiredBotPermissions = new Permission[] {};
	private boolean requiresBotAdmin = false;
	private boolean isToggleable = true;
	private boolean isDashboardCommand = true;
	private boolean shouldAutoRegister = true;

	public abstract void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel);

	public String[] getCommand() {
		return command;
	}

	public Command setCommand(String... command) {
		this.command = command;
		return this;
	}

	public String getSyntax() {
		return syntax;
	}

	public Command setSyntax(String syntax) {
		if (syntax == null) this.syntax = "";
		else this.syntax = syntax;
		return this;
	}

	public Category getCategory() {
		return category;
	}

	public Command setCategory(Category category) {
		if (category == null) this.category = Category.OTHER;
		else this.category = category;
		return this;
	}

	public Progress getProgress() {
		return progress;
	}

	public Command setProgress(Progress progress) {
		if (progress == null) this.progress = Progress.DONE;
		else this.progress = progress;
		return this;
	}
	
	public CommandVisibility getVisibility() {
		return visibility;
	}
	
	public boolean isVisible() {
		return visibility == CommandVisibility.SHOWN;
	}
	
	public Command setVisibility(CommandVisibility visibility) {
		if (visibility == null) this.visibility = CommandVisibility.HIDDEN;
		else this.visibility = visibility;
		return this;
	}
	
	public Command setHidden() {
		this.visibility = CommandVisibility.HIDDEN;
		return this;
	}

	public int getRequiredArgumentCount() {
		return requiredArgumentCount;
	}

	public Command setRequiredArgumentCount(int requiredArgumentCount) {
		this.requiredArgumentCount = requiredArgumentCount;
		return this;
	}

	public Permission[] getRequiredPermissions() {
		return requiredPermissions;
	}

	public Command setRequiredPermissions(Permission... requiredPermissions) {
		if (requiredPermissions == null) this.requiredPermissions = new Permission[] {};
		else this.requiredPermissions = requiredPermissions;
		return this;
	}

	public Permission[] getRequiredBotPermissions() {
		return requiredBotPermissions;
	}

	public Command setRequiredBotPermissions(Permission... requiredBotPermissions) {
		if (requiredBotPermissions == null) this.requiredBotPermissions = new Permission[] {};
		else this.requiredBotPermissions = requiredBotPermissions;
		return this;
	}

	public boolean requiresBotAdmin() {
		return requiresBotAdmin;
	}

	public Command requiresBotAdmin(boolean requiresBotAdmin) {
		this.requiresBotAdmin = requiresBotAdmin;
		return this;
	}
	
	/**
	 * Sets requiredBotAdmin to true
	 * @return self
	 */
	public Command botAdminRequired() {
		this.requiresBotAdmin(true);
		return this;
	}

	public boolean isToggleable() {
		return isToggleable;
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
		return isDashboardCommand;
	}

	public Command setDashboardCommand(boolean isDashboardCommand) {
		this.isDashboardCommand = isDashboardCommand;
		return this;
	}
	
	public boolean shouldAutoRegister() {
		return this.shouldAutoRegister;
	}
	
	public void dontAutoRegister() {
		this.shouldAutoRegister = false;
	}

	@Override
	public String toString() {
		return "Command [command=" + Arrays.toString(command) + ", syntax=" + syntax + ", category=" + category
				+ ", progress=" + progress + ", visibility=" + visibility + ", requiredArgumentCount="
				+ requiredArgumentCount + ", requiredPermissions=" + Arrays.toString(requiredPermissions)
				+ ", requiredBotPermissions=" + Arrays.toString(requiredBotPermissions) + ", requiresBotAdmin="
				+ requiresBotAdmin + ", isToggleable=" + isToggleable + ", isDashboardCommand=" + isDashboardCommand
				+ ", shouldAutoRegister=" + shouldAutoRegister + "]";
	}
}