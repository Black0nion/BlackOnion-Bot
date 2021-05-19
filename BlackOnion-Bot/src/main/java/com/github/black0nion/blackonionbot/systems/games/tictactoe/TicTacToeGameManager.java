package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.TextChannel;

public class TicTacToeGameManager {
	private static ArrayList<TicTacToe> games = new ArrayList<>();
	
	public static final int SIZE = 3;
	
	/**
	 *
	 * @param messageID of the Game Message
	 * @param playerX UserID of Player X
	 * @param playerY UserID of Player Y
	 * @return The new created Game
	 */
	public static TicTacToe createGame(TextChannel channel, TicTacToePlayer playerX, TicTacToePlayer playerY) {
		TicTacToe newGame = new TicTacToe(channel, playerX, playerY);
		games.add(newGame);
		return newGame;
	}
	
	public static void deleteGame(TicTacToe game) {
		games.remove(game);
	}
	
	public static boolean isIngame(String userID) {
		for (TicTacToe game : games) {
			if (game.getPlayerX().getId().equals(userID) || game.getPlayerY().getId().equals(userID))
				return true;
		}
		return false;
	}
	
	public static TicTacToe getGameByMessageID(long messageID) {
		return games.stream().filter(game -> game.getMessage().getIdLong() == messageID).findFirst().orElse(null);
	}
}