package com.github.black0nion.blackonionbot.commands;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandsTest {
	@Test
	void test_less_than_50_commands() {
		int commandCount = new Reflections("com.github.black0nion.blackonionbot.commands").getSubTypesOf(SlashCommand.class).size();
		assertTrue(commandCount < 100, "There are more than 100 commands! (" + commandCount + ")");
	}
}