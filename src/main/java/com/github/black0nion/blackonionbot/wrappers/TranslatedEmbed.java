package com.github.black0nion.blackonionbot.wrappers;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.temporal.TemporalAccessor;

public class TranslatedEmbed extends EmbedBuilder {

	private final Language lang;
	private String title = null;
	private String url = null;
	private Color color = null;

	public TranslatedEmbed(final Language lang) {
		this.lang = lang;
	}

	public TranslatedEmbed() {
		this.lang = LanguageSystem.getDefaultLanguage();
	}

	public TranslatedEmbed(final TranslatedEmbed embed) {
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
	public TranslatedEmbed setFooter(String text) {
		final String tempText = this.lang.getTranslation(text);
		if (tempText != null) {
			text = tempText;
		}
		super.setFooter(text);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setTitle(String title, final String url) {
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

	@Nonnull
	public TranslatedEmbed addField(final String name, final Object value, boolean inline) {
		return this.addField(name, String.valueOf(value), inline);
	}

	@Nonnull
	public TranslatedEmbed addField(final String name, final String value) {
		return this.addField(name, value, false);
	}

	@Override
	@Nonnull
	public TranslatedEmbed addField(String name, String value, final boolean inline) {
		final String translatedName = this.lang.getTranslation(name);
		final String translatedValue = this.lang.getTranslation(value);

		if (translatedName != null)
			name = translatedName;
		if (translatedValue != null)
			value = translatedValue;
		super.addField(name, value, inline);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed addBlankField(final boolean inline) {
		super.addBlankField(inline);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed addField(final Field field) {
		super.addField(field);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed appendDescription(final @Nonnull CharSequence description) {
		super.appendDescription(description);
		return this;
	}

	@Nonnull
	public TranslatedEmbed setDescriptionTranslated(final @Nonnull CharSequence description) {
		super.setDescription(this.lang.getTranslation(String.valueOf(description)));
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed clear() {
		super.clear();
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed clearFields() {
		super.clearFields();
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setAuthor(final String name) {
		super.setAuthor(name);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setAuthor(final String name, final String url) {
		super.setAuthor(name, url);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setAuthor(final String name, final String url, final String iconUrl) {
		super.setAuthor(name, url, iconUrl);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setFooter(String text, final String iconUrl) {
		super.setFooter(text, iconUrl);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setImage(final String url) {
		super.setImage(url);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setThumbnail(final String url) {
		super.setThumbnail(url);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setTimestamp(final TemporalAccessor temporal) {
		super.setTimestamp(temporal);
		return this;
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setTitle(String title) {
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
	public TranslatedEmbed setColor(final Color color) {
		this.color = color;
		super.setColor(color);
		return this;
	}

	@Override
	@Nonnull
	public TranslatedEmbed setColor(final int color) {
		this.color = new Color(color);
		super.setColor(color);
		return this;
	}
}
