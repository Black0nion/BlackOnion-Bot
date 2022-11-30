package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.wrappers.StartsWithArrayList;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A builder class for creating a {@link SlashCommand}.<br>
 * Handles the creation of the command and its properties like required permissions, if the command is toggleable or the required {@link GuildType GuildType}.
 */
@SuppressWarnings("unused")
public class SlashCommandBuilder {

	private final SlashCommandData data;
	private Category category = Category.OTHER;
	private Progress progress = Progress.DONE;
	private Permission[] requiredPermissions = Permission.EMPTY_PERMISSIONS;
	private Permission[] requiredBotPermissions = Permission.EMPTY_PERMISSIONS;
	private CustomPermission[] requiredCustomPermissions = {};
	private boolean isToggleable = true;
	private boolean shouldAutoRegister = true;
	private boolean isPremium = false;
	private boolean isEphemeral = false;
	/**
	 * If true, the command will only get registered in the admins' guild.
	 */
	private boolean adminGuild = false;
	private final HashMap<String, StartsWithArrayList> autoComplete = new HashMap<>();

	public SlashCommandBuilder autocomplete(String command, Collection<String> values) {
		this.autoComplete.put(command, new StartsWithArrayList(values));
		return this;
	}

	public SlashCommandBuilder(final SlashCommandData data) {
		this.data = Objects.requireNonNull(data);
	}

	public SlashCommandData getData() {
		return this.data;
	}

	public Category getCategory() {
		return this.category;
	}

	public SlashCommandBuilder setCategory(final Category category) {
		this.category = Objects.requireNonNullElse(category, Category.OTHER);
		return this;
	}

	public Progress getProgress() {
		return this.progress;
	}

	public SlashCommandBuilder setProgress(final Progress progress) {
		this.progress = Objects.requireNonNullElse(progress, Progress.DONE);
		return this;
	}

	public boolean isVisible(final BlackUser user) {
		return user.hasPermission(this.requiredCustomPermissions);
	}

	/**
	 * Sets the required permissions for this command to {@link CustomPermission#ADMIN} and {@link SlashCommandBuilder#isEphemeral} to true.
	 */
	public SlashCommandBuilder setHidden() {
		this.requiredCustomPermissions = new CustomPermission[] { CustomPermission.ADMIN };
		this.setEphemeral(true);
		return this;
	}

	public boolean isAdminGuild() {
		return adminGuild;
	}

	/**
	 * Only registers the command in the admins' guild and sets it hidden.
	 */
	public SlashCommandBuilder setAdminGuild() {
		this.adminGuild = true;
		return this.setHidden();
	}

	public Permission[] getRequiredPermissions() {
		return this.requiredPermissions;
	}

	public SlashCommandBuilder setRequiredPermissions(final Permission... requiredPermissions) {
		this.requiredPermissions = Objects.requireNonNullElse(requiredPermissions, Permission.EMPTY_PERMISSIONS);
		return this;
	}

	public Permission[] getRequiredBotPermissions() {
		return this.requiredBotPermissions;
	}

	public SlashCommandBuilder setRequiredBotPermissions(final Permission... requiredBotPermissions) {
		this.requiredBotPermissions = Objects.requireNonNullElse(requiredBotPermissions, Permission.EMPTY_PERMISSIONS);
		return this;
	}

	public SlashCommandBuilder permissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = permissions;
		return this;
	}

	public SlashCommandBuilder addCustomPermissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = ArrayUtils.addAll(this.requiredCustomPermissions, permissions);
		return this;
	}

	public CustomPermission[] getRequiredCustomPermissions() {
		return this.requiredCustomPermissions;
	}

	public boolean isToggleable() {
		return this.isToggleable;
	}

	public SlashCommandBuilder toggleable() {
		this.isToggleable = true;
		return this;
	}

	public SlashCommandBuilder notToggleable() {
		this.isToggleable = false;
		return this;
	}

	public boolean shouldAutoRegister() {
		return this.shouldAutoRegister;
	}

	public SlashCommandBuilder dontAutoRegister() {
		this.shouldAutoRegister = false;
		return this;
	}

	public SlashCommandBuilder premiumRequired() {
		this.isPremium = true;
		return this;
	}

	public boolean isPremium() {
		return this.isPremium;
	}

	public SlashCommandBuilder setEphemeral() {
		return this.setEphemeral(true);
	}

	@SuppressWarnings("UnusedReturnValue")
	public SlashCommandBuilder setEphemeral(boolean ephemeral) {
		this.isEphemeral = ephemeral;
		return this;
	}

	public boolean isEphemeral() {
		return this.isEphemeral;
	}

	public Map<String, StartsWithArrayList> getAutoComplete() {
		return autoComplete;
	}
}
