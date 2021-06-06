package com.github.black0nion.blackonionbot.systems.dashboard.values.types;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValueType;

public class DashboardString extends DashboardValue {
	
	private final String defaultValue;
	private final String databaseKey;
	private final String fancyName;
	
	public DashboardString(final String databaseKey, final String fancyName, final String defaultValue) {
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
	public boolean save(final String key, final String value, final BlackGuild guild) {
		guild.save(key, value);
		return true;
	}

	@Override
	public String getDatabaseKey() {
		return databaseKey;
	}
}
