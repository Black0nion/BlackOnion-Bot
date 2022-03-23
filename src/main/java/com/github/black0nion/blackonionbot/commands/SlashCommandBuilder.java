package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.wrappers.StartsWithArrayList;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.lang.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings("unused")
public class SlashCommandBuilder {

	private final SlashCommandData data;
	private Category category = Category.OTHER;
	private Progress progress = Progress.DONE;
	private Permission[] requiredPermissions = Utils.EMPTY_PERMISSIONS;
	private Permission[] requiredBotPermissions = Utils.EMPTY_PERMISSIONS;
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

	@Nonnull
	public static SlashCommandBuilder builder(@Nonnull SlashCommandData data) {
		return new SlashCommandBuilder(data);
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

	public SlashCommandBuilder setHidden() {
		this.requiredCustomPermissions = new CustomPermission[] { CustomPermission.ADMIN };
		this.setEphemeral(true);
		return this;
	}

	public boolean isAdminGuild() {
		return adminGuild;
	}

	public SlashCommandBuilder setAdminGuild() {
		this.setHidden();
		this.adminGuild = true;
		return this;
	}

	public Permission[] getRequiredPermissions() {
		return this.requiredPermissions;
	}

	public SlashCommandBuilder setRequiredPermissions(final Permission... requiredPermissions) {
		this.requiredPermissions = Objects.requireNonNullElseGet(requiredPermissions, () -> new Permission[] {});
		return this;
	}

	public Permission[] getRequiredBotPermissions() {
		return this.requiredBotPermissions;
	}

	public SlashCommandBuilder setRequiredBotPermissions(final Permission... requiredBotPermissions) {
		this.requiredBotPermissions = Objects.requireNonNullElseGet(requiredBotPermissions, () -> new Permission[] {});
		return this;
	}

	public SlashCommandBuilder permissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = permissions;
		return this;
	}

	public SlashCommandBuilder setRequiredCustomPermissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = permissions;
		return this;
	}

	public SlashCommandBuilder addCustomPermissions(final CustomPermission... permissions) {
		this.requiredCustomPermissions = (CustomPermission[]) ArrayUtils.addAll(this.requiredCustomPermissions, permissions);
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

	public HashMap<String, StartsWithArrayList> getAutoComplete() {
		return autoComplete;
	}
}