package com.github.black0nion.blackonionbot.systems.dashboard.values;

import com.github.black0nion.blackonionbot.blackobjects.BlackHashMap;
import com.github.black0nion.blackonionbot.systems.dashboard.values.types.DashboardBoolean;

public enum DashboardValueType {
	STRING(String.class), 
	BOOLEAN(DashboardBoolean.class), 
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
