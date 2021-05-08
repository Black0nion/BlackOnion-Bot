package com.github.black0nion.blackonionbot.systems.dashboard.values;

import com.github.black0nion.blackonionbot.misc.BlackHashMap;

public enum DashboardValueType {
	STRING(String.class), 
	BOOLEAN(Boolean.class), 
	MULTIPLE_CHOICE(BlackHashMap.class), 
	DECIMAL_NUMBER(Double.class),
	NUMBER(Integer.class);
	
	private final Class<?> clazz;
	
	DashboardValueType(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
}
