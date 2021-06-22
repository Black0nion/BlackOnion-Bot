package com.github.black0nion.blackonionbot.blackobjects;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class BlackLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    public BlackLinkedHashMap<K, V> add(final K key, final V value) {
	super.put(key, value);
	return this;
    }
}