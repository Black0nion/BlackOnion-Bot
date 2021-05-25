package com.github.black0nion.blackonionbot.systems.dashboard.values;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;

public abstract class DashboardValue {
	public abstract DashboardValueType getValueType();
	
	public abstract JSONObject toJSON();
	
	/**
	 * @param key
	 * @param value
	 * @param guild
	 * @return could be saved?
	 */
	public abstract boolean save(String key, String value, BlackGuild guild);
	
	public abstract String getDatabaseKey();
}