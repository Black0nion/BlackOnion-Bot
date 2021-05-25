package com.github.black0nion.blackonionbot.systems.dashboard.values.types;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValueType;

public class DashboardString extends DashboardValue {
	
	private String defaultValue;
	private String databaseKey;
	private String fancyName;
	
	public DashboardString(String databaseKey, String fancyName, String defaultValue) {
		this.defaultValue = defaultValue;
		this.databaseKey = databaseKey;
		this.fancyName = fancyName;
	}

	@Override
	public DashboardValueType getValueType() {
		return DashboardValueType.STRING;
	}

	@Override
	public JSONObject toJSON() {
		return new JSONObject().put("defaultValue", defaultValue).put("databaseKey", databaseKey).put("fancyName", fancyName);
	}

	@Override
	public boolean save(String key, String value, BlackGuild guild) {
		guild.save(key, value);
		return true;
	}

	@Override
	public String getDatabaseKey() {
		return databaseKey;
	}
}
