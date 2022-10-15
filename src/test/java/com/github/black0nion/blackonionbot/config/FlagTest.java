package com.github.black0nion.blackonionbot.config;

import com.github.black0nion.blackonionbot.config.immutable.Flags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FlagTest {
	@Test
	void test_range_flag() {
		Flags.Range range = assertDoesNotThrow(() -> Flags.range(-1, -1));
		assertNotNull(range);
	}
}
