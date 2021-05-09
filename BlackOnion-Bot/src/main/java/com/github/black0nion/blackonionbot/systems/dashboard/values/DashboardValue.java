package com.github.black0nion.blackonionbot.systems.dashboard.values;

import org.json.JSONObject;

public abstract class DashboardValue {
	public abstract JSONObject toJSON();
	
	public abstract void save(String key, String value);
}