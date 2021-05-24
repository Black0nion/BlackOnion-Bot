package com.github.black0nion.blackonionbot.systems.dashboard.values.types;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackHashMap;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValueType;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;

public class DashboardMultipleChoice extends DashboardValue {
	
	private BlackHashMap<String, String> possibleValues;
	private String databaseKey;
	private String fancyName;
	
	public DashboardMultipleChoice(String databaseKey, String fancyName, BlackHashMap<String, String> possibleValues) {
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
	public boolean save(String key, String value, String guild) {
		if (possibleValues.containsKey(value)) {			
			GuildManager.save(guild, key, value);
			return true;
		}
		return false;
	}

	@Override
	public String getDatabaseKey() {
		return databaseKey;
	}
}
