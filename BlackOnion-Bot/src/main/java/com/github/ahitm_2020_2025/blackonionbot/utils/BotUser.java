package com.github.ahitm_2020_2025.blackonionbot.utils;

import com.github.ahitm_2020_2025.blackonionbot.enums.BotRole;

public class BotUser {
	
	private String name;
	private String passsword;
	private BotRole role;
	private String email;
	private String originalName;
	
	public BotUser(String name, String password, BotRole role) {
		this(name, null, password, role);
	}
	
	public BotUser(String name, String email, String password, BotRole role) {
		this.name = Utils.hashSHA256(name);
		this.originalName = name;
		this.email = email == null ? null : Utils.hashSHA256(email);
		this.passsword = Utils.hashSHA256(password);
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public String getPasssword() {
		return passsword;
	}

	public BotRole getRole() {
		return role;
	}
	
	public boolean isAdmin() {
		return role == BotRole.ADMIN ? true : false;
	}

	public String getEmail() {
		return email;
	}
	
	public String getOriginalName() {
		return originalName;
	}
	
}
