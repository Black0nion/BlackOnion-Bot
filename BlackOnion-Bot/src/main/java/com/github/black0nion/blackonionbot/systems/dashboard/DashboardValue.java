package com.github.black0nion.blackonionbot.systems.dashboard;

public class DashboardValue {
	private String databaseKey;
	private String prettyName;
	private DashboardValueType type;
	
	public DashboardValue(String databaseKey, String prettyName, DashboardValueType type) {
		this.databaseKey = databaseKey;
		this.prettyName = prettyName;
		this.type = type;
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
	
	@Override
	public String toString() {
		return "Database Key: " + databaseKey + " | Pretty Name: " + prettyName + " | Value Type: " + type.name();
	}
}