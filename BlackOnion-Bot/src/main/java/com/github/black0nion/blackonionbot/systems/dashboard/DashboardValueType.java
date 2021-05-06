package com.github.black0nion.blackonionbot.systems.dashboard;

import com.github.black0nion.blackonionbot.misc.BlackHashMap;

public enum DashboardValueType {
	STRING(String.class), 
	BOOLEAN(boolean.class), 
	MULTIPLE_CHOICE(BlackHashMap.class), 
	NUMBER(double.class);
	
	private final Class<?> clazz;
	
	DashboardValueType(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
}
