package com.github.black0nion.blackonionbot.commands.common;

import com.github.black0nion.blackonionbot.commands.Progress;
import com.github.black0nion.blackonionbot.commands.common.utils.command.CommandUtils;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.github.black0nion.blackonionbot.utils.Utils.gOD;

public abstract class AbstractCommand<T extends AbstractCommandBuilder<T, D>, D extends CommandData> implements CommandUtils, Command {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final D data;

	protected Category category;
	protected final Progress progress;
	protected final Set<Permission> requiredPermissions;
	protected final Set<Permission> requiredBotPermissions;
	protected final Set<CustomPermission> requiredCustomPermissions;
	protected final boolean isToggleable;
	protected final boolean shouldAutoRegister;
	protected final boolean isPremium;
	protected final boolean isEphemeral;
	protected final boolean isAdminGuild;

	protected final Config config;

	protected AbstractCommand(AbstractCommandBuilder<T, D> builder, Config config) {
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
		this.config = config;
	}

	@Override
	public void handleButtonPress(ButtonInteractionEvent event) {
		// NOOP
	}

	@Override
	public void handleModalInteraction(ModalInteractionEvent event) {
		// NOOP
	}

	@Override
	public void handleSelectMenuInteraction(GenericSelectMenuInteractionEvent<?, ?> event) {
		// NOOP
	}

	//region Getters
	public boolean isEphemeral() {
		return isEphemeral;
	}

	@Override
	public D getData() {
		return data;
	}

	@Override
	public String getName() {
		return data.getName();
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public Progress getProgress() {
		return progress;
	}

	@Override
	public Set<Permission> getRequiredPermissions() {
		return requiredPermissions;
	}

	@Override
	@Nonnull
	public Set<Permission> getRequiredBotPermissions() {
		return requiredBotPermissions;
	}

	@Override
	@Nonnull
	public Set<CustomPermission> getRequiredCustomPermissions() {
		return requiredCustomPermissions;
	}

	public boolean shouldAutoRegister() {
		return shouldAutoRegister;
	}

	@Override
	public boolean isPremiumCommand() {
		return isPremium;
	}

	@Override
	public boolean isToggleable() {
		return isToggleable;
	}

	@Override
	public boolean isAdminGuild() {
		return isAdminGuild;
	}
	//endregion
}