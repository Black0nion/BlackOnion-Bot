package com.github.black0nion.blackonionbot.misc;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class BlackArrayList<V> extends ArrayList<V> {
	public BlackArrayList<V> addAndGetSelf(V value) {
		super.add(value);
		return this;
	}
}