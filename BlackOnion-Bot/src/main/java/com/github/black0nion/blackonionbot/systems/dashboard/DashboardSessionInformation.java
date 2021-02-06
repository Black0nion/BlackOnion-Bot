package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.impl.get.RefreshToken;
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
	 * Use {@link #from(String accessToken, String refreshToken)} for automatic caching
	 * @param accessToken
	 * @param refreshToken
	 */
	public DashboardSessionInformation(String accessToken, String refreshToken, DiscordUser user) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.user = user;
	}
	
	public static DashboardSessionInformation from(String accessToken, String refreshToken) {
		JSONObject userInfo = Utils.getUserInfoFromToken(accessToken);
		DiscordUser user = (userInfo != null ? new DiscordUser(Long.parseLong(userInfo.getString("id")), userInfo.getString("username"), userInfo.getString("avatar"), userInfo.getString("discriminator"), userInfo.getString("locale").toUpperCase(), userInfo.getBoolean("mfa_enabled")) : null);
		return existingSessions.stream().filter(info -> {
			return info.getAccessToken().equals(accessToken) && info.getRefreshToken().equals(refreshToken);
		}).findFirst().orElse(new DashboardSessionInformation(accessToken, refreshToken, user));
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
}
