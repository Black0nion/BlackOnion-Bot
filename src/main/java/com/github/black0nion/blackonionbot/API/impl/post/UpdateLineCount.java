package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import com.github.black0nion.blackonionbot.systems.logging.StatisticsManager;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.API;
import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.bot.BotInformation;

import spark.Request;
import spark.Response;

public class UpdateLineCount extends PostRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession user) {
	if (!request.headers("t0k3n").equals("1irS$RYZY4D|?aNyCDIsMUb8z5fk|h5kfvGJ91|!sXd?pTm!Z$idsrUw&#9|HI5sDFWLn")) {
	    response.status(401);
	    return "";
	}
	final int newLineCount = body.getInt("line_count");
	final int newFileCount = body.getInt("file_count");
	StatisticsManager.setLineCount(newLineCount);
	StatisticsManager.setFileCount(newFileCount);
	BotInformation.LINE_COUNT = newLineCount;
	BotInformation.FILE_COUNT = newFileCount;
	API.logWarning("New file / line count! " + newLineCount + " and " + newFileCount);
	return new JSONObject().put("line_count", newLineCount).put("file_count", newFileCount).toString();
    }

    @Override
    public String[] requiredBodyParameters() {
	return new String[] { "line_count", "file_count" };
    }

    @Override
    public String[] requiredParameters() {
	return new String[] { "t0k3n" };
    }

    @Override
    public boolean requiresLogin() {
	return false;
    }

    @Override
    public String url() {
	return "updatefilelinecount";
    }
}