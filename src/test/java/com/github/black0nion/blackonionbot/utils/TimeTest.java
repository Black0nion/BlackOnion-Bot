package com.github.black0nion.blackonionbot.utils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeTest {
	@Test
	void test_days() {
		Time time = Time.DAYS(1);
		assertEquals(TimeUnit.DAYS, time.unit());
		assertEquals(1, time.time());

		time = Time.DAYS(5);
		assertEquals(TimeUnit.DAYS, time.unit());
		assertEquals(5, time.time());

		time = Time.DAYS(0);
		assertEquals(TimeUnit.DAYS, time.unit());
		assertEquals(0, time.time());

		assertThrows(IllegalArgumentException.class, () -> Time.DAYS(-1));
		assertThrows(IllegalArgumentException.class, () -> Time.DAYS(-5));
	}

	@Test
	void test_hours() {
		Time time = Time.HOURS(1);
		assertEquals(TimeUnit.HOURS, time.unit());
		assertEquals(1, time.time());

		time = Time.HOURS(5);
		assertEquals(TimeUnit.HOURS, time.unit());
		assertEquals(5, time.time());

		time = Time.HOURS(0);
		assertEquals(TimeUnit.HOURS, time.unit());
		assertEquals(0, time.time());

		assertThrows(IllegalArgumentException.class, () -> Time.HOURS(-1));
		assertThrows(IllegalArgumentException.class, () -> Time.HOURS(-5));
	}

	@Test
	void test_minutes() {
		Time time = Time.MINUTES(1);
		assertEquals(TimeUnit.MINUTES, time.unit());
		assertEquals(1, time.time());

		time = Time.MINUTES(5);
		assertEquals(TimeUnit.MINUTES, time.unit());
		assertEquals(5, time.time());

		time = Time.MINUTES(0);
		assertEquals(TimeUnit.MINUTES, time.unit());
		assertEquals(0, time.time());

		assertThrows(IllegalArgumentException.class, () -> Time.MINUTES(-1));
		assertThrows(IllegalArgumentException.class, () -> Time.MINUTES(-5));
	}

	@Test
	void test_seconds() {
		Time time = Time.SECONDS(1);
		assertEquals(TimeUnit.SECONDS, time.unit());
		assertEquals(1, time.time());

		time = Time.SECONDS(5);
		assertEquals(TimeUnit.SECONDS, time.unit());
		assertEquals(5, time.time());

		time = Time.SECONDS(0);
		assertEquals(TimeUnit.SECONDS, time.unit());
		assertEquals(0, time.time());

		assertThrows(IllegalArgumentException.class, () -> Time.SECONDS(-1));
		assertThrows(IllegalArgumentException.class, () -> Time.SECONDS(-5));
	}

	@Test
	void test_milliseconds() {
		Time time = Time.MILLISECONDS(1);
		assertEquals(TimeUnit.MILLISECONDS, time.unit());
		assertEquals(1, time.time());

		time = Time.MILLISECONDS(5);
		assertEquals(TimeUnit.MILLISECONDS, time.unit());
		assertEquals(5, time.time());

		time = Time.MILLISECONDS(0);
		assertEquals(TimeUnit.MILLISECONDS, time.unit());
		assertEquals(0, time.time());

		assertThrows(IllegalArgumentException.class, () -> Time.MILLISECONDS(-1));
		assertThrows(IllegalArgumentException.class, () -> Time.MILLISECONDS(-5));
	}

	@Test
	void test_microseconds() {
		Time time = Time.MICROSECONDS(1);
		assertEquals(TimeUnit.MICROSECONDS, time.unit());
		assertEquals(1, time.time());

		time = Time.MICROSECONDS(5);
		assertEquals(TimeUnit.MICROSECONDS, time.unit());
		assertEquals(5, time.time());

		time = Time.MICROSECONDS(0);
		assertEquals(TimeUnit.MICROSECONDS, time.unit());
		assertEquals(0, time.time());

		assertThrows(IllegalArgumentException.class, () -> Time.MICROSECONDS(-1));
		assertThrows(IllegalArgumentException.class, () -> Time.MICROSECONDS(-5));
	}

	@Test
	void test_nanoseconds() {
		Time time = Time.NANOSECONDS(1);
		assertEquals(TimeUnit.NANOSECONDS, time.unit());
		assertEquals(1, time.time());

		time = Time.NANOSECONDS(5);
		assertEquals(TimeUnit.NANOSECONDS, time.unit());
		assertEquals(5, time.time());

		time = Time.NANOSECONDS(0);
		assertEquals(TimeUnit.NANOSECONDS, time.unit());
		assertEquals(0, time.time());

		assertThrows(IllegalArgumentException.class, () -> Time.NANOSECONDS(-1));
		assertThrows(IllegalArgumentException.class, () -> Time.NANOSECONDS(-5));
	}

	@Test
	void test_toString() {
		Time time = Time.DAYS(1);
		assertEquals("1 day", time.toString());

		time = Time.DAYS(5);
		assertEquals("5 days", time.toString());

		time = Time.DAYS(0);
		assertEquals("0 days", time.toString());

		time = Time.HOURS(1);
		assertEquals("1 hour", time.toString());

		time = Time.HOURS(5);
		assertEquals("5 hours", time.toString());

		time = Time.HOURS(0);
		assertEquals("0 hours", time.toString());

		time = Time.MINUTES(1);
		assertEquals("1 minute", time.toString());

		time = Time.MINUTES(5);
		assertEquals("5 minutes", time.toString());

		time = Time.MINUTES(0);
		assertEquals("0 minutes", time.toString());

		time = Time.SECONDS(1);
		assertEquals("1 second", time.toString());

		time = Time.SECONDS(5);
		assertEquals("5 seconds", time.toString());

		time = Time.SECONDS(0);
		assertEquals("0 seconds", time.toString());

		time = Time.MILLISECONDS(1);
		assertEquals("1 millisecond", time.toString());

		time = Time.MILLISECONDS(5);
		assertEquals("5 milliseconds", time.toString());

		time = Time.MILLISECONDS(0);
		assertEquals("0 milliseconds", time.toString());

		time = Time.MICROSECONDS(1);
		assertEquals("1 microsecond", time.toString());

		time = Time.MICROSECONDS(5);
		assertEquals("5 microseconds", time.toString());

		time = Time.MICROSECONDS(0);
		assertEquals("0 microseconds", time.toString());

		time = Time.NANOSECONDS(1);
		assertEquals("1 nanosecond", time.toString());

		time = Time.NANOSECONDS(5);
		assertEquals("5 nanoseconds", time.toString());

		time = Time.NANOSECONDS(0);
		assertEquals("0 nanoseconds", time.toString());
	}
}
