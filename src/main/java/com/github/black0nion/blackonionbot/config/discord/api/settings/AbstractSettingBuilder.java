package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unchecked")
public abstract class AbstractSettingBuilder<T, S extends Setting<T>, B extends AbstractSettingBuilder<T, S, B>> {

	protected final String name;
	protected final Class<T> type;
	protected final SettingsSaver settingsSaver;
	protected T defaultValue;
	protected boolean nullable;
	protected Validator<T>[] validators;
	protected Set<Permission> permissions = EnumSet.noneOf(Permission.class);
	protected Set<CustomPermission> customPermissions = EnumSet.noneOf(CustomPermission.class);

	protected AbstractSettingBuilder(SettingsSaver saver, String name, Class<T> type) {
		this.settingsSaver = saver;
		this.name = name;
		this.type = type;
	}

	public B setNullable(boolean nullable) {
		this.nullable = nullable;
		return (B) this;
	}

	public B nullable() {
		return setNullable(true);
	}

	public B defaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return (B) this;
	}

	public B validators(Validator<T>... validators) {
		this.validators = validators;
		return (B) this;
	}

	public B permissions(@Nonnull Permission... permissions) {
		requireNonNull(customPermissions, "permissions");
		this.permissions = EnumSet.copyOf(List.of(permissions));
		return (B) this;
	}

	public B addPermissions(@Nonnull Permission... permissions) {
		requireNonNull(permissions, "permissions");
		Collections.addAll(this.permissions, permissions);
		return (B) this;
	}

	public B customPermissions(@Nonnull CustomPermission... customPermissions) {
		requireNonNull(customPermissions, "customPermissions");
		this.customPermissions = EnumSet.copyOf(List.of(customPermissions));
		return (B) this;
	}

	public B addCustomPermissions(@Nonnull CustomPermission... customPermissions) {
		requireNonNull(customPermissions, "customPermissions");
		Collections.addAll(this.customPermissions, customPermissions);
		return (B) this;
	}

	public abstract S build();
}
