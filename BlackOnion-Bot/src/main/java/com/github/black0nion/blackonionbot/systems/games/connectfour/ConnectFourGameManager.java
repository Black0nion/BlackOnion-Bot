package com.github.black0nion.blackonionbot.systems.games.connectfour;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ConnectFourGameManager {
	private static ArrayList<ConnectFour> games = new ArrayList<>();
	
	public static int Y = 8;
	public static int X = 9;
	
	/**
	 *
	 * @param messageID of the Game Message
	 * @param playerX UserID of Player X
	 * @param playerY UserID of Player Y
	 * @return The new created Game
	 */
	public static ConnectFour createGame(final MessageChannel channel, final User playerX, final User playerY) {
		final ConnectFour newGame = new ConnectFour(channel, playerX, playerY);
		games.add(newGame);
		return newGame;
	}
	
	public static void deleteGame(final ConnectFour game) {
		for (int i = 0; i < games.size(); i++)
			if (games.get(i).getMessageID() == game.getMessageID())
				games.remove(i);
	}
	
	public static boolean isIngame(final String userID) {
		for (final ConnectFour game : games)
			if (game.getPlayerX().getId().equals(userID) || game.getPlayerY().getId().equals(userID))
				return true;
		return false;
	}
	
	public static ConnectFour getGameByMessageID(final long messageID) {
		for (final ConnectFour game : games)
			if (game.getMessageID() == messageID)
				return game;
		return null;
	}
}
