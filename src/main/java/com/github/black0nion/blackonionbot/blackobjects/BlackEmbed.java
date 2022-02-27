package com.github.black0nion.blackonionbot.blackobjects;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.temporal.TemporalAccessor;

public class BlackEmbed extends EmbedBuilder {

	private final Language lang;
	private String title = null;
	private String url = null;
	private Color color = null;

	public BlackEmbed(final Language lang) {
		this.lang = lang;
	}

	public BlackEmbed() {
		this.lang = LanguageSystem.getDefaultLanguage();
	}

	public BlackEmbed(final BlackEmbed embed) {
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
	public BlackEmbed setFooter(String text) {
		final String tempText = this.lang.getTranslation(text);
		if (tempText != null) {
			text = tempText;
		}
		super.setFooter(text);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setTitle(String title) {
		return this.setTitle(title, null);
	}

	@Override
	@Nonnull
	public BlackEmbed setTitle(String title, final String url) {
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
	public BlackEmbed addField(final String name, final String value) {
		return this.addField(name, value, false);
	}

	@Override
	@Nonnull
	public BlackEmbed addField(String name, String value, final boolean inline) {
		final String translatedName = this.lang.getTranslation(name);
		final String translatedValue = this.lang.getTranslation(value);

		if (translatedName != null) name = translatedName;
		if (translatedValue != null) value = translatedValue;
		super.addField(name, value, inline);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed addBlankField(final boolean inline) {
		super.addBlankField(inline);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed addField(final Field field) {
		super.addField(field);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed appendDescription(final @Nonnull CharSequence description) {
		super.appendDescription(description);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed clear() {
		super.clear();
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed clearFields() {
		super.clearFields();
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setAuthor(final String name) {
		super.setAuthor(name);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setAuthor(final String name, final String url) {
		super.setAuthor(name, url);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setAuthor(final String name, final String url, final String iconUrl) {
		super.setAuthor(name, url, iconUrl);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setColor(final Color color) {
		this.color = color;
		super.setColor(color);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setColor(final int color) {
		this.color = new Color(color);
		super.setColor(color);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setFooter(String text, final String iconUrl) {
		super.setFooter(text, iconUrl);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setImage(final String url) {
		super.setImage(url);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setThumbnail(final String url) {
		super.setThumbnail(url);
		return this;
	}

	@Override
	@Nonnull
	public BlackEmbed setTimestamp(final TemporalAccessor temporal) {
		super.setTimestamp(temporal);
		return this;
	}

	public String getTitle() {
		return this.title;
	}

	public String getUrl() {
		return this.url;
	}

	public Color getColor() {
		return this.color;
	}
}