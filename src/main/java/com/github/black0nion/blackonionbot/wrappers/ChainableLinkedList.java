package com.github.black0nion.blackonionbot.wrappers;

import java.util.LinkedList;

public class ChainableLinkedList<V> extends LinkedList<V> {
	public ChainableLinkedList<V> addAndGetSelf(final V value) {
		super.add(value);
		return this;
	}
}