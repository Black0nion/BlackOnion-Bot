package com.github.black0nion.blackonionbot.utils.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Throws a error if a @Nonnull field is null.
 * @author Lyubomyr Shaydariv (https://stackoverflow.com/a/44364504/10052779)
 */
public class NonNullTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
		final Collection<Field> notNullFields = getNotNullFields(typeToken.getRawType());
		// If no @Nonnull fields found, then just tell Gson to pick the next best type adapter
		if (notNullFields.isEmpty()) {
			return null;
		}
		// If there's at least one @Nonnull field, get the original type adapter

		final TypeAdapter<T> delegateTypeAdapter = gson.getDelegateAdapter(this, typeToken);
		return new TypeAdapter<>() {

			@Override
			public void write(final JsonWriter out, final T value) throws IOException {
				delegateTypeAdapter.write(out, value);
			}

			@Override
			public T read(final JsonReader in) throws IOException {
				try {
					// Read the value ...
					final T value = delegateTypeAdapter.read(in);
					// ... and do some post-processing
					for (final Field f : notNullFields) {
						if (f.get(value) == null) {
							throw new MalformedJsonException(f + " has no value");
						}
					}
					return value;
				} catch (final IllegalAccessException ex) {
					throw new IOException(ex);
				}
			}
		};
	}

	private static Collection<Field> getNotNullFields(final Class<?> clazz) {
		// Primitive types and java.lang.Object do not have @Nonnull
		if (clazz.isPrimitive() || clazz == Object.class) {
			return Collections.emptyList();
		}
		// Scan the whole hierarchy from the bottom subclass to the top superclass (except java.lang.Object we mentioned above)
		final Collection<Field> notNullFields = new ArrayList<>();
		for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
			for (final Field f : c.getDeclaredFields()) {
				if (f.isAnnotationPresent(Nonnull.class)) {
					// Don't forget to make private fields accessible
					f.setAccessible(true);
					notNullFields.add(f);
				}
			}
		}
		return notNullFields;
	}
}