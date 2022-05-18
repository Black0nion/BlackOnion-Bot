package com.github.black0nion.blackonionbot.systems.language;

import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class LanguageTest {
	private static final Logger logger = LoggerFactory.getLogger(LanguageTest.class);

	private static Schema languageSchema;
	private static List<Language> translations;

	@Test
	@Order(1)
	void test_language_json_schema() {
		JSONObject jsonSchema = new JSONObject(new JSONTokener(Objects.requireNonNull(LanguageTest.class.getResourceAsStream("/translationsschema.json"))));

		assertNotNull(languageSchema = SchemaLoader.load(jsonSchema));
		logger.info("Loaded schema: '{}'", languageSchema);
	}

	@Test
	@Order(2)
	void test_languages_against_json_schema() {
		assumeTrue(languageSchema != null);

		AtomicBoolean defaultLangFound = new AtomicBoolean(false);
		translations = new Reflections("translations", Scanners.Resources).getResources("[A-Z][a-z]+\\.json").stream()
			.map(resource -> new JSONObject(new JSONTokener(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)))))
			.peek(json -> assertDoesNotThrow(() -> languageSchema.validate(json)))
			.map(json -> assertDoesNotThrow(() -> new Language(json)))
			.peek(m -> {
				if (m.isDefault()) {
					assertFalse(defaultLangFound.get());
					defaultLangFound.set(true);
				}
			}).toList();

		logger.info("Loaded languages: '{}'", translations);

		assertTrue(translations.size() > 0);
	}

	@Test
	@Order(3)
	void test_language_dummy_translation() {
		assumeTrue(translations != null);

		translations.forEach(m -> assertNotNull(m.getTranslation("dummy")));
		translations.forEach(m -> assertNotNull(m.getFullName()));
		translations.forEach(m -> assertTrue(m.getFullName().matches("[A-Z][a-z]+ \\([A-Z]{2}\\)")));
		logger.info("List contains dummy translations: '{}'", translations.stream().map(lang -> lang.getTranslationNonNull("dummy")).toList());
	}
}