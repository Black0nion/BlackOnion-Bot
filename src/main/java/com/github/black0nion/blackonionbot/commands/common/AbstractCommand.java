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

import static com.github.black0nion.blackonionbot.utils.Utils.gOD;

public abstract class AbstractCommand<T extends AbstractCommandBuilder<T, D>, D extends CommandData> implements CommandUtils {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final D data;

	protected Category category;
	protected final Progress progress;
	protected final Permission[] requiredPermissions;
	protected final Permission[] requiredBotPermissions;
	protected final CustomPermission[] requiredCustomPermissions;
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

	public void handleButtonPress(ButtonInteractionEvent event) {
		// NOOP
	}

	public void handleModalInteraction(ModalInteractionEvent event) {
		// NOOP
	}

	public void handleSelectMenuInteraction(GenericSelectMenuInteractionEvent<?, ?> event) {
		// NOOP
	}

	//region Getters
	public boolean isEphemeral() {
		return isEphemeral;
	}

	public D getData() {
		return data;
	}

	@Override
	public String getName() {
		return data.getName();
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

	public boolean isAdminGuild() {
		return isAdminGuild;
	}
	//endregion
}
