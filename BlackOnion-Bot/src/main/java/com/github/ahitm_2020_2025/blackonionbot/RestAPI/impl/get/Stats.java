package com.github.ahitm_2020_2025.blackonionbot.RestAPI.impl.get;

import org.json.JSONObject;

import com.github.ahitm_2020_2025.blackonionbot.RestAPI.GetRequest;
import com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation;
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
						.put("line_count", BotInformation.line_count)
						.put("file_count", BotInformation.file_count))
	    		.put("message_stats", new JSONObject()
	    				.put("messages_sent", ValueManager.getInt("messagesSent"))
	    				.put("commands_executed", ValueManager.getInt("commandsExecuted")))
	    		.put("cpu", new JSONObject()
						.put("cpu_name", BotInformation.cpuName)
						.put("cpu_cores", BotInformation.osBean.getAvailableProcessors())
						.put("cpu_speed",  BotInformation.cpuMhz))
				.put("prefix", BotInformation.prefix)
				.put("os", BotInformation.osName)
				.toString();
	}

	@Override
	public String url() {
		return "stats";
	}
}
