package com.github.black0nion.blackonionbot.systems.dashboard.values.types;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValueType;

public class DashboardBoolean extends DashboardValue {
	
	private boolean defaultValue;
	private String databaseKey;
	private String fancyName;
	
	public DashboardBoolean(String databaseKey, String fancyName, boolean defaultValue) {
		this.defaultValue = defaultValue;
		this.databaseKey = databaseKey;
		this.fancyName = fancyName;
	}

	@Override
	public DashboardValueType getValueType() {
		return DashboardValueType.BOOLEAN;
	}

	@Override
	public JSONObject toJSON() {
		return new JSONObject().put("defaultValue", defaultValue).put("databaseKey", databaseKey).put("fancyName", fancyName);
	}

	@Override
	public boolean save(String key, String value, BlackGuild guild) {
		boolean result;
		if (value.equals("true")) result = true;
		else if (value.equals("false")) result = false;
		else return false;
		guild.save(key, result);
		return true;
	}

	@Override
	public String getDatabaseKey() {
		return databaseKey;
	}
}
