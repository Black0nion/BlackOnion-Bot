package com.github.black0nion.blackonionbot.utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BlackRateLimiter {

	private final Semaphore semaphore;
	private final int maxPermits;
	private final TimeUnit timePeriod;
	/**
	 * How many illegal requests got tried
	 */
	private int tooMany;
	private long lastRestock;

	public static BlackRateLimiter create(final Time time) {
		return create(time.time(), time.unit());
	}

	public static BlackRateLimiter create(final int permits, final TimeUnit timePeriod) {
		return new BlackRateLimiter(permits, timePeriod);
	}

	private BlackRateLimiter(final int permits, final TimeUnit timePeriod) {
		this.semaphore = new Semaphore(permits);
		this.maxPermits = permits;
		this.timePeriod = timePeriod;
		this.lastRestock = System.currentTimeMillis();
	}

	public boolean tryAcquire() {
		this.tryRestock();
		final boolean tryAcquire = this.semaphore.tryAcquire();
		if (tryAcquire) {
			this.tooMany = 0;
		} else {
			this.tooMany++;
		}
		return tryAcquire;
	}

	public void tryRestock() {
		if ((System.currentTimeMillis() - this.lastRestock) > TimeUnit.MILLISECONDS.convert(1, this.timePeriod)) {
			this.lastRestock = System.currentTimeMillis();
			this.semaphore.release(this.maxPermits - this.semaphore.availablePermits());
		}
	}

	/**
	 * @return How many illegal requests got tried since the last proper request
	 */
	public int getTooMany() {
		return this.tooMany;
	}
}
