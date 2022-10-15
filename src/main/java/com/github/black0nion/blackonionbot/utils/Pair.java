package com.github.black0nion.blackonionbot.utils;

public class Pair<A1, A2> {

	private A1 first;
	private A2 second;

	public Pair(final A1 first, final A2 second) {
		this.first = first;
		this.second = second;
	}

	public A1 getFirst() {
		return first;
	}

	public void setFirst(final A1 first) {
		this.first = first;
	}

	/**
	 * @return the value
	 */
	public A2 getSecond() {
		return second;
	}

	public void setSecond(final A2 second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "Pair{" +
			"first=" + first +
			", second=" + second +
			'}';
	}
}
