package com.github.ahitm_2020_2025.blackonionbot.RestAPI;

import static spark.Spark.post;
import static spark.Spark.get;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.ahitm_2020_2025.blackonionbot.Logger;
import com.github.ahitm_2020_2025.blackonionbot.RestAPI.impl.get.*;
import com.github.ahitm_2020_2025.blackonionbot.RestAPI.impl.post.*;
import com.github.ahitm_2020_2025.blackonionbot.bot.BotSecrets;
import com.github.ahitm_2020_2025.blackonionbot.enums.LogOrigin;
import com.github.ahitm_2020_2025.blackonionbot.utils.BotUser;
import spark.Spark;

public class API {

	static ArrayList<PostRequest> postRequests = new ArrayList<>();
	static ArrayList<GetRequest> getRequests = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public API() {
		
		//Spark.secure("files/keystore.jks", "ahitm20202025", null, null);
		Spark.port(187);
		//-----------------Get Requests-----------------
		getRequests.add(new Stats());
		getRequests.add(new Paths());
		//----------------Post Requests-----------------
		postRequests.add(new ChangePrefix());
		postRequests.add(new Activity());
		//----------------------------------------------
		
		
		//Error handling
		Spark.internalServerError((request, response) -> {
			response.type("application/json");
			response.status(500);
			return new JSONObject().put("success", false).put("reason", 500).toString();
		});
		
		Spark.notFound((request, response) -> {
			response.type("application/json");
			response.status(404);
			return new JSONObject().put("success", false).put("reason", 404).toString();
		});
		
		//----------------Post Requests-----------------
		for (PostRequest req : postRequests) {
			if (req.url() == null) {
				API.logError("Path is null: " + req.getClass().getName());
				continue;
			}
			String url = "/api/" + req.url();
			post(url, (request, response) -> {
				try {
					JSONObject body = new JSONObject();
					if (req.requiredParameters().length != 0)
						body = new JSONObject(request.body());
					JSONObject headers = new JSONObject();
					request.headers().forEach(head -> {
						headers.put(head, request.headers(head));
					});
					API.logInfo("Answered POST request (Path: " + url + ") from: " + request.ip() + " with header: "
							+ body.toString());

					response.type("text/plain");
					if (req.isJson())
						response.type("application/json");
					
					String password = null;
					String username = null;
					if (req.requiresLogin()) {
						if (!headers.has("username") || !headers.has("password")) {
							response.status(401);
							return new JSONObject().put("success", false).put("reason", 401).toString();
						}

						password = headers.getString("password");
						username = headers.getString("username");
						
						if (!BotSecrets.credentialsRight(username, password)) {
							response.status(401);
							return new JSONObject().put("success", false).put("reason", 401).toString();
						}

						if (req.requiresAdmin()
								&& !BotSecrets.isAdmin(username, password)) {
							response.status(403);
							return new JSONObject().put("success", false).put("reason", 403).toString();
						}
					}
					
					for (String s : req.requiredParameters()) {
						if (!body.has(s)) {
							response.status(400);
							return new JSONObject().put("success", false).put("reason", 400).toString();
						}
					}
					
					BotUser user = null;
					if (username != null && password != null) {
						user = BotSecrets.getUserByCredentials(username, password);
					}
					
					return req.handle(request, response, body, user);
				} catch (JSONException e) {
					API.logInfo("Answered malformed POST request (Path: " + url + ") from: " + request.ip());
					response.status(400);
					return new JSONObject().put("success", false).put("reason", 400).put("detailedReason", "jsonException").toString();
				}
			});
		}
		//----------------------------------------------
		//-----------------Get Requests-----------------

		for (GetRequest req : getRequests) {
			if (req.url() == null) {
				API.logError("Path is null: " + req.getClass().getName());
				continue;
			}
			String url = "/api/" + req.url();
			get(url, (request, response) -> {
				try {
					JSONObject body = new JSONObject();
					if (req.requiredParameters().length != 0)
						body = new JSONObject(request.body());
					JSONObject headers = new JSONObject();
					request.headers().forEach(head -> {
						headers.put(head, request.headers(head));
					});
					API.logInfo("Answered GET request (Path: " + url + ") from: " + request.ip() + " with header: "
							+ body.toString());

					if (req.isJson())
						response.type("application/json");
					
					String password = null;
					String username = null;
					if (req.requiresLogin()) {
						if (!headers.has("username") || !headers.has("password")) {
							response.status(401);
							return new JSONObject().put("success", false).put("reason", 401).toString();
						}
						
						password = headers.getString("password");
						username = headers.getString("username");

						if (!BotSecrets.credentialsRight(username, password)) {
							response.status(401);
							return new JSONObject().put("success", false).put("reason", 401).toString();
						}

						if (req.requiresAdmin()
								&& !BotSecrets.isAdmin(username, password)) {
							response.status(403);
							return new JSONObject().put("success", false).put("reason", 403).toString();
						}
					}
					
					for (String s : req.requiredParameters()) {
						if (!body.has(s)) {
							response.status(400);
							return new JSONObject().put("success", false).put("reason", 400).toString();
						}
					}
					
					BotUser user = null;
					if (username != null && password != null) {
						user = BotSecrets.getUserByCredentials(username, password);
					}
					
					return req.handle(request, response, body ,user);
				} catch (JSONException e) {
					response.status(400);
					return new JSONObject().put("success", false).put("reason", 400).put("detailedReason", "jsonException").toString();
				}
			});
		}
		//----------------------------------------------
	}

	public static void logInfo(String logInput) {
		Logger.logInfo(logInput, LogOrigin.API);
	}

	public static void logWarning(String logInput) {
		Logger.logWarning(logInput, LogOrigin.API);
	}

	public static void logError(String logInput) {
		Logger.logError(logInput, LogOrigin.API);
	}
}
