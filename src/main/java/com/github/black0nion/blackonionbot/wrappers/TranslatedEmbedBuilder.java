package com.github.black0nion.blackonionbot.wrappers;

import com.github.black0nion.blackonionbot.systems.language.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class TranslatedEmbedBuilder extends EmbedBuilder {

	private final Language lang;
	private String title = null;
	private String url = null;
	private Color color = null;

	public TranslatedEmbedBuilder(final Language lang) {
		this.lang = lang;
	}

	public TranslatedEmbedBuilder(final TranslatedEmbedBuilder embed) {
		super(embed);
		this.lang = embed.lang;
		this.title = embed.title;
		this.url = embed.url;
		this.color = embed.color;
	}

	public Language getLang() {
		return this.lang;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setFooter(String text) {
		final String tempText = this.lang.getTranslation(text);
		if (tempText != null) {
			text = tempText;
		}
		super.setFooter(text);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setFooter(String text, final String iconUrl) {
		final String tempText = this.lang.getTranslation(text);
		if (tempText != null) {
			text = tempText;
		}
		super.setFooter(text, iconUrl);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setTitle(String title, final String url) {
		if (title == null) {
			super.setTitle(null, url);
		}
		final String tempTitle = this.lang.getTranslation(title);
		if (tempTitle != null) {
			title = tempTitle;
		}
		super.setTitle(title, url);
		this.title = title;
		this.url = url;
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder addField(final Field field) {
		super.addField(field);
		return this;
	}

	@Nonnull
	public TranslatedEmbedBuilder addField(final String name, final Object value, boolean inline) {
		return this.addField(name, String.valueOf(value), inline);
	}

	@Nonnull
	public TranslatedEmbedBuilder addField(final String name, final String value) {
		return this.addField(name, value, false);
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder addField(@Nonnull String name, @Nonnull String value, final boolean inline) {
		if (this.lang != null) {
			final String translatedName = this.lang.getTranslation(name);
			if (translatedName != null) {
				name = translatedName;
			}

			final String translatedValue = this.lang.getTranslation(value);
			if (translatedValue != null) {
				value = translatedValue;
			}
		}

		super.addField(name, value, inline);
		return this;
	}

	@Nonnull
	public TranslatedEmbedBuilder addFields(final @Nonnull List<Field> fields) {
		this.getFields().addAll(fields);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder addBlankField(final boolean inline) {
		super.addBlankField(inline);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder appendDescription(final @Nonnull CharSequence description) {
		super.appendDescription(description);
		return this;
	}

	@Nonnull
	public TranslatedEmbedBuilder setDescriptionTranslated(final @Nonnull CharSequence description) {
		super.setDescription(this.lang.getTranslation(String.valueOf(description)));
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder clear() {
		super.clear();
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder clearFields() {
		super.clearFields();
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setAuthor(final String name) {
		super.setAuthor(name);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setAuthor(final String name, final String url) {
		super.setAuthor(name, url);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setAuthor(final String name, final String url, final String iconUrl) {
		super.setAuthor(name, url, iconUrl);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setImage(final String url) {
		super.setImage(url);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setThumbnail(final String url) {
		super.setThumbnail(url);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setTimestamp(final TemporalAccessor temporal) {
		super.setTimestamp(temporal);
		return this;
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setTitle(String title) {
		return this.setTitle(title, null);
	}

	public String getUrl() {
		return this.url;
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setColor(final Color color) {
		this.color = color;
		super.setColor(color);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbedBuilder setColor(final int color) {
		this.color = new Color(color);
		super.setColor(color);
		return this;
	}
}
