package com.github.black0nion.blackonionbot.influx;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.GetRequest;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.util.HashMap;

public class CommandList extends GetRequest {

    @Override
    public String handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, @Nullable BlackSession user) {
        return SlashCommandBase.commandsJson.toString();
    }

    @Override
    public String url() {
        return "commandlist";
    }
}