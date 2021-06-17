package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.API;
import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import spark.Request;
import spark.Response;

public class UpdateLineCount extends PostRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession user) {
	if (!request.headers("token").equals("updatepls")) {
	    response.status(403);
	    return new JSONObject().put("success", false).toString();
	}
	final int newLineCount = body.getInt("line_count");
	final int newFileCount = body.getInt("file_count");
	ValueManager.save("lines", newLineCount);
	ValueManager.save("files", newFileCount);
	BotInformation.line_count = newLineCount;
	BotInformation.file_count = newFileCount;
	API.logWarning("New file / line count! " + newLineCount + " and " + newFileCount);
	return new JSONObject().put("success", true).put("line_count", newLineCount).put("file_count", newFileCount).toString();
    }

    @Override
    public String[] requiredBodyParameters() {
	return new String[] { "line_count", "file_count" };
    }

    @Override
    public String[] requiredParameters() {
	return new String[] { "token" };
    }

    @Override
    public String url() {
	return "updatefilelinecount";
    }
}
