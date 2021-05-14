package com.github.black0nion.blackonionbot.API.impl.get;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.GetRequest;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import spark.Request;
import spark.Response;

public class Stats implements GetRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
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
				.put("prefix", BotInformation.defaultPrefix)
				.put("os", BotInformation.osName)
				.toString();
	}

	@Override
	public String url() {
		return "stats";
	}
}
