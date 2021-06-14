package com.github.black0nion.blackonionbot.utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.bot.Bot;

public class BlackRateLimiter {
    private final Semaphore semaphore;
    private final int maxPermits;
    private final TimeUnit timePeriod;
    private int tooMany;

    public static BlackRateLimiter create(final Time time) {
	return create(time.getTime(), time.getUnit());
    }

    public static BlackRateLimiter create(final int permits, final TimeUnit timePeriod) {
	final BlackRateLimiter limiter = new BlackRateLimiter(permits, timePeriod);
	limiter.schedulePermitReplenishment();
	return limiter;
    }

    private BlackRateLimiter(final int permits, final TimeUnit timePeriod) {
	this.semaphore = new Semaphore(permits);
	this.maxPermits = permits;
	this.timePeriod = timePeriod;
    }

    public boolean tryAcquire() {
	final boolean tryAcquire = this.semaphore.tryAcquire();
	if (tryAcquire) {
	    this.tooMany = 0;
	} else {
	    this.tooMany++;
	}
	return tryAcquire;
    }

    public void schedulePermitReplenishment() {
	Bot.scheduledExecutor.scheduleAtFixedRate(() -> {
	    this.semaphore.release(this.maxPermits - this.semaphore.availablePermits());
	}, 0, 1, this.timePeriod);
    }

    public int getTooMany() {
	return this.tooMany;
    }
}