package com.github.black0nion.blackonionbot.misc;

import java.util.HashMap;

@SuppressWarnings("serial")
public class BlackHashMap<K, V> extends HashMap<K, V> {
	public BlackHashMap<K, V> putAndGetSelf(K key, V value) {
		super.put(key, value);
		return this;
	}
}