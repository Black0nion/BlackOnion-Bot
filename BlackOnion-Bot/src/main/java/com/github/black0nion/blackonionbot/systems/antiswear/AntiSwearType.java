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
	REMOVE("Removes the profanity from the message and resends the message without it."),
	OFF;
	
	private String description;
	
	private AntiSwearType(String description) {
		this.description = description;
	}
	
	private AntiSwearType() {
		
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AntiSwearType parse(String input) {
		if (input == null || input == "") return null;
		return valueOf(input.toUpperCase());
	}
}