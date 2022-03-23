package com.github.black0nion.blackonionbot.wrappers;

import java.util.ArrayList;

public class ChainableArrayList<V> extends ArrayList<V> {
	public ChainableArrayList<V> addAndGetSelf(final V value) {
		super.add(value);
		return this;
	}
}