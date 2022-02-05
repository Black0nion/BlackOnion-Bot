/**
 *
 */
package com.github.black0nion.blackonionbot.utils;

/**
 * @author _SIM_
 */
public class BlackIncrementor {

    long count;

    public BlackIncrementor() {
	count = 0;
    }

    public void increment() {
	count++;
    }

	/**
	 * Useful for streams
	 */
	public void increment(Object dummy) {
	increment();
	}

    /**
     * @return the count
     */
    public long getCount() {
	return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(final int count) {
	this.count = count;
    }

    public void reset() {
	count = 0;
    }

    @Override
    public String toString() {
	return String.valueOf(count);
    }
}