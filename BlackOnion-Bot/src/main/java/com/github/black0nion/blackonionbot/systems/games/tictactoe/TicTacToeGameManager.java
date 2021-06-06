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
	public static TicTacToe createGame(final TextChannel channel, final TicTacToePlayer playerX, final TicTacToePlayer playerY) {
		final TicTacToe newGame = new TicTacToe(channel, playerX, playerY);
		games.add(newGame);
		return newGame;
	}
	
	public static void deleteGame(final TicTacToe game) {
		games.remove(game);
	}
	
	public static boolean isIngame(final String userID) {
		for (final TicTacToe game : games)
			if (game.getPlayerX().getId().equals(userID) || game.getPlayerY().getId().equals(userID))
				return true;
		return false;
	}
	
	public static TicTacToe getGameByMessageID(final long messageID) {
		return games.stream().filter(game -> game.getMessage().getIdLong() == messageID).findFirst().orElse(null);
	}
}
