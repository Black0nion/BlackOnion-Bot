package com.github.black0nion.blackonionbot.utils;

import java.io.File;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class Utils {
	public static String removeMarkdown(String text) {
		return text.replace("_", "\\_").replace("*", "\\*");
	}

	//TODO: overarbeit
	public static CharSequence removeFirstArg(String[] args) {
		String str = "";
		ArrayList<String> argsArray = new ArrayList<>(Arrays.asList(args));
		argsArray.remove(0);
		for (String s : argsArray) {
			str += s + " ";
		}
		return str;
	}
	
	public static String round(String decimal, double number) {
		DecimalFormat df = new DecimalFormat(decimal);
		df.setRoundingMode(RoundingMode.CEILING);
	    return df.format(number);
	}
	
	public static boolean compareSHA256(String hashed, String unhashed) {
		String sha256hex = Hashing.sha256()
				  .hashString(unhashed, StandardCharsets.UTF_8)
				  .toString();
		return hashed.equals(sha256hex);
	}
	
	public static String hashSHA256(String input) {
		return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
	}
	
	public static String getCountryFromCode(String code) {
		return new JSONObject(FileUtils.readFromFile(new File("resources/countrycodes.json"))).getString(code);
	}
	
	public static String getUserIdFromCode(String code) {
		JSONObject obj = new JSONObject(getTokenFromCode(code).getBody());
		if (obj.has("access_token")) {
			JSONObject userInfo = new JSONObject(getUserInfoFromToken(obj.getString("access_token")).getBody());
			if (userInfo.has("id"))
				return userInfo.getString("id");
			else
				return null;
		} else 
			return null;
	}
	
	public static HttpResponse<String> getUserInfoFromToken(String token) {
		try {
			Unirest.setTimeouts(0, 0);
			return Unirest.get("https://discord.com/api/users/@me")
			  .header("Authorization", "Bearer " + token)
			  .asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HttpResponse<String> getTokenFromCode(String code) {
		ArrayList<String> discordAuthSettings = FileUtils.readArrayListFromFile("discordauthsettings");
		if (discordAuthSettings.size() < 3) {
			return null;
		}
		try {
			Unirest.setTimeouts(0, 0);
			return Unirest.post("https://discord.com/api/oauth2/token")
					  .header("Content-Type", "application/x-www-form-urlencoded")
					  .field("code", code)
					  .field("client_id", discordAuthSettings.get(0))
					  .field("client_secret", discordAuthSettings.get(1))
					  .field("grant_type", "authorization_code")
					  .field("redirect_uri", discordAuthSettings.get(2))
					  .field("scope", "identify")
					  .asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
