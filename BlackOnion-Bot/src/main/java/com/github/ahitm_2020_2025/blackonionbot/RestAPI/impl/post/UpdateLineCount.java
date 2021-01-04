package com.github.ahitm_2020_2025.blackonionbot.RestAPI.impl.post;

import org.json.JSONObject;

import com.github.ahitm_2020_2025.blackonionbot.RestAPI.PostRequest;
import com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.utils.BotUser;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;

import spark.Request;
import spark.Response;

public class UpdateLineCount implements PostRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, BotUser user) {
		final int newLineCount = body.getInt("line_count");
		final int newFileCount = body.getInt("file_count");
		ValueManager.save("lines", newLineCount);
		ValueManager.save("files", newFileCount);
		BotInformation.line_count = newLineCount;
		BotInformation.file_count = newFileCount;
		return new JSONObject()
				.put("success", true)
				.put("line_count", newLineCount)
				.put("file_count", newFileCount)
				.toString();
	}
	
	@Override
	public String[] requiredParameters() {
		return new String[] {"line_count", "file_count"};
	}

	@Override
	public String url() {
		return "updatefilelinecount";
	}
	
	@Override
	public boolean requiresAdmin() {
		return true;
	}

}
