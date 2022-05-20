package com.github.black0nion.blackonionbot.utils;

public class Pair<K, V> {

	private K key;
	private V value;

	public Pair(final K key, final V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(final K key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}

	public void setValue(final V value) {
		this.value = value;
	}
}