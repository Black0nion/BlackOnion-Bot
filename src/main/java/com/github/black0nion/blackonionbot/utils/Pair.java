/**
 * Author: _SIM_ Date: 26.05.2021 Copyright 2021 BlackOnion File: Pair.java
 */
package com.github.black0nion.blackonionbot.utils;

/**
 * @author _SIM_ Date: 26.05.2021 Copyright 2021 BlackOnion Class Name: Pair
 */
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
