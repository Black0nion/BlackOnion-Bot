package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Progress;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static com.github.black0nion.blackonionbot.utils.Utils.gOD;

@SuppressWarnings("unused")
public abstract class SlashCommand {

	final SlashCommandData data;
	Category category;
	final Progress progress;
	final Permission[] requiredPermissions;
	final Permission[] requiredBotPermissions;
	final CustomPermission[] requiredCustomPermissions;
	final boolean isToggleable;
	final boolean shouldAutoRegister;
	final boolean isPremium;
	final boolean isEphemeral;
	final boolean isAdminGuild;

	public SlashCommand(SlashCommandBuilder builder) {
		this.data = builder.getData();
		this.category = gOD(builder.getCategory(), Category.OTHER);
		this.progress = gOD(builder.getProgress(), Progress.DONE);
		this.requiredPermissions = builder.getRequiredPermissions();
		this.requiredBotPermissions = builder.getRequiredBotPermissions();
		this.requiredCustomPermissions = builder.getRequiredCustomPermissions();
		this.isToggleable = builder.isToggleable();
		this.shouldAutoRegister = builder.shouldAutoRegister();
		this.isPremium = builder.isPremium();
		this.isEphemeral = builder.isEphemeral();
		this.isAdminGuild = builder.isAdminGuild();
	}

	public abstract void execute(final SlashCommandEvent cmde, final SlashCommandInteractionEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel);

	//region Getters
	public SlashCommandData getData() {
		return data;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Progress getProgress() {
		return progress;
	}

	public Permission[] getRequiredPermissions() {
		return requiredPermissions;
	}

	public Permission[] getRequiredBotPermissions() {
		return requiredBotPermissions;
	}

	public CustomPermission[] getRequiredCustomPermissions() {
		return requiredCustomPermissions;
	}

	public boolean isToggleable() {
		return isToggleable;
	}

	public boolean shouldAutoRegister() {
		return shouldAutoRegister;
	}

	public boolean isPremiumCommand() {
		return isPremium;
	}

	public boolean isEphemeral() {
		return isEphemeral;
	}

	public boolean isAdminGuild() {
		return isAdminGuild;
	}
	//endregion

	public boolean isVisible(final BlackUser user) {
		return user.hasPermission(this.requiredCustomPermissions);
	}

	@Nonnull
	protected static SlashCommandBuilder builder(@Nonnull SlashCommandData data) {
		return SlashCommandBuilder.builder(data);
	}

	@Nonnull
	protected static SlashCommandBuilder builder(@Nonnull String name, @Nonnull String description) {
		return SlashCommandBuilder.builder(Commands.slash(name, description));
	}

	@Override
	public String toString() {
		return "SlashCommand [data=" + this.data + ", category=" + this.category + ", progress=" + this.progress + ", requiredPermissions=" + Arrays.toString(this.requiredPermissions) + ", requiredBotPermissions=" + Arrays.toString(this.requiredBotPermissions) + ", requiredCustomPermissions=" + Arrays.toString(this.requiredCustomPermissions) + ", isToggleable=" + this.isToggleable + ", shouldAutoRegister=" + this.shouldAutoRegister + ", isPremium=" + this.isPremium + "]";
	}
}