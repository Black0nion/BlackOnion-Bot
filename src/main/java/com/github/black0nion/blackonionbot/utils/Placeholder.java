/**
 * Author: _SIM_
 * Date: 26.05.2021
 * Copyright 2021 BlackOnion
 * File: Placeholder.java
 */
package com.github.black0nion.blackonionbot.utils;

/**
 * Date: 26.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: Placeholder
 *
 * @author _SIM_
 */
public class Placeholder {

    private final String placeholder;
    private final String value;

    public Placeholder(final String placeholder, final String value) {
	this.placeholder = "%" + placeholder + "%";
	this.value = value;
    }

    public Placeholder(final String placeholder, final Object value) {
	this.placeholder = "%" + placeholder + "%";
	this.value = value.toString();
    }

    public String process(final String input) {
	return input.replace(this.placeholder, this.value);
    }
}