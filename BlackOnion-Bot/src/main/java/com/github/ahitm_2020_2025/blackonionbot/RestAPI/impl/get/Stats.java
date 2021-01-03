package com.github.ahitm_2020_2025.blackonionbot.RestAPI.impl.get;

import static com.github.ahitm_2020_2025.blackonionbot.BotInformation.cpuMhz;
import static com.github.ahitm_2020_2025.blackonionbot.BotInformation.cpuName;
import static com.github.ahitm_2020_2025.blackonionbot.BotInformation.file_count;
import static com.github.ahitm_2020_2025.blackonionbot.BotInformation.line_count;
import static com.github.ahitm_2020_2025.blackonionbot.BotInformation.os;
import static com.github.ahitm_2020_2025.blackonionbot.BotInformation.osBean;
import static com.github.ahitm_2020_2025.blackonionbot.BotInformation.prefix;

import org.json.JSONObject;

import com.github.ahitm_2020_2025.blackonionbot.RestAPI.GetRequest;
import com.github.ahitm_2020_2025.blackonionbot.utils.BotUser;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import spark.Request;
import spark.Response;

public class Stats implements GetRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, BotUser user) {
		response.type("application/json");
	    return new JSONObject()
	    		.put("success", true)
	    		.put("code_stats", new JSONObject()
						.put("line_count", line_count)
						.put("file_count", file_count))
	    		.put("message_stats", new JSONObject()
	    				.put("messages_sent", ValueManager.getInt("messagesSent"))
	    				.put("commands_executed", ValueManager.getInt("commandsExecuted")))
	    		.put("cpu", new JSONObject()
						.put("cpu_name", cpuName)
						.put("cpu_cores", osBean.getAvailableProcessors())
						.put("cpu_speed",  cpuMhz))
				.put("prefix", prefix)
				.put("os", os.name())
				.toString();
	}

	@Override
	public String url() {
		return "stats";
	}
}
