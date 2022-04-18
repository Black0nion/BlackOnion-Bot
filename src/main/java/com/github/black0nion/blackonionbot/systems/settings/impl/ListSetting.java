package com.github.black0nion.blackonionbot.systems.settings.impl;

import com.github.black0nion.blackonionbot.systems.settings.ConsumerCancellable;
import com.github.black0nion.blackonionbot.systems.settings.Setting;
import net.dv8tion.jda.internal.utils.Checks;
import org.bson.Document;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ListSetting<T> extends Setting<List<T>> {

	private final Class<T> genericClass;
	public ListSetting(String name, String descriptionKey, Class<T> genericClass, List<T> defaultValue, Consumer<List<T>> onChanged, ConsumerCancellable<List<T>> preChanged, boolean nullable) {
		super(name, descriptionKey, defaultValue, onChanged, preChanged, nullable);
		this.genericClass = genericClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<T> parse(Object value) throws IllegalArgumentException {
		if (value instanceof List) {
			return (List<T>) value;
		}
		if (value instanceof JSONArray json) {
			return json.toList().stream().map(o -> {
				if (o instanceof String) {
					return genericClass.cast(o);
				}
				return genericClass.cast(o.toString());
			// use this over toList() because of mutability
			}).collect(Collectors.toList());
		}
		if (value instanceof String str) {
			return Arrays.stream(str.split("[;,]")).map(String::trim).filter(s -> !s.isEmpty()).map(s -> (T) s).collect(Collectors.toList());
		}
		throw new IllegalArgumentException("Expected a list, got " + value.getClass().getSimpleName());
	}

	@Override
	protected List<T> loadImpl(Document doc, String key) {
		return doc.getList(key, genericClass);
	}

	public static class Builder<T> extends SettingBuilder<Builder<T>, List<T>, ListSetting<T>> {

		private Class<T> genericClass;

		public Builder<T> genericClass(Class<T> genericClass) {
			this.genericClass = genericClass;
			return this;
		}

		@Override
		protected ListSetting<T> buildImpl() {
			Checks.notNull(genericClass, "genericClass");
			return new ListSetting<>(name, descriptionKey, genericClass, defaultValue, onChanged, preChanged, nullable);
		}
	}
}