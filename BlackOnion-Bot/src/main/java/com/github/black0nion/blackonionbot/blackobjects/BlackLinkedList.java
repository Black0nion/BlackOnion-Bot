package com.github.black0nion.blackonionbot.blackobjects;

import java.util.LinkedList;

@SuppressWarnings("serial")
public class BlackLinkedList<V> extends LinkedList<V> {
    public BlackLinkedList<V> addAndGetSelf(final V value) {
	super.add(value);
	return this;
    }
}