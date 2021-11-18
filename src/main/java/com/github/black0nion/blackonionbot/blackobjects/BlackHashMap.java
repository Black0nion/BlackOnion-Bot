package com.github.black0nion.blackonionbot.blackobjects;

import java.util.HashMap;

@SuppressWarnings("serial")
public class BlackHashMap<K, V> extends HashMap<K, V> {
	public BlackHashMap<K, V> add(final K key, final V value) {
		super.put(key, value);
		return this;
	}
}