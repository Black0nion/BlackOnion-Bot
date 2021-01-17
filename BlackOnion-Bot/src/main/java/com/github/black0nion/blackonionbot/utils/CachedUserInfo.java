package com.github.black0nion.blackonionbot.utils;

import java.util.Date;

import org.json.JSONObject;

public class CachedUserInfo {
	
	JSONObject info;
	Date refreshDate;
	String token;
	
	public CachedUserInfo(JSONObject userInfo, String token) {
		this.info = userInfo;
		this.refreshDate = new Date();
		this.token = token;
	}
	
	public JSONObject getInfo() {
		return info;
	}

	public String getToken() {
		return token;
	}
	
	public Date getRefreshDate() {
		return refreshDate;
	}
	
	public CachedUserInfo refresh(String token) {
		JSONObject userInfo = Utils.getUserInfoFromToken(token);
		
		if (!userInfo.has("id"))
			return null;
		
		this.info = userInfo;
		this.refreshDate = new Date();
		
		return this;
	}
}
