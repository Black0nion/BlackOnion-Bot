package com.github.black0nion.blackonionbot.utils;

import java.util.concurrent.TimeUnit;

public class Time {

	private final TimeUnit unit;
	private final int time;

	private Time(final TimeUnit unit, final int time) {
		this.unit = unit;
		this.time = time;
	}

	public static Time DAYS(final int time) {
		return new Time(TimeUnit.DAYS, time);
	}

	public static Time HOURS(final int time) {
		return new Time(TimeUnit.HOURS, time);
	}

	public static Time MINUTES(final int time) {
		return new Time(TimeUnit.MINUTES, time);
	}

	public static Time SECONDS(final int time) {
		return new Time(TimeUnit.SECONDS, time);
	}

	public static Time MILLISECONDS(final int time) {
		return new Time(TimeUnit.MILLISECONDS, time);
	}

	public static Time MICROSECONDS(final int time) {
		return new Time(TimeUnit.MICROSECONDS, time);
	}

	public static Time NANOSECONDS(final int time) {
		return new Time(TimeUnit.NANOSECONDS, time);
	}

	public int getTime() {
		return this.time;
	}

	public TimeUnit getUnit() {
		return this.unit;
	}
}