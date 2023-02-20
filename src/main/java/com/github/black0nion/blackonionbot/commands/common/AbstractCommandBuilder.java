package com.github.black0nion.blackonionbot.commands.common;

import com.github.black0nion.blackonionbot.commands.Progress;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandBuilder;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractCommandBuilder<T extends AbstractCommandBuilder<?, ?>, D extends CommandData> {

	@SuppressWarnings("unchecked")
	private final T self = (T) this;

	private Category category = Category.OTHER;
	private Progress progress = Progress.DONE;
	private Set<Permission> requiredPermissions = EnumSet.noneOf(Permission.class);
	private Set<Permission> requiredBotPermissions = EnumSet.noneOf(Permission.class);
	private Set<CustomPermission> requiredCustomPermissions = EnumSet.noneOf(CustomPermission.class);
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

	/**
	 * Sets the required permissions for this command to {@link CustomPermission#ADMIN} and {@link SlashCommandBuilder#isEphemeral} to true.
	 */
	public T setHidden() {
		this.requiredCustomPermissions = EnumSet.of(CustomPermission.ADMIN);
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

	public Set<Permission> getRequiredPermissions() {
		return this.requiredPermissions;
	}

	public T setRequiredPermissions(final Permission... requiredPermissions) {
		if (requiredPermissions != null) this.requiredPermissions = EnumSet.copyOf(this.requiredPermissions);
		else this.requiredPermissions = EnumSet.noneOf(Permission.class);
		return self;
	}

	public Set<Permission> getRequiredBotPermissions() {
		return this.requiredBotPermissions;
	}

	public T setRequiredBotPermissions(final Permission... requiredBotPermissions) {
		if (requiredBotPermissions != null) this.requiredBotPermissions = EnumSet.copyOf(this.requiredBotPermissions);
		else this.requiredBotPermissions = EnumSet.noneOf(Permission.class);
		return self;
	}

	public T permissions(final CustomPermission... permissions) {
		if (permissions != null) this.requiredCustomPermissions = EnumSet.copyOf(this.requiredCustomPermissions);
		else this.requiredCustomPermissions = EnumSet.noneOf(CustomPermission.class);
		return self;
	}

	public T addCustomPermissions(final CustomPermission... permissions) {
		if (permissions != null) Collections.addAll(this.requiredCustomPermissions, permissions);
		else this.requiredCustomPermissions = EnumSet.noneOf(CustomPermission.class);
		return self;
	}

	public Set<CustomPermission> getRequiredCustomPermissions() {
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
