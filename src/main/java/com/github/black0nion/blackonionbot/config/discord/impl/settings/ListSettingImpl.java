package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSettingBuilder;
import com.github.black0nion.blackonionbot.config.discord.api.settings.SettingsSaver;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListSettingImpl<T, L extends Collection<T>> extends AbstractSetting<L> implements ListSetting<T, L> {

	private final Function<String, T> parseFunction;
	private final Function<T, String> serializeFunction;
	private final Supplier<L> listSupplier;

	@SafeVarargs
	protected ListSettingImpl(SettingsSaver settingsSaver,
		String name,
		L defaultValue,
		Supplier<L> listSupplier,
		Function<String, T> parseFunction,
		Function<T, String> serializeFunction,
		Class<L> type,
		Permission[] permissions,
		CustomPermission[] customPermissions,
		@Nullable Validator<L>... validators
	) {
		super(settingsSaver, name, defaultValue != null ? defaultValue : listSupplier.get(), type, false, permissions, customPermissions, validators);
		this.listSupplier = listSupplier;
		Objects.requireNonNull(parseFunction, "parseFunction");
		Objects.requireNonNull(serializeFunction, "serializeFunction");
		this.parseFunction = parseFunction;
		this.serializeFunction = serializeFunction;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected L parse(@NotNull Object value) throws Exception {
		if (value.getClass().isAssignableFrom(getType())) {
			return (L) value;
		}

		if (value instanceof String str) {
			if (str.isEmpty()) return listSupplier.get();

			String[] split = str.split(",");
			L list = listSupplier.get();
			for (String s : split) {
				list.add(parseFunction.apply(s));
			}
			return list;
		}

		throw new IllegalArgumentException("Can't parse " + value + " to " + getType().getSimpleName());
	}

	@Override
	public Object toDatabaseValue() {
		return getValue().stream().map(serializeFunction).collect(Collectors.joining(","));
	}

	@Override
	public Object toSerializedValue() {
		// generate a JSON array of the values in the list
		return getValue().stream().map(serializeFunction).toList();
	}

	private final List<Class<?>> canParse = List.of(getType(), String.class);

	@Override
	public List<Class<?>> canParse() {
		return canParse;
	}

	public static final class Builder<T, L extends Collection<T>> extends AbstractSettingBuilder<L, ListSetting<T, L>, Builder<T, L>> {

		private final Supplier<L> emptyListSupplier;
		private final Function<String, T> parseFunction;
		private final Function<T, String> serializeFunction;

		public Builder(SettingsSaver saver, String name, Class<? extends Collection> listType, Supplier<L> emptyListSupplier, Function<String, T> parseFunction, Function<T, String> serializeFunction) {
			super(saver, name, (Class<L>) listType);
			this.emptyListSupplier = emptyListSupplier;
			this.parseFunction = parseFunction;
			this.serializeFunction = serializeFunction;
		}

		@Override
		public Builder<T, L> setNullable(boolean nullable) {
			throw new IllegalArgumentException("ListSetting can't be nullable");
		}

		@Override
		public ListSetting<T, L> build() {
			return new ListSettingImpl<>(settingsSaver, name, defaultValue, emptyListSupplier, parseFunction, serializeFunction, type, permissions, customPermissions, validators);
		}
	}
}
