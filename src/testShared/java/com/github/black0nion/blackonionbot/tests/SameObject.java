package com.github.black0nion.blackonionbot.tests;

import java.util.Objects;

/**
 * An object that is equal to itself.
 */
public class SameObject {

	public SameObject() {}

	private Object obj;

	public SameObject(Object obj) {
		this.obj = obj;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj
			|| (obj != null && getClass() == obj.getClass() && this.obj != null && Objects.equals(this.obj, ((SameObject) obj).obj));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
