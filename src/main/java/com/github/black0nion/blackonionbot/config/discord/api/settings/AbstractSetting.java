package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class AbstractSetting<T> implements Setting<T> {

	private final SettingsSaver settingsSaver;

	private final String name;
	private T value;

	private final Class<T> type;
	private final boolean nullable;
	@Nullable
	private final Validator<T>[] validators;

	private final Permission[] permissions;
	private final CustomPermission[] customPermissions;

	@SafeVarargs
	protected AbstractSetting(SettingsSaver settingsSaver, String name, T defaultValue, Class<T> type, boolean nullable, Permission[] permissions, CustomPermission[] customPermissions, @Nullable Validator<T>... validators) {
		this.settingsSaver = settingsSaver;
		this.name = name;
		this.type = type;
		this.nullable = nullable;
		this.permissions = permissions;
		this.customPermissions = customPermissions;
		this.validators = validators;
		if (defaultValue != null) {
			validate(defaultValue);
			this.value = defaultValue;
		} else if (!nullable) {
			throw new IllegalArgumentException("Default value is null");
		} else {
			this.value = null;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public Object toDatabaseValue() {
		return value;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public Validator<T>[] getValidators() {
		return validators;
	}

	@Override
	public Permission[] getRequiredPermissions() {
		return permissions;
	}

	@Override
	public CustomPermission[] getRequiredCustomPermissions() {
		return customPermissions;
	}

	/**
	 * @throws IllegalArgumentException if the value is not valid (according to the validators)
	 */
	@Override
	public void setValue(T value) {
		if (value == null) {
			if (nullable) {
				saveValue(null);
				return;
			}
			throw new IllegalArgumentException("Value is null");
		}
		validate(value);
		saveValue(value);
	}

	/**
	 * Only use this if you know what you are doing (e.g. when loading from the database)
	 */
	@Override
	public void setValueBypassing(T value) {
		this.value = value;
	}

	/**
	 * Will only be called if the value is of a type that can be parsed according to {@link #canParse()}
	 */
	protected abstract T parse(@Nonnull Object value) throws Exception; // NOSONAR will get wrapped in a ParseException

	@Override
	public void setParsedValue(Object value) throws ParseException {
		try {
			saveValue(pleaseParse(value));
		} catch (ParseException | SettingSaveException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	@Override
	public void setParsedValueBypassing(Object value) throws ParseException {
		setValueBypassing(pleaseParse(value));
	}

	private T pleaseParse(Object value) {
		if (value == null) {
			if (nullable) {
				saveValue(null);
				return null;
			}
			throw new IllegalArgumentException("Value is null");
		}

		if (this.type.isAssignableFrom(value.getClass())) {
			setValue(this.type.cast(value));
			return null;
		}

		try {
			return parse(value);
		} catch (ParseException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	/**
	 * Sets the value, bypassing the validation
	 * Also notifies the settings container of the change
	 */
	private void saveValue(T value) throws SettingSaveException {
		this.value = value;
		settingsSaver.accept(this);
	}

	/**
	 * @throws IllegalArgumentException if the value is not valid (according to the validators)
	 */
	private void validate(@Nonnull T value) {
		// check if the value is of the right type (can include subtypes)
		if (!type.isAssignableFrom(value.getClass()) && !this.canParse(value.getClass())) {
			throw new IllegalArgumentException("Value '" + value + "' is not of the right type for setting '" + name + "' (expected " + type.getSimpleName() + ")");
		}

		if (this.validators != null) {
			for (Validator<T> validator : validators) {
				if (!validator.test(value)) {
					throw new IllegalArgumentException("Value '" + value + "' is not valid for setting '" + name + "' according to validator '" + validator + "'");
				}
			}
		}
	}

	@Override
	public String toString() {
		return "AbstractSetting{" +
			"name='" + name + '\'' +
			", value=" + getType().getSimpleName() + "[" + value + "]" +
			", nullable=" + nullable +
			", validators=" + Arrays.toString(validators) +
			'}';
	}
}
