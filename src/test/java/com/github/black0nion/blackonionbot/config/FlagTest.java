package com.github.black0nion.blackonionbot.config;

import com.github.black0nion.blackonionbot.config.common.ConfigFlag;
import com.github.black0nion.blackonionbot.config.common.Flags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlagTest {
	@Test
	void test_range_flag() {
		Flags.Range range = assertDoesNotThrow(() -> Flags.range(-1, -1));
		assertNotNull(range);
	}

	@Test
	void test_matches_regex_flag() {
		Flags.MatchesRegex regex = assertDoesNotThrow(() -> Flags.matchesRegex(".*"));
		assertNotNull(regex);
		assertEquals(".*", regex.regex().pattern());
	}

	@Test
	void test_nonnull_same() {
		assertSame(Flags.NonNull, Flags.NonNull);
		assertEquals(Flags.NonNull, Flags.NonNull); // NOSONAR
		assertNotEquals(new ConfigFlag() {}, Flags.NonNull);
	}

	@Test
	void test_default() {
		Flags.Default<String> def = assertDoesNotThrow(() -> Flags.defaultValue("test"));
		assertNotNull(def);
		assertEquals("test", def.defaultValue());

		Flags.Default<String> def2 = assertDoesNotThrow(() -> Flags.defaultValue(null));
		assertNotNull(def2);
		assertNull(def2.defaultValue());
	}
}
