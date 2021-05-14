package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.bot.Bot;

import net.dv8tion.jda.api.entities.User;

public class TicTacToePlayer {
	public User user = null;
	
	public TicTacToePlayer() {
	}
	
	public TicTacToePlayer(User user) {
		this.user = user;
	}
	
	@Nullable
	public User getUser() {
		return user;
	}
	
	public String getId() {
		return user != null ? user.getId() : "bot";
	}
	
	public String getName() {
		return user != null ? user.getName() : "Bot";
	}
	
	public String getAsMention() {
		return user != null ? user.getAsMention() : Bot.jda.getSelfUser().getAsMention();
	}
	
	public boolean isBot() {
		return user == null;
	}
}