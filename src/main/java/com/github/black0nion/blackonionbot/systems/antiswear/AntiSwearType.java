/**
 * Author: _SIM_
 * Date: 25.05.2021
 * Copyright 2021 BlackOnion
 * File: AntiSwearType.java
 */
package com.github.black0nion.blackonionbot.systems.antiswear;

/**
 * @author _SIM_
 * Date: 25.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: AntiSwearType
 */
public enum AntiSwearType {
	DELETE("Deletes the message, never to be seen again."),
	REPLACE("Removes the profanity from the message with stars and resends the message without it."),
	OFF;
	
	private String description;
	
	private AntiSwearType(final String description) {
		this.description = description;
	}
	
	private AntiSwearType() {
		
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AntiSwearType parse(final String input) {
		if (input == null || input == "") return null;
		return valueOf(input.toUpperCase());
	}
}