package com.github.black0nion.blackonionbot.commands;

import java.util.Arrays;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Progress;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class SlashCommand {

    private CommandData data;
    private Category category = Category.OTHER;
    private Progress progress = Progress.DONE;
    private Permission[] requiredPermissions = new Permission[] {};
    private Permission[] requiredBotPermissions = new Permission[] {};
    private CustomPermission[] requiredCustomPermissions;
    private boolean isToggleable = true;
    private boolean shouldAutoRegister = true;
    private boolean isPremium = false;

    public abstract void execute(final com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel);

    public CommandData getData() {
	return this.data;
    }

    public void setData(final CommandData data) {
	this.data = data;
    }

    public Category getCategory() {
	return this.category;
    }

    public SlashCommand setCategory(final Category category) {
	if (category == null) {
	    this.category = Category.OTHER;
	} else {
	    this.category = category;
	}
	return this;
    }

    public Progress getProgress() {
	return this.progress;
    }

    public SlashCommand setProgress(final Progress progress) {
	if (progress == null) {
	    this.progress = Progress.DONE;
	} else {
	    this.progress = progress;
	}
	return this;
    }

    public boolean isVisible(final BlackUser user) {
	return user.hasPermission(this.requiredCustomPermissions);
    }

    public SlashCommand setHidden() {
	this.requiredCustomPermissions = new CustomPermission[] { CustomPermission.ADMIN };
	return this;
    }

    public Permission[] getRequiredPermissions() {
	return this.requiredPermissions;
    }

    public SlashCommand setRequiredPermissions(final Permission... requiredPermissions) {
	if (requiredPermissions == null) {
	    this.requiredPermissions = new Permission[] {};
	} else {
	    this.requiredPermissions = requiredPermissions;
	}
	return this;
    }

    public Permission[] getRequiredBotPermissions() {
	return this.requiredBotPermissions;
    }

    public SlashCommand setRequiredBotPermissions(final Permission... requiredBotPermissions) {
	if (requiredBotPermissions == null) {
	    this.requiredBotPermissions = new Permission[] {};
	} else {
	    this.requiredBotPermissions = requiredBotPermissions;
	}
	return this;
    }

    public SlashCommand setRequiredCustomPermissions(final CustomPermission... permissions) {
	this.requiredCustomPermissions = permissions;
	return this;
    }

    public CustomPermission[] getRequiredCustomPermissions() {
	return this.requiredCustomPermissions;
    }

    public boolean isToggleable() {
	return this.isToggleable;
    }

    public SlashCommand toggleable() {
	this.isToggleable = true;
	return this;
    }

    public SlashCommand notToggleable() {
	this.isToggleable = false;
	return this;
    }

    public boolean shouldAutoRegister() {
	return this.shouldAutoRegister;
    }

    public SlashCommand dontAutoRegister() {
	this.shouldAutoRegister = false;
	return this;
    }

    public SlashCommand premiumRequired() {
	this.isPremium = true;
	return this;
    }

    public boolean isPremiumCommand() {
	return this.isPremium;
    }

    @Override
    public String toString() {
	return "SlashCommand [data=" + this.data + ", category=" + this.category + ", progress=" + this.progress + ", requiredPermissions=" + Arrays.toString(this.requiredPermissions) + ", requiredBotPermissions=" + Arrays.toString(this.requiredBotPermissions) + ", requiredCustomPermissions=" + Arrays.toString(this.requiredCustomPermissions) + ", isToggleable=" + this.isToggleable + ", shouldAutoRegister=" + this.shouldAutoRegister + ", isPremium=" + this.isPremium + "]";
    }
}