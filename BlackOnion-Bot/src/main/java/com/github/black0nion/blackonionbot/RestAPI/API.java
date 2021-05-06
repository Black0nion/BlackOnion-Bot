package com.github.black0nion.blackonionbot.RestAPI;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.dashboard.DashboardSessionInformation;
import com.github.black0nion.blackonionbot.systems.dashboard.SessionManager;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;

import spark.Spark;

public class API {

	static ArrayList<PostRequest> postRequests = new ArrayList<>();
	static ArrayList<GetRequest> getRequests = new ArrayList<>();

	public API() {
		
		//Spark.secure("files/keystore.jks", "ahitm20202025", null, null);
		Spark.port(187);
		Reflections reflections = new Reflections(API.class.getPackage().getName());
		//-----------------Get Requests-----------------
		Set<Class<? extends GetRequest>> getRequestClasses = reflections.getSubTypesOf(GetRequest.class);

		for (Class<?> req : getRequestClasses) {
			try { getRequests.add((GetRequest) req.getConstructor().newInstance()); } catch (Exception e) { e.printStackTrace(); }
		}
		//----------------Post Requests-----------------
		Set<Class<? extends PostRequest>> postRequestClasses = reflections.getSubTypesOf(PostRequest.class);

		for (Class<?> req : postRequestClasses) {
			try { postRequests.add((PostRequest) req.getConstructor().newInstance()); } catch (Exception e) { e.printStackTrace(); }
		}
		//----------------------------------------------
		
		//Error handling
		Spark.internalServerError((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.type("application/json");
			response.status(500);
			return new JSONObject().put("success", false).put("reason", 500).toString();
		});
		
		Spark.notFound((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
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
					response.header("Access-Control-Allow-Origin", "*");
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
					
					for (String s : req.requiredParameters()) {
						if (!body.has(s)) {
							response.status(400);
							return new JSONObject().put("success", false).put("reason", 400).toString();
						}
					}
					
					DashboardSessionInformation information = SessionManager.generateSession(request.session());
					DiscordUser user = information != null ? information.getUser() : null;
					
					if (req.requiresLogin() && user == null) {
						response.status(401);
						return new JSONObject().put("success", false).put("reason", 401).toString();
					}
					
					return req.handle(request, response, body, user);
				} catch (Exception e) {
					API.logInfo("Answered malformed POST request (Path: " + url + ") from: " + request.ip());
					e.printStackTrace();
					response.status(400);
					response.type("application/json");
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
					response.header("Access-Control-Allow-Origin", "*");
					JSONObject body = new JSONObject();
					if (req.requiredParameters().length != 0)
						body = new JSONObject(request.body());
					JSONObject headers = new JSONObject();
					request.headers().forEach(head -> {
						headers.put(head, request.headers(head));
					});
					API.logInfo("Answered GET request (Path: " + url + ") from: " + request.ip() + " with header: " + body.toString());

					if (req.isJson())
						response.type("application/json");
					
					String token = null;
					JSONObject userInfo = null;
					if (req.requiresLogin()) {
						if (!headers.has("token")) {
							response.status(401);
							return new JSONObject().put("success", false).put("reason", 401).toString();
						}
						
						token = headers.getString("token");
						
						if (!Utils.isDiscordUser(token)) {
							response.status(401);
							return new JSONObject().put("success", false).put("reason", 401).toString();
						}
						if (req.requiresAdmin() && !Utils.isAdmin(token)) {
							response.status(403);
							return new JSONObject().put("success", false).put("reason", 403).toString();
						}
						
						userInfo = Utils.getUserInfoFromToken(token);
					}
					
					for (String s : req.requiredParameters()) {
						if (!body.has(s)) {
							response.status(400);
							return new JSONObject().put("success", false).put("reason", 400).toString();
						}
					}
					
					DiscordUser user = (userInfo != null ? new DiscordUser(Long.parseLong(userInfo.getString("id")), userInfo.getString("username"), userInfo.getString("avatar"), userInfo.getString("discriminator"), userInfo.getString("locale").toUpperCase(), userInfo.getBoolean("mfa_enabled")) : null);
					return req.handle(request, response, null, user);
				} catch (JSONException e) {
					response.status(400);
					response.type("application/json");
					return new JSONObject().put("success", false).put("reason", 400).put("detailedReason", "jsonException: " + e.getMessage()).toString();
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
