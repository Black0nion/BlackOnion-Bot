package com.github.black0nion.blackonionbot.commands.slash.impl.misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PollCommandTest {
	@Test
	void test_digits_list_and_digits_unicode_same_length() {
		assertEquals(PollCommand.DIGITS_LIST.size(), PollCommand.DIGITS_UNICODE.size());
	}
}
