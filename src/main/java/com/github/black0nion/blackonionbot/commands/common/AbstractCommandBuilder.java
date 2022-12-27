package com.github.black0nion.blackonionbot.commands.common;

import com.github.black0nion.blackonionbot.commands.Progress;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandBuilder;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;

public abstract class AbstractCommandBuilder<T extends AbstractCommandBuilder<?, ?>, D extends CommandData> {

	@SuppressWarnings("unchecked")
	private final T self = (T) this;

	private Category category = Category.OTHER;
	private Progress progress = Progress.DONE;
	private Permission[] requiredPermissions = Permission.EMPTY_PERMISSIONS;
	private Permission[] requiredBotPermissions = Permission.EMPTY_PERMISSIONS;
	private CustomPermission[] requiredCustomPermissions = {};
	private boolean isToggleable = true;
	private boolean shouldAutoRegister = true;
	private boolean isPremium = false;
	private boolean isEphemeral = false;
	/*
	 * If true, the command will only get registered in the admins' guild.
	 */
	private boolean adminGuild = false;
	private final D data;

	public AbstractCommandBuilder(final D data) {
		this.data = Objects.requireNonNull(data);
	}

	public D getData() {
		return data;
	}

	public Category getCategory() {
		return category;
	}

	public T setCategory(final Category category) {
		this.category = Objects.requireNonNullElse(category, Category.OTHER);
		return self;
	}

	public Progress getProgress() {
		return this.progress;
	}

	public T setProgress(final Progress progress) {
		this.progress = Objects.requireNonNullElse(progress, Progress.DONE);
		return self;
	}

	public boolean isVisible(final BlackUser user) {
		return user.hasPermission(this.requiredCustomPermissions);
	}

	/**
	 * Sets the required permissions for this command to {@link CustomPermission#ADMIN} and {@link SlashCommandBuilder#isEphemeral} to true.
	 */
	public T setHidden() {
		this.requiredCustomPermissions = new CustomPermission[] { CustomPermission.ADMIN };
		this.isEphemeral = true;
		return self;
	}

	public boolean isAdminGuild() {
		return adminGuild;
	}

	/**
	 * Only registers the command in the admins' guild and sets it hidden.
	 */
	public T setAdminGuild() {
		this.adminGuild = true;
		return this.setHidden();
	}

	public Permission[] getRequiredPermissions() {
		return this.requiredPermissions;
	}

	public T setRequiredPermissions(final Permission... requiredPermissions) {
		this.requiredPermissions = Objects.requireNonNullElse(requiredPermissions, Permission.EMPTY_PERMISSIONS);
		return self;
	}

	public Permission[] getRequiredBotPermissions() {
		return this.requiredBotPermissions;
	}

	public T setRequiredBotPermissions(final Permission... requiredBotPermissions) {
		this.requiredBotPermissions = Objects.requireNonNullElse(requiredBotPermissions, Permission.EMPTY_PERMISSIONS);
		return self;
	}

	public T permissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = permissions;
		return self;
	}

	public T addCustomPermissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = ArrayUtils.addAll(this.requiredCustomPermissions, permissions);
		return self;
	}

	public CustomPermission[] getRequiredCustomPermissions() {
		return this.requiredCustomPermissions;
	}

	public boolean isToggleable() {
		return this.isToggleable;
	}

	public T toggleable() {
		this.isToggleable = true;
		return self;
	}

	public T notToggleable() {
		this.isToggleable = false;
		return self;
	}

	public boolean shouldAutoRegister() {
		return this.shouldAutoRegister;
	}

	public T dontAutoRegister() {
		this.shouldAutoRegister = false;
		return self;
	}

	public T premiumRequired() {
		this.isPremium = true;
		return self;
	}

	public T setEphemeral(boolean ephemeral) {
		isEphemeral = ephemeral;
		return self;
	}

	public boolean isPremium() {
		return this.isPremium;
	}

	public boolean isEphemeral() {
		return isEphemeral;
	}
}
