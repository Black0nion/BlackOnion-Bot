package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.impl.get.RefreshToken;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;

public class DashboardSessionInformation {
	
	private static List<DashboardSessionInformation> existingSessions = new ArrayList<>();
	
	String sessionId;
	
	@Nullable
	String accessToken;
	@Nullable
	String refreshToken;
	@Nullable
	DiscordUser user;
	
	/**
	 * Use {@link #from(String sessionId, String accessToken, String refreshToken)} for automatic caching
	 * @param sessionId
	 * @param accessToken
	 * @param refreshToken
	 */
	public DashboardSessionInformation(String sessionId, String accessToken, String refreshToken, DiscordUser user) {
		this.sessionId = sessionId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.user = user;
	}
	
	public DashboardSessionInformation(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public static DashboardSessionInformation from(String sessionId) {
		return existingSessions.stream().filter(info -> {
			return info.getSessionId().equals(sessionId);
		}).findFirst().orElseGet(() -> {
			DashboardSessionInformation info = new DashboardSessionInformation(sessionId);
			existingSessions.add(info);
			return info;
		});
	}
	
	public static DashboardSessionInformation from(String sessionId, String accessToken, String refreshToken) {
		JSONObject userInfo = Utils.getUserInfoFromToken(accessToken);
		DashboardSessionInformation info = existingSessions.stream().filter(inf -> {
			if (inf.getAccessToken() == null || inf.getRefreshToken() == null)
				return false;
			return inf.getAccessToken().equals(accessToken) && inf.getRefreshToken().equals(refreshToken) && inf.getSessionId().equals(sessionId);
		}).findFirst().orElseGet(() -> {
			DashboardSessionInformation newInfo = new DashboardSessionInformation(sessionId, accessToken, refreshToken, (userInfo != null && userInfo.has("id") ? new DiscordUser(Long.parseLong(userInfo.getString("id")), userInfo.getString("username"), userInfo.getString("avatar"), userInfo.getString("discriminator"), userInfo.getString("locale").toUpperCase(), userInfo.getBoolean("mfa_enabled")) : null));
			if (newInfo.user == null) newInfo.refreshToken();
			existingSessions.add(newInfo);
			return newInfo;
		});
		
		info.accessToken = accessToken;
		info.refreshToken = refreshToken;
		
		info.update();
		
		return info;
	}
	
	@Nullable
	public static DashboardSessionInformation get(String sessionId) {
		return existingSessions.stream().filter(session -> {return session.sessionId.equals(sessionId);}).findFirst().orElse(null);
	} 
	
	public DashboardSessionInformation refreshToken() {
		String resp = RefreshToken.refreshToken(refreshToken);
		if (resp == null) return this;
		String[] response = resp.split(":");
		accessToken = response[0];
		refreshToken = response[1];
		return this;
	}
	
	public DashboardSessionInformation update() {
		JSONObject userInfo = Utils.getUserInfoFromToken(accessToken);
		user = (userInfo != null ? new DiscordUser(Long.parseLong(userInfo.getString("id")), userInfo.getString("username"), userInfo.getString("avatar"), userInfo.getString("discriminator"), userInfo.getString("locale").toUpperCase(), userInfo.getBoolean("mfa_enabled")) : null);
		return this;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public DiscordUser getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "DashboardSessionInformation [sessionId=" + sessionId + ", accessToken=" + accessToken
				+ ", refreshToken=" + refreshToken + ", user=" + user + "]";
	}
}
