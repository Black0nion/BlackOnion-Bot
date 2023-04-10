package com.github.black0nion.blackonionbot.commands.common;

import com.github.black0nion.blackonionbot.commands.Progress;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Command {
	CommandData getData();

	String getName();

	void handleButtonPress(ButtonInteractionEvent event);

	void handleModalInteraction(ModalInteractionEvent event);

	void handleSelectMenuInteraction(GenericSelectMenuInteractionEvent<?, ?> event);

	Category getCategory();
	void setCategory(Category category);

	Progress getProgress();

	Set<Permission> getRequiredPermissions();

	@Nonnull
	Set<Permission> getRequiredBotPermissions();

	@Nonnull
	Set<CustomPermission> getRequiredCustomPermissions();

	boolean isPremiumCommand();

	boolean isAdminGuild();

	boolean isToggleable();
}