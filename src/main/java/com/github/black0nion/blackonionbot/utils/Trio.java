/**
 * Author: _SIM_ Date: 26.05.2021 Copyright 2021 BlackOnion File: Trio.java
 */
package com.github.black0nion.blackonionbot.utils;

/**
 * @author _SIM_ Date: 26.05.2021 Copyright 2021 BlackOnion Class Name: Trio
 */
public class Trio<T, U, V> {

  private T first;
  private U second;
  private V third;

  public Trio(final T first, final U second, final V third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  /**
   * @return the first
   */
  public T getFirst() {
    return first;
  }

  /**
   * @param first the first to set
   */
  public void setFirst(final T first) {
    this.first = first;
  }

  /**
   * @return the second
   */
  public U getSecond() {
    return second;
  }

  /**
   * @param second the second to set
   */
  public void setSecond(final U second) {
    this.second = second;
  }

  /**
   * @return the third
   */
  public V getThird() {
    return third;
  }

  /**
   * @param third the third to set
   */
  public void setThird(final V third) {
    this.third = third;
  }
}
