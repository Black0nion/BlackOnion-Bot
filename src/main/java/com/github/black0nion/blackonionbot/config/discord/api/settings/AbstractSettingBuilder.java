package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unchecked")
public abstract class AbstractSettingBuilder<T, S extends Setting<T>, B extends AbstractSettingBuilder<T, S, B>> {

	protected final String name;
	protected final Class<T> type;
	protected final SettingsSaver settingsSaver;
	protected T defaultValue;
	protected boolean nullable;
	protected Validator<T>[] validators;
	protected Permission[] permissions;
	protected CustomPermission[] customPermissions;

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

	public B permissions(Permission... permissions) {
		this.permissions = permissions;
		return (B) this;
	}

	public B addPermissions(Permission... permissions) {
		if (this.permissions == null) {
			this.permissions = permissions;
		} else {
			Permission[] newPermissions = new Permission[this.permissions.length + permissions.length];
			System.arraycopy(this.permissions, 0, newPermissions, 0, this.permissions.length);
			System.arraycopy(permissions, 0, newPermissions, this.permissions.length, permissions.length);
			this.permissions = newPermissions;
		}
		return (B) this;
	}

	public B customPermissions(CustomPermission... customPermissions) {
		this.customPermissions = customPermissions;
		return (B) this;
	}

	public B addCustomPermissions(CustomPermission... customPermissions) {
		if (this.customPermissions == null) {
			this.customPermissions = customPermissions;
		} else {
			CustomPermission[] newCustomPermissions = new CustomPermission[this.customPermissions.length + customPermissions.length];
			System.arraycopy(this.customPermissions, 0, newCustomPermissions, 0, this.customPermissions.length);
			System.arraycopy(customPermissions, 0, newCustomPermissions, this.customPermissions.length, customPermissions.length);
			this.customPermissions = newCustomPermissions;
		}
		return (B) this;
	}

	public abstract S build();
}
