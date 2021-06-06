package com.github.black0nion.blackonionbot.systems.dashboard.values.types;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackHashMap;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValueType;

public class DashboardMultipleChoice extends DashboardValue {
	
	private final BlackHashMap<String, String> possibleValues;
	private final String databaseKey;
	private final String fancyName;
	
	public DashboardMultipleChoice(final String databaseKey, final String fancyName, final BlackHashMap<String, String> possibleValues) {
		this.possibleValues = possibleValues;
		this.databaseKey = databaseKey;
		this.fancyName = fancyName;
	}

	@Override
	public DashboardValueType getValueType() {
		return DashboardValueType.MULTIPLE_CHOICE;
	}

	@Override
	public JSONObject toJSON() {
		return new JSONObject().put("possibleValues", possibleValues).put("databaseKey", databaseKey).put("fancyName", fancyName);
	}

	@Override
	public boolean save(final String key, final String value, final BlackGuild guild) {
		if (possibleValues.containsKey(value)) {			
			guild.save(key, value);
			return true;
		}
		return false;
	}

	@Override
	public String getDatabaseKey() {
		return databaseKey;
	}
}
