package com.github.black0nion.blackonionbot.utils;

@SuppressWarnings("unused")
public class Quadruple<T1, T2, T3, T4> {

	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;

	public Quadruple(final T1 first, final T2 second, final T3 third, final T4 fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}

	public T1 getFirst() {
		return this.first;
	}

	public void setFirst(final T1 first) {
		this.first = first;
	}

	public T2 getSecond() {
		return this.second;
	}

	public void setSecond(final T2 second) {
		this.second = second;
	}

	public T3 getThird() {
		return this.third;
	}

	public void setThird(final T3 third) {
		this.third = third;
	}

	public T4 getFourth() {
		return this.fourth;
	}

	public void setFourth(final T4 fourth) {
		this.fourth = fourth;
	}
}
