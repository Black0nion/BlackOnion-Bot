/**
 * Author: _SIM_
 * Date: 26.05.2021
 * Copyright 2021 BlackOnion
 * File: Placeholder.java
 */
package com.github.black0nion.blackonionbot.utils;

/**
 * @author _SIM_
 * Date: 26.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: Placeholder
 */
public class Placeholder {
	
	private String placeholder;
	private String value;
	
	public Placeholder(String placeholder, String value) {
		this.placeholder = "%" + placeholder + "%";
		this.value = value;
	}
	
	public String process(String input) {
		return input.replace(placeholder, value.toString());
	}
}