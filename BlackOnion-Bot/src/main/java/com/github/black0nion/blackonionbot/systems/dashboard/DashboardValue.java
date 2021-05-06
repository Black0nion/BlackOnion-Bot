package com.github.black0nion.blackonionbot.systems.dashboard;

import com.github.black0nion.blackonionbot.misc.BlackHashMap;

public class DashboardValue {
	private String databaseKey;
	private String prettyName;
	private DashboardValueType type;
	/**
	 * Key: database key
	 * Value: fancy name
	 */
	private BlackHashMap<String, String> multipleChoice = null;
	
	public DashboardValue(String databaseKey, String prettyName, DashboardValueType type) {
		this.databaseKey = databaseKey;
		this.prettyName = prettyName;
		this.type = type;
	}
	
	public DashboardValue(String databaseKey, String prettyName, DashboardValueType type, BlackHashMap<String, String> multipleChoice) {
		this.databaseKey = databaseKey;
		this.prettyName = prettyName;
		this.type = type;
		this.multipleChoice = multipleChoice;
	}

	public String getDatabaseKey() {
		return databaseKey;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public DashboardValueType getType() {
		return type;
	}
	
	/**
	 * Key: database key
	 * Value: fancy name
	 */
	public BlackHashMap<String, String> getMultipleChoice() {
		return multipleChoice;
	}
	
	@Override
	public String toString() {
		return "Database Key: " + databaseKey + " | Pretty Name: " + prettyName + " | Value Type: " + type.name();
	}
}