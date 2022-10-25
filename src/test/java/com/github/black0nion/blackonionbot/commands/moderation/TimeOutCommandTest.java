package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.misc.exception.TooLongException;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeOutCommandTest {

	SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);

	@Test
	void test_parseDuration_allThere() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7 + 1).plusHours(1).plusMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullWeeks() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(1).plusHours(1).plusMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullDays() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7).plusHours(1).plusMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullHours() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7).plusDays(1).plusMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullMinutes() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7).plusHours(24).plusMinutes(60), duration);
	}

	@Test
		void test_parseDuration_allNull() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		assertThrows(IllegalArgumentException.class, () -> TimeOutCommand.parseDuration(event));
	}

	@Test
	void test_parseDuration_nullExceptMinutes() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullExceptHours() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofHours(1), duration);
	}

	@Test
	void test_parseDuration_nullExceptDays() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(1), duration);
	}

	@Test
	void test_parseDuration_nullExceptWeeks() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7), duration);
	}

	@Test
	void test_parseDuration_nullDaysWeeks() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofHours(1).plusMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullHoursWeeks() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(1).plusMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullHoursDays() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7).plusMinutes(1), duration);
	}

	@Test
	void test_parseDuration_nullMinutesWeeks() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(null);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(1).plusHours(1), duration);
	}

	@Test
	void test_parseDuration_nullMinutesDays() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7).plusHours(1), duration);
	}

	@Test
	void test_parseDuration_nullMinutesHours() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(null);
		when(event.getOption(eq(Utils.HOURS), any())).thenReturn(null);
		when(event.getOption(eq(Utils.DAYS), any())).thenReturn(1L);
		when(event.getOption(eq(Utils.WEEKS), any())).thenReturn(1L);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofDays(7).plusDays(1), duration);
	}

	@Test
	void test_parseDuration_maxDuration() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn((long) TimeOutCommand.MAX_TIMEOUT_DURATION_MIN);

		Duration duration = assertDoesNotThrow(() -> TimeOutCommand.parseDuration(event));
		assertEquals(Duration.ofMinutes(TimeOutCommand.MAX_TIMEOUT_DURATION_MIN), duration);
	}

	@Test
	void test_parseDuration_tooLong() {
		when(event.getOption(eq(Utils.MINUTES), any())).thenReturn(TimeOutCommand.MAX_TIMEOUT_DURATION_MIN + 1L);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> TimeOutCommand.parseDuration(event));
		assertInstanceOf(TooLongException.class, exception.getCause());
	}
}
