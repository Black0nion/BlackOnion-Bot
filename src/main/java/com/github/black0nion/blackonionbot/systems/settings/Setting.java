package com.github.black0nion.blackonionbot.systems.settings;

import net.dv8tion.jda.internal.utils.Checks;
import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * NOTE: always set the value to the actual value of the setting, even if value == defaultValue
 */
public abstract class Setting<T> {
	protected final String name;
	protected final String descriptionKey;

	protected final T defaultValue;
	@Nullable
	private T value;
	protected final boolean nullable;

	protected Consumer<T> onChanged;
	protected ConsumerCancellable<T> preChanged;

	public Setting(String name, String descriptionKey, T defaultValue, Consumer<T> onChanged, ConsumerCancellable<T> preChanged, boolean nullable) {
		this.name = name;
		this.descriptionKey = descriptionKey;
		this.defaultValue = defaultValue;
		this.onChanged = onChanged;
		this.preChanged = preChanged;
		this.nullable = nullable;
	}

	public Setting<T> addChangeListener(Consumer<T> onChanged) {
		Checks.notNull(onChanged, "onChanged");
		this.onChanged = this.onChanged == null ? onChanged : this.onChanged.andThen(onChanged);
		return this;
	}

	public Setting<T> addSettingChangeListener(Consumer<Setting<T>> onChanged) {
		Checks.notNull(onChanged, "onChanged");
		this.onChanged = this.onChanged == null ? val -> onChanged.accept(this) : this.onChanged.andThen(val -> onChanged.accept(this));
		return this;
	}

	//region Getters
	public String getName() {
		return name;
	}

	public String getDescriptionKey() {
		return descriptionKey;
	}

	public boolean isNullable() {
		return nullable;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return null if {@link Setting#nullable} is true. DOES NOT return {@link Setting#defaultValue}!
	 */
	@Nullable
	public T getValue() {
		return value;
	}

	public T getValueOrDefault() {
		return value == null ? defaultValue : value;
	}

	/**
	 * A method to make the compiler shut up about nUlLaBlE when you're using {@link Setting#getValue()} and {@link Setting#nullable} is false.
	 */
	@Nonnull
	public T getValueNN() {
		assert value != null : "Value is null";
		return value;
	}

	public T getValueOrElse(T defaultValue) {
		return value == null ? defaultValue : value;
	}

	public T getValueOrElseGet(Supplier<T> getter) {
		return value == null ? getter.get() : value;
	}
	//endregion

	public final void load(Document config) {
		// don't call setValue here, because it will call onChanged
		this.value = this.loadImpl(config, name);
	}

	protected T loadImpl(Document doc, String key) {
		//noinspection unchecked
		return defaultValue != null ? doc.get(name, defaultValue) : (T) doc.get(name);
	}

	/**
	 * Only nullable if {@link Setting#nullable} is true.
	 * @throws IllegalArgumentException if {@link Setting#nullable} is false and {@code value} is null.
	 */
	public void setValue(@Nullable T value) {
		if (!isValidValue(value)) {
			throw new IllegalArgumentException("Invalid value for setting " + name);
		}
		if (preChanged != null) {
			preChanged.accept(value);
			if (preChanged.isCancelled()) {
				return;
			}
		}
		this.value = value;
		if (onChanged != null) onChanged.accept(value);
	}

	/**
	 * Calls {@link Setting#setValue(T)} with {@link Setting#defaultValue}.
	 */
	public void reset() {
		setValue(defaultValue);
	}

	protected abstract T parse(Object value) throws IllegalArgumentException;

	protected boolean isValidValue(T value) {
		return value != null || nullable;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Setting<?> setting = (Setting<?>) other;
		return Objects.equals(name, setting.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "Setting{" +
			"name='" + name + '\'' +
			", descriptionKey='" + descriptionKey + '\'' +
			", defaultValue=" + defaultValue +
			", value=" + value +
			'}';
	}

	@SuppressWarnings("unchecked")
	protected abstract static class SettingBuilder<B, V, S> {
		protected String name;
		protected String descriptionKey;
		protected V defaultValue;
		protected boolean nullable;
		protected Consumer<V> onChanged;
		protected ConsumerCancellable<V> preChanged;

		protected SettingBuilder() {}

		protected SettingBuilder(String name) {
			this.name = name;
		}

		public B name(String name) {
			this.name = name;
			return (B) this;
		}

		public B descriptionKey(String descriptionKey) {
			this.descriptionKey = descriptionKey;
			return (B) this;
		}

		public B defaultValue(V defaultValue) {
			this.defaultValue = defaultValue;
			return (B) this;
		}

		public B notNullable() {
			return this.nullable(false);
		}

		public B nullable(boolean nullable) {
			this.nullable = nullable;
			return (B) this;
		}

		public B onChanged(Consumer<V> onChanged) {
			this.onChanged = onChanged;
			return (B) this;
		}

		public B preChanged(ConsumerCancellable<V> preChanged) {
			this.preChanged = preChanged;
			return (B) this;
		}

		public final S build() {
			Checks.notNull(name, "name");
			if (!nullable)
				Checks.notNull(defaultValue, "defaultValue");
			return buildImpl();
		}

		protected abstract S buildImpl();
	}
}